package ru.geekbrains.notes.ui.item;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.observer.ObserverNote;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;
import ru.geekbrains.notes.SharedPref;

public class ViewNoteFragment extends Fragment implements View.OnClickListener, ObserverNote {

    private static final String ARG = "NOTE_ID";

    private int noteId = 0;
    private Publisher publisher;
    private Publisher publisher2;

    private View viewFragment;

    public View getViewFragment() {
        return viewFragment;
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
        Log.v("Debug1", "ViewNoteFragment onAttach");

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

        Button button_edit = v.findViewById(R.id.button_edit);
        button_edit.setOnClickListener(this);

        Button button_delete = v.findViewById(R.id.button_delete);
        button_delete.setOnClickListener(this);

        Log.v("Debug1", "ViewNoteFragment onCreateView getArguments() != null noteId=" + noteId);

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
                Note note = ((GlobalVariables) getActivity().getApplication()).getNoteById(noteId);
                textViewNoteValue.setText(note.getValue());
            } else
                textViewNoteValue.setText("");
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

    @Override
    public void onClick(View v) {
        Log.v("Debug1", "ViewNoteFragment onClick noteId=" + noteId);

        if (v.getId() == R.id.button_edit) {
            Log.v("Debug1", "ViewNoteFragment onClick button_edit");
            EditNoteFragment editNoteFragment = EditNoteFragment.newInstance(noteId);

            Fragment ParentFragment = getParentFragment();
            if (ParentFragment != null){
                FragmentManager fragmentManager = ParentFragment.getFragmentManager();
                if (fragmentManager != null){
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.add(R.id.frame_container_main, editNoteFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }

        } else if (v.getId() == R.id.button_delete) {
            Log.v("Debug1", "ViewNoteFragment onClick button_delete");

            if (getActivity() != null && getActivity().getApplication() != null) {
                List<Note> notes = ((GlobalVariables) getActivity().getApplication()).getNotes();

                int prevID = 0;
                for (int i = 0; i < notes.size(); i++) {
                    if (notes.get(i).getID() == noteId) {
                        notes.remove(i);
                        break;
                    }
                    prevID = notes.get(i).getID();
                }

                Log.v("Debug1", "ViewNoteFragment onClick button_delete prevID=" + prevID);

                ((GlobalVariables) getActivity().getApplication()).setNotes(notes);
                if (getContext() != null) {
                    new SharedPref(getContext()).saveNotes(notes);

                    fillViewNote(prevID, viewFragment);

                    if (publisher != null) {
                        publisher.notify(noteId);
                    }

                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

                        FragmentManager fragmentManager = getFragmentManager();
                        if (fragmentManager != null) {
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.remove(this);
                            fragmentTransaction.commit();
                        }
                    }
                }
            }
        }
    }

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
    public void updateNote(int noteID) {
        Log.v("Debug1", "ViewNoteFragment updateNote noteId=" + noteId);
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