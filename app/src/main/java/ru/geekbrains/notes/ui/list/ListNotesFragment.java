package ru.geekbrains.notes.ui.list;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.Settings;
import ru.geekbrains.notes.SharedPref;
import ru.geekbrains.notes.note.NotesCloudRepositoryImpl;
import ru.geekbrains.notes.note.NotesLocalRepositoryImpl;
import ru.geekbrains.notes.note.NotesRepository;
import ru.geekbrains.notes.note.comparator.DateCreateSorterComparator;
import ru.geekbrains.notes.note.comparator.DateEditSorterComparator;
import ru.geekbrains.notes.note.comparator.HeaderSorterComparator;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.observer.ObserverNote;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;
import ru.geekbrains.notes.ui.DatepickerFragment;
import ru.geekbrains.notes.ui.MainActivity;
import ru.geekbrains.notes.ui.MainFragment;
import ru.geekbrains.notes.ui.auth.AuthFragment;
import ru.geekbrains.notes.ui.item.EditNoteFragment;
import ru.geekbrains.notes.ui.item.ViewNoteFragment;
import ru.geekbrains.notes.ui.settings.SettingsFragment;

import static ru.geekbrains.notes.Constant.*;


public class ListNotesFragment extends Fragment implements ObserverNote {

    private Publisher publisher;
    private RecyclerView recyclerView;
    private List<Note> notes;
    private List<Note> notesCloud;
    private View viewFragmentListNotes;
    private TextView textViewEmptyListNotes;
    private RVAdapter rvAdapter;
    private ProgressBar progressBar;
    private boolean isLoading = false;
    int currentPositionRV;

    //private final NotesRepository localRepository = new NotesLocalRepositoryImpl(getContext());

    Button buttonAddOne;
    Button button1000;

