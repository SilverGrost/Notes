package ru.geekbrains.notes.ui.item;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.Settings;
import ru.geekbrains.notes.note.Callback;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.note.NotesLocalRepositoryImpl;
import ru.geekbrains.notes.note.NotesRepository;
import ru.geekbrains.notes.observer.ObserverNote;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;
import ru.geekbrains.notes.SharedPref;
import ru.geekbrains.notes.ui.list.SearchResultFragment;

import static ru.geekbrains.notes.Constant.TYPE_EVENT_DELETE_NOTE;
import static ru.geekbrains.notes.Constant.TYPE_EVENT_EDIT_NOTE;

public class ViewNoteFragment extends Fragment implements ObserverNote {

    private static final String ARG = "NOTE_ID";

    private int noteId = 0;
    private Publisher publisher;
    private Publisher publisher2;
    private int position;

    private View viewFragment;

    public View getViewFragment() {
        return viewFragment;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.view_note_fragment, menu);

        //Убираем меню главного фрагмента в портретной орииентации
        if (getActivity() != null) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                MenuItem item_action_search = menu.findItem(R.id.action_search);
                if (item_action_search != null)
                    item_action_search.setVisible(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.v("Debug1", "MainActivity onOptionsItemSelected");
        // Обработка выбора пункта меню приложения (активити)
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            buttonEditAction();
            return true;
        } else if (id == R.id.action_delete) {
            buttonDeleteAction();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static ViewNoteFragment newInstance(int noteID) {
        Log.v("Debug1", "ViewNoteFragment newInstance noteID=" + noteID);
        ViewNoteFragment fragment = new ViewNoteFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG, noteID);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Debug1", "ViewNoteFragment onCreate");

        if (getArguments() != null) {
            noteId = getArguments().getInt(ARG, 0);
            Log.v("Debug1", "ViewNoteFragment onCreate getArguments() != null noteId=" + noteId);
        } else {
            if (getActivity() != null && getActivity().getApplication() != null) {
                noteId = ((GlobalVariables) getActivity().getApplication()).getCurrentNote();
                Log.v("Debug1", "ViewNoteFragment onCreate getArguments() == null noteId=" + noteId);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Debug1", "ViewNoteFragment onAttach context=" + context);
        if (context instanceof PublisherHolder) {
            publisher = ((PublisherHolder) context).getPublisher();
        }
        if (context instanceof PublisherHolder) {
            publisher2 = ((PublisherHolder) context).getPublisher();
            publisher2.subscribe(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Debug1", "ViewNoteFragment onDetach");
        publisher = null;
        publisher2 = null;
    }

    public ViewNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_note, container, false);

        if (getActivity() != null) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setHasOptionsMenu(true);
            } else {
                Fragment parentFragment = getParentFragment();
                if (parentFragment == null) {
                    setHasOptionsMenu(getActivity() != null);
                }
            }
        }
        /*Button button_edit = v.findViewById(R.id.button_edit);
        button_edit.setOnClickListener(this);
        Button button_delete = v.findViewById(R.id.button_delete);
        button_delete.setOnClickListener(this);*/
        Log.v("Debug1", "ViewNoteFragment onCreateView getArguments() != null noteId=" + noteId + ", container=" + container);
        return v;
    }

    public void fillViewNote(int noteId, View view) {
        Log.v("Debug1", "ViewNoteFragment fillViewNote");
        this.noteId = noteId;
        if (getActivity() != null) {
            ((GlobalVariables) getActivity().getApplication()).setCurrentNote(noteId);
            TextView textViewNoteValue = view.findViewById(R.id.viewTextNoteValue);
            Log.v("Debug1", "ViewNoteFragment fillViewNote noteId=" + noteId);
            if (noteId != -1) {
                Note note = ((GlobalVariables) getActivity().getApplication()).getNoteByNoteId(noteId);
                textViewNoteValue.setText(note.getValue());
            } else
                textViewNoteValue.setText("");

            Settings settings = new Settings();
            if (getActivity() != null) {
                settings = ((GlobalVariables) getActivity().getApplication()).getSettings();
            }

            textViewNoteValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.getTextSize());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("Debug1", "ViewNoteFragment onViewCreated");
        viewFragment = view;
        if (getArguments() != null) {
            noteId = getArguments().getInt(ARG, 0);
            Log.v("Debug1", "ViewNoteFragment onViewCreated getArguments() != null noteId=" + noteId);
        } else {
            Log.v("Debug1", "ViewNoteFragment onViewCreated getArguments() == null noteId=" + noteId);
        }
    }

