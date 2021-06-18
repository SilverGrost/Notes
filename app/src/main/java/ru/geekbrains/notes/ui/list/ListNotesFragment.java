package ru.geekbrains.notes.ui.list;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.SharedPref;
import ru.geekbrains.notes.note.DateSorterComparator;
import ru.geekbrains.notes.note.HeaderSorterComparator;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.observer.ObserverNote;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;
import ru.geekbrains.notes.ui.DatepickerFragment;
import ru.geekbrains.notes.ui.MainFragment;
import ru.geekbrains.notes.ui.item.EditNoteFragment;
import ru.geekbrains.notes.ui.item.ViewNoteFragment;


public class ListNotesFragment extends Fragment implements ObserverNote {

    private Publisher publisher;
    private RecyclerView recyclerView;
    private List<Note> notes;
    private View viewFragmenListNotes;
    TextView textViewEmprtyListNotes;

    Button buttonAddOne;
    Button button1000;

    @Override
    public void updateNote(int noteID) {
        Log.v("Debug1", "ListNotesFragment updateNote noteID=" + noteID);
        if (recyclerView != null) {
            initRecyclerViewListNotes(recyclerView, noteID);
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

        if (publisher != null) {
            publisher.unsubscribe(this);
        }
    }

    public List<Note> sortNotes(List<Note> notes) {
        if (getActivity() != null) {
            int textSortId = ((GlobalVariables) getActivity().getApplication()).getSortTypeId();
            Comparator<Note> dateSorter = new DateSorterComparator();
            Comparator<Note> headerSorter = new HeaderSorterComparator();
            switch (textSortId) {
                case (1):
                    notes.sort(dateSorter);
                    break;
                case (0):
                    notes.sort(dateSorter.reversed());
                    break;
                case (3):
                    notes.sort(headerSorter);
                    break;
                case (2):
                    notes.sort(headerSorter.reversed());
                    break;
            }
        }
        return notes;
    }

    public void setEmptyResultTextView(View view) {
        Log.v("Debug1", "ListNotesFragment setEmptyResultTextView view.getTag()=" + view.getTag());
        textViewEmprtyListNotes = view.findViewById(R.id.textViewEmprtyListNotesRV);

        if (textViewEmprtyListNotes != null) {
            if (notes.size() != 0) {
                recyclerView.setVisibility(View.VISIBLE);
                textViewEmprtyListNotes.setVisibility(View.GONE);
                buttonAddOne.setVisibility(View.GONE);
                button1000.setVisibility(View.GONE);

            } else {
                recyclerView.setVisibility(View.GONE);
                textViewEmprtyListNotes.setVisibility(View.VISIBLE);
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
                        for (int i = start; i < 1000; i++) {
                            Date date = new Date();
                            Note note = new Note(("Заметка №" + i), (i * 2), date.toInstant().getEpochSecond());
                            notes.add(note);
                        }
                        //Сохраняем заметки в глобальной переменной
                        ((GlobalVariables) getActivity().getApplication()).setNotes(notes);
                        new SharedPref(getActivity()).saveNotes(notes);
                        initRecyclerViewListNotes(recyclerView, 0);
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
        viewFragmenListNotes = view;

        if (getActivity() != null) {
            notes = ((GlobalVariables) getActivity().getApplication()).getNotes();
        }

        textViewEmprtyListNotes = view.findViewById(R.id.textViewEmprtyListNotesRV);
        buttonAddOne = view.findViewById(R.id.button_addFirstNote);
        button1000 = view.findViewById(R.id.button_addFirst1000);
        recyclerView = view.findViewById(R.id.recycler_view_lines);

        initRecyclerViewListNotes(recyclerView, 0);

        return view;
    }


    private void initRecyclerViewListNotes(RecyclerView recyclerView, int scrollPosition) {
        Log.v("Debug1", "ListNotesFragment initRecyclerViewListNotes scrollPosition=" + scrollPosition);

        if (getActivity() != null) {
            List<Note> notes = ((GlobalVariables) getActivity().getApplication()).getNotes();

            // Эта установка служит для повышения производительности системы
            recyclerView.setHasFixedSize(true);

            setEmptyResultTextView(viewFragmenListNotes);

            // Будем работать со встроенным менеджером
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);

            layoutManager.scrollToPosition(scrollPosition);

            //Установим размер шрифта
            String[] textSize = getResources().getStringArray(R.array.text_size);
            float textSizeFloat = 0;
            if (getActivity() != null) {
                int textSizeId = ((GlobalVariables) getActivity().getApplication()).getTextSizeId();
                textSizeFloat = Float.parseFloat(textSize[textSizeId]);
            }

            // Установим адаптер
            final ListNotesAdapter listNotesAdapter = new ListNotesAdapter(sortNotes(notes), textSizeFloat);
            recyclerView.setAdapter(listNotesAdapter);

            listNotesAdapter.notifyDataSetChanged();

            // Добавим разделитель карточек
            /*DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),  LinearLayoutManager.VERTICAL);
            itemDecoration.setDrawable(getResources().getDrawable(R.drawable.separator, null));
            recyclerView.addItemDecoration(itemDecoration);*/

            // Установим слушателя на текст
            listNotesAdapter.SetOnNoteClicked((view, position) -> {
                int noteId = (int) view.getTag();
                Log.v("Debug1", "ListNotesFragment initRecyclerView onNoteClickedList noteId=" + noteId);

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
                    Log.v("Debug1", "ListNotesFragment initRecyclerView viewNoteFragment == null");
                    viewNoteFragment = ViewNoteFragment.newInstance(noteId);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(R.id.frame_container_main, viewNoteFragment, "ViewNoteFragment");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    Log.v("Debug1", "ListNotesFragment initRecyclerView viewNoteFragment != null");
                    viewNoteFragment.fillViewNote(noteId, viewNoteFragment.getViewFragment());
                }
            });

            // Установим слушателя на дату
            listNotesAdapter.SetOnDateClicked((view, position) -> {
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

}
