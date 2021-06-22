package ru.geekbrains.notes.ui.list;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.Settings;
import ru.geekbrains.notes.SharedPref;
import ru.geekbrains.notes.note.comparator.DateCreateSorterComparator;
import ru.geekbrains.notes.note.comparator.DateEditSorterComparator;
import ru.geekbrains.notes.note.comparator.HeaderSorterComparator;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.observer.ObserverNote;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;
import ru.geekbrains.notes.ui.DatepickerFragment;
import ru.geekbrains.notes.ui.MainFragment;
import ru.geekbrains.notes.ui.item.EditNoteFragment;
import ru.geekbrains.notes.ui.item.ViewNoteFragment;

import static ru.geekbrains.notes.Constant.*;


public class ListNotesFragment extends Fragment implements ObserverNote {

    private Publisher publisher;
    private RecyclerView recyclerView;
    private List<Note> notes;
    private View viewFragmentListNotes;
    private TextView textViewEmptyListNotes;
    private RVAdapter rvAdapter;
    int currentPositionRV;

    Button buttonAddOne;
    Button button1000;

    @Override
    public void updateNote(int noteID, int typeEvent) {
        Log.v("Debug1", "ListNotesFragment updateNote noteID=" + noteID + ", typeEvent=" + typeEvent);
        if (recyclerView != null) {
            initRecyclerViewListNotes(recyclerView, noteID, typeEvent);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Debug1", "ListNotesFragment onAttach");

        if (context instanceof PublisherHolder) {
            publisher = ((PublisherHolder) context).getPublisher();
            publisher.subscribe(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Debug1", "ListNotesFragment onDetach");

        Settings settings = new Settings();
        if (getActivity() != null)
            settings = ((GlobalVariables) getActivity().getApplication()).getSettings();

        Log.v("Debug1", "ListNotesFragment onDetach currentPositionRV=" + currentPositionRV);
        settings.setCurrentPosition(currentPositionRV);

        if (getContext() != null) {
            new SharedPref(getContext()).saveSettings(settings);
            ((GlobalVariables) getActivity().getApplication()).setSettings(settings);
        }

        if (publisher != null) {
            publisher.unsubscribe(this);
        }
    }

    public List<Note> sortNotes(List<Note> notes) {
        Log.v("Debug1", "ListNotesFragment sortNotes");
        if (getActivity() != null) {
            int textSortId = ((GlobalVariables) getActivity().getApplication()).getSettings().getOrderType();
            Comparator<Note> dateSorter = new DateEditSorterComparator();
            Comparator<Note> dateCreateSorter = new DateCreateSorterComparator();
            Comparator<Note> headerSorter = new HeaderSorterComparator();
            switch (textSortId) {
                case (ORDER_BY_DATE_EDIT):
                    notes.sort(dateSorter);
                    break;
                case (ORDER_BY_DATE_EDIT_DESC):
                    notes.sort(dateSorter.reversed());
                    break;
                case (ORDER_BY_DATE_CREATE):
                    notes.sort(dateCreateSorter);
                    break;
                case (ORDER_BY_DATE_CREATE_DESC):
                    notes.sort(dateCreateSorter.reversed());
                    break;
                case (ORDER_BY_DATE_VALUE):
                    notes.sort(headerSorter);
                    break;
                case (ORDER_BY_DATE_VALUE_DESC):
                    notes.sort(headerSorter.reversed());
                    break;
            }
        }
        return notes;
    }

    public void setEmptyResultTextView(View view) {
        Log.v("Debug1", "ListNotesFragment setEmptyResultTextView view.getTag()=" + view.getTag());
        textViewEmptyListNotes = view.findViewById(R.id.textViewEmprtyListNotesRV);
        if (textViewEmptyListNotes != null) {
            if (notes.size() != 0) {
                recyclerView.setVisibility(View.VISIBLE);
                textViewEmptyListNotes.setVisibility(View.GONE);
                buttonAddOne.setVisibility(View.GONE);
                button1000.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.GONE);
                textViewEmptyListNotes.setVisibility(View.VISIBLE);
                buttonAddOne.setVisibility(View.VISIBLE);
                button1000.setVisibility(View.VISIBLE);

                Button buttonAddOne = view.findViewById(R.id.button_addFirstNote);
                buttonAddOne.setOnClickListener(v -> {
                    if (getActivity() != null) {
                        EditNoteFragment editNoteFragment = EditNoteFragment.newInstance(-1);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.add(R.id.frame_container_main, editNoteFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
                Button button1000 = view.findViewById(R.id.button_addFirst1000);
                button1000.setOnClickListener(v -> {
                    if (getActivity() != null) {
                        List<Note> notes = ((GlobalVariables) getActivity().getApplication()).getNotes();
                        int start = notes.size();
                        for (int i = start; i < COUNTDEMONOTES; i++) {
                            Date date = new Date();
                            Note note = new Note(("Заметка №" + i), (i * 2), date.toInstant().getEpochSecond(), date.toInstant().getEpochSecond());
                            notes.add(note);
                            /*try {
                                Thread.sleep(1000); //Приостанавливает поток на 1 секунду
                            } catch (Exception ignored) {

                            }*/
                        }
                        //Сохраняем заметки в глобальной переменной
                        ((GlobalVariables) getActivity().getApplication()).setNotes(notes);
                        new SharedPref(getActivity()).saveNotes(notes);
                        initRecyclerViewListNotes(recyclerView, 0, 0);
                    }
                });
            }
        }
    }

    // При создании фрагмента укажем его макет
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("Debug1", "ListNotesFragment onCreateView");
        View view = inflater.inflate(R.layout.fragment_list_notes, container, false);
        Log.v("Debug1", "ListNotesFragment onCreateView view.getTag()=" + view.getTag());
        viewFragmentListNotes = view;
        if (getActivity() != null) {
            notes = ((GlobalVariables) getActivity().getApplication()).getNotes();
        }
        textViewEmptyListNotes = view.findViewById(R.id.textViewEmprtyListNotesRV);
        buttonAddOne = view.findViewById(R.id.button_addFirstNote);
        button1000 = view.findViewById(R.id.button_addFirst1000);
        recyclerView = view.findViewById(R.id.recycler_view_lines);

        Settings settings = ((GlobalVariables) getActivity().getApplication()).getSettings();
        currentPositionRV = settings.getCurrentPosition();

        initRecyclerViewListNotes(recyclerView, -1, TYPE_EVENT_ADD_NOTE);

        return view;
    }

    private void viewNote(int noteId){
        ViewNoteFragment viewNoteFragment = null;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (getActivity() != null)
                viewNoteFragment = (ViewNoteFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.activity_container_note_view);
        } else {
            MainFragment mainFragment = null;
            if (getActivity() != null)
                mainFragment = (MainFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.frame_container_main);
            if (mainFragment != null) {
                FragmentManager childFragmentManager = mainFragment.getChildFragmentManager();
                viewNoteFragment = (ViewNoteFragment) childFragmentManager.findFragmentById(R.id.activity_container_note_view);
            }
        }
        if (viewNoteFragment == null) {
            Log.v("Debug1", "ListNotesFragment viewNote viewNoteFragment == null");
            viewNoteFragment = ViewNoteFragment.newInstance(noteId);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.add(R.id.frame_container_main, viewNoteFragment, "ViewNoteFragmentPortrait");
            //fragmentTransaction.replace(R.id.frame_container_main, viewNoteFragment, "ViewNoteFragmentPortrait");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Log.v("Debug1", "ListNotesFragment viewNote viewNoteFragment != null");
            viewNoteFragment.fillViewNote(noteId, viewNoteFragment.getViewFragment());
        }
    }

    private void editNote(int noteId){
        EditNoteFragment editNoteFragment = null;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (getActivity() != null)
                editNoteFragment = (EditNoteFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.activity_container_note_view);
        } else {
            MainFragment mainFragment = null;
            if (getActivity() != null)
                mainFragment = (MainFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.frame_container_main);
            if (mainFragment != null) {
                FragmentManager childFragmentManager = mainFragment.getChildFragmentManager();
                editNoteFragment = (EditNoteFragment) childFragmentManager.findFragmentById(R.id.activity_container_note_view);
            }
        }
        if (editNoteFragment == null) {
            Log.v("Debug1", "ListNotesFragment editNote viewNoteFragment == null");
            editNoteFragment = EditNoteFragment.newInstance(noteId);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.add(R.id.frame_container_main, editNoteFragment, "EditNoteFragmentPortrait");
            //fragmentTransaction.replace(R.id.frame_container_main, viewNoteFragment, "ViewNoteFragmentPortrait");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Log.v("Debug1", "ListNotesFragment editNote viewNoteFragment != null");
            editNoteFragment.fillEditNote(editNoteFragment.getEditFragment());
        }
    }

    private void deleteNote(int noteId){
        if (getActivity() != null && getActivity().getApplication() != null) {
            List<Note> notes = ((GlobalVariables) getActivity().getApplication()).getNotes();
            int prevID = 0;
            int position = 0;
            for (int i = 0; i < notes.size(); i++) {
                if (notes.get(i).getID() == noteId) {
                    notes.remove(i);
                    break;
                }
                prevID = notes.get(i).getID();
                position = i;
            }
            Log.v("Debug1", "ViewNoteFragment onClick button_delete prevID=" + prevID);
            ((GlobalVariables) getActivity().getApplication()).setNotes(notes);
            if (getContext() != null) {
                new SharedPref(getContext()).saveNotes(notes);
                if (publisher != null) {
                    Log.v("Debug1", "ViewNoteFragment onClick button_delete notify");
                    publisher.notify(position, TYPE_EVENT_DELETE_NOTE);
                }
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (getActivity() != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentManager.popBackStack();
                        fragmentTransaction.commit();
                    }
                }
            }
        }
    }


    private void initRecyclerViewListNotes(RecyclerView recyclerView, int noteIdForScrollPosition, int typeEvent) {
        Log.v("Debug1", "ListNotesFragment initRecyclerViewListNotes noteIdForScrollPosition=" + noteIdForScrollPosition + ", currentPositionRV=" + currentPositionRV + ", typeEvent=" + typeEvent);

        if (getActivity() != null) {
            List<Note> notes = ((GlobalVariables) getActivity().getApplication()).getNotes();
            // Эта установка служит для повышения производительности системы
            recyclerView.setHasFixedSize(true);
            setEmptyResultTextView(viewFragmentListNotes);
            // Будем работать со встроенным менеджером
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (layoutManager.findFirstVisibleItemPosition() != -1)
                        currentPositionRV = layoutManager.findFirstVisibleItemPosition();
                    Log.v("Debug1", "ListNotesFragment initRecyclerViewListNotes currentPositionRV=" + currentPositionRV);
                }
            });

            notes = sortNotes(notes);

            int scrollPosition = 0;
            Log.v("Debug1", "ListNotesFragment initRecyclerViewListNotes scrollPosition=" + scrollPosition + ", noteIdForScrollPosition=" + noteIdForScrollPosition);
            if (typeEvent == TYPE_EVENT_ADD_NOTE | typeEvent == TYPE_EVENT_EDIT_NOTE) {
                scrollPosition = ((GlobalVariables) getActivity().getApplication()).getScrollPositionByNoteId((noteIdForScrollPosition));
                Log.v("Debug1", "ListNotesFragment initRecyclerViewListNotes typeEvent == TYPE_EVENT_ADD_NOTE scrollPosition=" + scrollPosition);
            } else if (typeEvent == TYPE_EVENT_DELETE_NOTE) {
                scrollPosition = currentPositionRV;
                Log.v("Debug1", "ListNotesFragment initRecyclerViewListNotes typeEvent == TYPE_EVENT_DELETE_NOTE scrollPosition=" + scrollPosition);
            }

            layoutManager.scrollToPosition(scrollPosition);

            Settings settings = new Settings();
            if (getActivity() != null) {
                settings = ((GlobalVariables) getActivity().getApplication()).getSettings();
            }
            // Установим адаптер
            rvAdapter = new RVAdapter(notes, settings, this, getResources().getConfiguration().orientation);
            recyclerView.setAdapter(rvAdapter);

            recyclerView.setItemAnimator(new DefaultItemAnimator());

            switch (typeEvent) {
                case (TYPE_EVENT_CHANGE_SETTINGS):
                    rvAdapter.notifyDataSetChanged();
                break;
                case (TYPE_EVENT_DELETE_NOTE):
                    rvAdapter.notifyItemRemoved(((GlobalVariables) getActivity().getApplication()).getScrollPositionByNoteId((noteIdForScrollPosition)));
                    break;
                default:
                    rvAdapter.notifyItemChanged(((GlobalVariables) getActivity().getApplication()).getScrollPositionByNoteId((noteIdForScrollPosition)));
                    break;
            }

            // Установим слушателя на текст
            rvAdapter.SetOnNoteClicked((view, position) -> {
                int noteId = (int) view.getTag();
                Log.v("Debug1", "ListNotesFragment initRecyclerView onNoteClickedList noteId=" + noteId);
                viewNote(noteId);
            });
            // Установим слушателя на дату
            rvAdapter.SetOnDateClicked((view, position) -> {
                int noteId = (int) view.getTag();
                Log.v("Debug1", "ListNotesFragment initRecyclerView onDateClickedList noteId=" + noteId);
                DatepickerFragment datepickerFragment = DatepickerFragment.newInstance(noteId);
                if (getActivity() != null) {
                    FragmentTransaction fragmentTransaction;
                    fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(R.id.frame_container_main, datepickerFragment, "DatepickerFragment");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }
    }

    // вызывается после создания макета фрагмента, здесь мы проинициализируем список
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("Debug1", "ListNotesFragment onViewCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("Debug1", "ListNotesFragment onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("Debug1", "ListNotesFragment onStop");
    }

    public void onResume() {
        super.onResume();
        Log.v("Debug1", "ListNotesFragment onResume");
    }

    public void onPause() {
        super.onPause();
        Log.v("Debug1", "ListNotesFragment onPause");
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.popup, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();
        int noteId = rvAdapter.getMenuPosition();
        if (itemId == R.id.popup_view) {
            viewNote(noteId);
            return true;
        } else if (itemId == R.id.popup_edit) {
            editNote(noteId);
            return true;
        } else if (itemId == R.id.popup_delete) {
            deleteNote(noteId);
            return true;
        }
        return super.onContextItemSelected(item);
    }

}