    private void buttonEditAction() {
        Log.v("Debug1", "ViewNoteFragment buttonEditAction");
        EditNoteFragment editNoteFragment = EditNoteFragment.newInstance(noteId);
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            SearchResultFragment searchResultFragment = (SearchResultFragment) fragmentManager.findFragmentByTag("SearchResultFragment");
            if (searchResultFragment == null)
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    fragmentManager = getActivity().getSupportFragmentManager();
                } else {
                    fragmentManager = getActivity().getSupportFragmentManager();
                    Fragment parentFragment = getParentFragment();
                    if (parentFragment != null) {
                        fragmentManager = parentFragment.getActivity().getSupportFragmentManager();
                    }
                }
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            //fragmentTransaction.add(R.id.frame_container_main, editNoteFragment);
            fragmentTransaction.replace(R.id.frame_container_main, editNoteFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private void buttonDeleteAction() {
        Log.v("Debug1", "ViewNoteFragment buttonDeleteAction");
        if (getActivity() != null && getActivity().getApplication() != null) {
            List<Note> notes = ((GlobalVariables) getActivity().getApplication()).getNotes();
            int prevID = 0;
            position = 0;
            Note note = new Note();
            for (int i = 0; i < notes.size(); i++) {
                if (notes.get(i).getID() == noteId) {
                    notes.remove(i);
                    note = notes.get(i);
                    break;
                }
                prevID = notes.get(i).getID();
                position = i;
            }
            Log.v("Debug1", "ViewNoteFragment onClick button_delete prevID=" + prevID);
            ((GlobalVariables) getActivity().getApplication()).setNotes(notes);

            NotesRepository localRepository = new NotesLocalRepositoryImpl(getContext());
            localRepository.removeNote(notes, note, new Callback<Object>() {
                @Override
                public void onSuccess(Object result) {
                    /*if (publisher != null) {
                        publisher.notify(position, TYPE_EVENT_DELETE_NOTE);
                    }*/
                    Log.v("Debug1", "ViewNoteFragment onClick button_delete onSuccess");
                }
            });


            //if (getContext() != null) {
                //new SharedPref(getContext()).saveNotes(notes);
                if (publisher != null) {
                    Log.v("Debug1", "ViewNoteFragment onClick button_delete notify");
                    publisher.notify(position, TYPE_EVENT_DELETE_NOTE);
                }
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (getActivity() != null) {
                        Log.v("Debug1", "ViewNoteFragment onClick button_delete getActivity() != null");
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentManager.popBackStack();
                        fragmentTransaction.commit();
                    }
                } else {
                    fillViewNote(prevID, viewFragment);
                }
            //}
        }
    }

    /*@Override
    public void onClick(View v) {
        Log.v("Debug1", "ViewNoteFragment onClick noteId=" + noteId);
        if (v.getId() == R.id.button_edit) {
            Log.v("Debug1", "ViewNoteFragment onClick button_edit");
            buttonEditAction();
        } else if (v.getId() == R.id.button_delete) {
            Log.v("Debug1", "ViewNoteFragment onClick button_delete");
            buttonDeleteAction();
        }
    }*/

    @Override
    public void onStart() {
        super.onStart();
        Log.v("Debug1", "ViewNoteFragment onStart");
        fillViewNote(noteId, viewFragment);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("Debug1", "ViewNoteFragment onStop");
    }

    @Override
    public void updateNote(int noteID, int typeEvent) {
        Log.v("Debug1", "ViewNoteFragment updateNote noteId=" + noteId + ", typeEvent=" + typeEvent);
        if (typeEvent != TYPE_EVENT_DELETE_NOTE)
            fillViewNote(noteId, viewFragment);
    }

    public void onResume() {
        super.onResume();
        Log.v("Debug1", "ViewNoteFragment onResume");
    }

    public void onPause() {
        super.onPause();
        Log.v("Debug1", "ViewNoteFragment onPause");
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.v("Debug1", "ViewNoteFragment onDestroyView");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.v("Debug1", "ViewNoteFragment onDestroy");
    }
}