    @Override
    public void updateNote(int noteID, int typeEvent) {
        Log.v("Debug1", "ListNotesFragment updateNote noteID=" + noteID + ", typeEvent=" + typeEvent);

        if (recyclerView != null) {
            if (typeEvent == TYPE_EVENT_CLOUD_SYNC) {
                Log.v("Debug1", "ListNotesFragment updateNote noteID=" + noteID + ", typeEvent=TYPE_EVENT_CLOUD_SYNC");
                if (getActivity() != null) {
                    cloudSync(((GlobalVariables) getActivity().getApplication()).getSettings());
                }
            } else
                initRecyclerViewListNotes(recyclerView, noteID, typeEvent);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Debug1", "ListNotesFragment onAttach");

        //getActivity().setTitle("Список заметок");
        MainActivity.setTitle(getActivity(), "Список заметок");

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

    public static List<Note> sortNotes(List<Note> notes, Activity activity) {
        Log.v("Debug1", "ListNotesFragment sortNotes");
        if (activity != null) {
            int textSortId = ((GlobalVariables) activity.getApplication()).getSettings().getOrderType();
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
                Button buttonAddDemoNotes = view.findViewById(R.id.button_addFirst1000);
                buttonAddDemoNotes.setText(getString(R.string.buttonAddDemo, COUNTDEMONOTES));

                buttonAddDemoNotes.setOnClickListener(v -> {
                    if (getActivity() != null) {
                        List<Note> notes = ((GlobalVariables) getActivity().getApplication()).getNotes();
                        int start = notes.size();

                        NotesRepository cloudRepository = null;
                        //NotesRepository localRepository = new NotesLocalRepositoryImpl(getContext());
                        if (getActivity() != null) {
                            Settings settings = ((GlobalVariables) getActivity().getApplication()).getSettings();
                            int authTypeService = settings.getAuthTypeService();
                            String userName = AuthFragment.checkCloudStatusByUserName(settings, getContext(), getActivity());
                            if (userName != null && !userName.equals("")) {
                                cloudRepository = new NotesCloudRepositoryImpl(authTypeService, userName);
                            }
                        }

                        for (int i = start; i < COUNTDEMONOTES; i++) {
                            Date date = new Date();
                            Note note = new Note(("Заметка №" + i), (i/* * 2*/), date.toInstant().getEpochSecond(), date.toInstant().getEpochSecond());

                            boolean cloudSync = false;
                            String userName;
                            if (getActivity() != null) {
                                //Получаем настройки из глобальной переменной
                                Settings settings = ((GlobalVariables) getActivity().getApplication()).getSettings();
                                userName = AuthFragment.checkCloudStatusByUserName(settings, getContext(), getActivity());
                                if (userName != null && !userName.equals("")) {
                                    cloudSync = true;
                                }
                            }

                            NotesRepository localRepository = new NotesLocalRepositoryImpl(getContext(), getActivity());

                            notes.add(note);

                            if (cloudSync) {

                                NotesRepository finalCloudRepository = cloudRepository;
                                cloudRepository.addNote(notes, note, result -> {
                                    note.setIdCloud((String) result);
                                    Log.v("Debug1", "ListNotesFragment setEmptyResultTextVie addDemo cloudRepository.addNote result=" + result);

                                    localRepository.updateNote(notes, note, result1 -> Log.v("Debug1", "ListNotesFragment setEmptyResultTextVie addDemo localRepository updateNote"));

                                    finalCloudRepository.updateNote(notes, note, result12 -> Log.v("Debug1", "ListNotesFragment setEmptyResultTextVie addDemo finalCloudRepository updateNote"));
                                });
                            }


                        }

                        //Сохраняем заметки в глобальной переменной
                        /*((GlobalVariables) getActivity().getApplication()).setNotes(notes);
                        new SharedPref(getActivity()).saveNotes(notes);*/
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

        return inflater.inflate(R.layout.fragment_list_notes, container, false);
    }

    private void cloudSync(Settings settings) {
        String userName = AuthFragment.checkCloudStatusByUserName(settings, getContext(), getActivity());
        int authTypeService = settings.getAuthTypeService();

        Log.v("Debug1", "ListNotesFragment cloudSync userName=" + userName + ", authTypeService=" + authTypeService);

        if (userName != null && !userName.equals("")) {
            NotesRepository cloudRepository = new NotesCloudRepositoryImpl(authTypeService, userName);
            cloudRepository.getNotes(result -> {
                notesCloud = result;
                if (getActivity() != null) {
                    ((GlobalVariables) getActivity().getApplication()).setNotesCloud(notesCloud);
                    Log.v("Debug1", "ListNotesFragment cloudSync notesCloud.size=" + notesCloud.size());

                    NotesRepository localRepository = new NotesLocalRepositoryImpl(getContext(), getActivity());
                    //Пройдёмся по облачным
                    for (int i = 0; i < notesCloud.size(); i++) {
                        Note noteCloud = notesCloud.get(i);
                        Note noteLocal = ((GlobalVariables) getActivity().getApplication()).getNoteByNoteId(notesCloud.get(i).getID());

                        //Если в локальных заметак нет заметки с таким id, то добавляем из облака
                        if (noteLocal.getID() == -1) {
                            //notes.add(noteCloud);
                            localRepository.addNote(notes, noteCloud, result12 -> Log.v("Debug1", "ListNotesFragment cloudSync localRepository.addNote"));
                        }
                        //Если есть, то сравниваем даты
                        else {
                            //Если дата правки в облаке больше, то обнавляем локальные из облака
                            if (noteCloud.getDateEdit() > noteLocal.getDateEdit()) {
                                //((GlobalVariables) getActivity().getApplication()).setNoteById(noteCloud.getID(), noteCloud);

                                //Обнавляем в локальном репозитории
                                localRepository.updateNote(notes, noteCloud, result1 -> {
                                    Log.v("Debug1", "ListNotesFragment cloudSync localRepository updateNote onSuccess");
                                    //((GlobalVariables) getActivity().getApplication()).setNotes(notes);

                                });


                            } else if (noteCloud.getDateEdit() < noteLocal.getDateEdit()) {
                                //Иначе обновляем облачные из локальных
                                ((GlobalVariables) getActivity().getApplication()).updateNoteCloudById(noteLocal.getID(), noteLocal);

                                cloudRepository.updateNote(null, noteLocal, result1 -> Log.v("Debug1", "ListNotesFragment cloudSync cloudRepository.updateNote Обновил в облаке result1=" + result1));
                            }
                        }
                    }

                    notesCloud = ((GlobalVariables) getActivity().getApplication()).getNotesCloud();
                    notes = ((GlobalVariables) getActivity().getApplication()).getNotes();

                    /*((GlobalVariables) getActivity().getApplication()).setNotesCloud(notesCloud);
                    ((GlobalVariables) getActivity().getApplication()).setNotes(notes);*/

                    Log.v("Debug1", "ListNotesFragment cloudSync notes.size=" + notes.size());
                    //Пройдёмся по локальным
                    for (int i = 0; i < notes.size(); i++) {
                        Note noteLocal = notes.get(i);
                        Note noteCloud = ((GlobalVariables) getActivity().getApplication()).getNoteCloudByNoteId(noteLocal.getID());

                        //Если в облачных заметак нет заметки с таким id, то добавляем из локальных
                        if (noteCloud.getID() == -1) {


                            cloudRepository.addNote(notes, noteLocal, result15 -> {
                                noteLocal.setIdCloud((String) result15);

                                localRepository.updateNote(notes, noteLocal, result1 -> Log.v("Debug1", "ListNotesFragment setEmptyResultTextVie addDemo localRepository updateNote"));

                                cloudRepository.updateNote(notes, noteLocal, result151 -> Log.v("Debug1", "ListNotesFragment setEmptyResultTextVie addDemo finalCloudRepository updateNote"));
                            });

                            //notesCloud.add(noteLocal);
                            localRepository.addNote(notesCloud, noteCloud, result13 -> Log.v("Debug1", "EditNoteFragment onClick button_ok notify TYPE_EVENT_ADD_NOTE"));

                        }
                        //Если есть, то сравниваем даты
                        else {
                            //Если дата правки в локальных больше, то обнавляем из локальных
                            if (noteLocal.getDateEdit() > noteCloud.getDateEdit()) {
                                ((GlobalVariables) getActivity().getApplication()).updateNoteCloudById(noteLocal.getID(), noteLocal);

                                cloudRepository.updateNote(null, noteLocal, result1 -> Log.v("Debug1", "ListNotesFragment cloudSync Обновил в облаке result1=" + result1));
                            } else if (noteLocal.getDateEdit() < noteCloud.getDateEdit()) {
                                //Обновляем локальные из облака
                                //((GlobalVariables) getActivity().getApplication()).setNoteById(noteCloud.getID(), noteCloud);

                                //Обнавляем в локальном репозитории
                                localRepository.updateNote(notes, noteCloud, result1 -> {
                                    Log.v("Debug1", "ListNotesFragment cloudSync localRepository updateNote onSuccess");
                                    //((GlobalVariables) getActivity().getApplication()).setNotes(notes);

                                });
                            }
                        }
                    }

                    if (getActivity() != null) {
                        notesCloud = ((GlobalVariables) getActivity().getApplication()).getNotesCloud();
                        notes = ((GlobalVariables) getActivity().getApplication()).getNotes();

                    }

                    localRepository.setNotes(notes, result14 -> {

                    });

                    initRecyclerViewListNotes(recyclerView, -1, TYPE_EVENT_ADD_NOTE);
                }
            });
        }
    }

    private void viewNote(int noteId) {
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

    private void editNote(int noteId) {
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

    private void showAlertDialogDeleteNote(int noteId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle("ВНИМАНИЕ!")
                .setMessage("Вы действительно хотите удалить заметку?")
                .setIcon(R.drawable.ic_clear)
                .setCancelable(false)
                .setPositiveButton("Да", (dialog, which) -> deleteNote(noteId))
                .setNegativeButton("Нет", (dialog, which) -> {
                });

        builder.show();
    }

    //TODO сделать один метод на несколько классов
    private void deleteNote(int noteId) {
        if (getActivity() != null && getActivity().getApplication() != null) {
            List<Note> notes = ((GlobalVariables) getActivity().getApplication()).getNotes();
            int prevID = 0;
            int position = 0;
            Note note = new Note();
            for (int i = 0; i < notes.size(); i++) {
                if (notes.get(i).getID() == noteId) {
                    note = notes.get(i);
                    notes.remove(i);
                    break;
                }
                prevID = notes.get(i).getID();
                position = i;
            }
            Log.v("Debug1", "ListNotesFragment onClick button_delete prevID=" + prevID);
            ((GlobalVariables) getActivity().getApplication()).setNotes(notes);

            boolean cloudSync = false;
            int authTypeService = 0;
            String userName = "";
            if (getActivity() != null) {
                //Получаем настройки из глобальной переменной
                Settings settings = ((GlobalVariables) getActivity().getApplication()).getSettings();
                authTypeService = settings.getAuthTypeService();
                userName = AuthFragment.checkCloudStatusByUserName(settings, getContext(), getActivity());
                if (userName != null && !userName.equals("")) {
                    cloudSync = true;
                }
            }
            Log.v("Debug1", "ListNotesFragment onClick button_delete cloudSync=" + cloudSync);

            if (cloudSync) {
                NotesRepository cloudRepository = new NotesCloudRepositoryImpl(authTypeService, userName);
                cloudRepository.removeNote(notes, note, result -> {
                    Log.v("Debug1", "ListNotesFragment onClick button_ok cloudRepository removeNote result=" + result);
                    if (getView() != null)
                        Snackbar.make(getView(), "Заметка удалена из облака", Snackbar.LENGTH_SHORT).show();
                });
            }

            NotesRepository localRepository = new NotesLocalRepositoryImpl(getContext(), getActivity());
            localRepository.removeNote(notes, note, result -> {
                Log.v("Debug1", "ListNotesFragment onClick button_delete localRepository removeNote onSuccess");
                if (getView() != null)
                    Snackbar.make(getView(), "Заметка удалена с устройства", Snackbar.LENGTH_SHORT).show();

            });


            if (publisher != null) {
                Log.v("Debug1", "ListNotesFragment onClick button_delete notify");
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
                    Log.v("Debug1", "ListNotesFragment initRecyclerViewListNotes onScrolled currentPositionRV=" + currentPositionRV);
                }
            });

            notes = sortNotes(notes, getActivity());

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

            //Читаем настройки из глобальной переменной
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

        Log.v("Debug1", "ListNotesFragment onViewCreated view.getTag()=" + view.getTag());
        viewFragmentListNotes = view;
        if (getActivity() != null) {
            notes = ((GlobalVariables) getActivity().getApplication()).getNotes();
        }
        textViewEmptyListNotes = view.findViewById(R.id.textViewEmprtyListNotesRV);
        buttonAddOne = view.findViewById(R.id.button_addFirstNote);
        button1000 = view.findViewById(R.id.button_addFirst1000);
        recyclerView = view.findViewById(R.id.recycler_view_lines);

        progressBar = view.findViewById(R.id.progress);

        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
        }

        Settings settings = ((GlobalVariables) getActivity().getApplication()).getSettings();

        Log.v("Debug1", "ListNotesFragment onViewCreated settings.getUserNameVK()=" + settings.getUserNameVK());


        currentPositionRV = settings.getCurrentPosition();

        isLoading = true;
        NotesRepository localRepository = new NotesLocalRepositoryImpl(getContext(), getActivity());
        localRepository.getNotes(result -> {
            notes = result;
            if (getActivity() != null) {
                ((GlobalVariables) getActivity().getApplication()).setNotes(notes);
                initRecyclerViewListNotes(recyclerView, -1, TYPE_EVENT_ADD_NOTE);
                isLoading = false;
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

        cloudSync(settings);

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
        //getActivity().setTitle("Список заметок");
        MainActivity.setTitle(getActivity(), "Список заметок");
        Log.v("Debug1", "ListNotesFragment onResume");
    }

    public void onPause() {
        super.onPause();
        Log.v("Debug1", "ListNotesFragment onPause");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
            showAlertDialogDeleteNote(noteId);
            return true;
        }
        return super.onContextItemSelected(item);
    }

}
