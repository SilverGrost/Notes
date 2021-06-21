package ru.geekbrains.notes.ui.item;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;
import java.util.List;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.Settings;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;
import ru.geekbrains.notes.SharedPref;

import static ru.geekbrains.notes.Constant.TYPE_EVENT_ADD_NOTE;
import static ru.geekbrains.notes.Constant.TYPE_EVENT_EDIT_NOTE;


public class EditNoteFragment extends Fragment implements View.OnClickListener {

    private static final String ARG = "NOTE_ID";
    int noteId = 0;

    private EditText editTextNoteValue;

    private Publisher publisher;

    public static EditNoteFragment newInstance(int noteId) {
        Log.v("Debug1", "EditNoteFragment newInstance noteId=" + noteId);
        EditNoteFragment fragment = new EditNoteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG, noteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Debug1", "EditNoteFragment onCreate");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Debug1", "EditNoteFragment onAttach");
        if (context instanceof PublisherHolder) {
            publisher = ((PublisherHolder) context).getPublisher();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Debug1", "EditNoteFragment onDetach");
        publisher = null;
    }

    public EditNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("Debug1", "EditNoteFragment onCreateView");
        View v = inflater.inflate(R.layout.fragment_edit_note, container, false);
        Button button_ok = v.findViewById(R.id.button_ok);
        button_ok.setOnClickListener(this);
        Button button_cancel = v.findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(this);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("Debug1", "EditNoteFragment onViewCreated");
        if (getArguments() != null && getActivity() != null) {
            noteId = getArguments().getInt(ARG, 0);
            Log.v("Debug1", "EditNoteFragment onViewCreated getArguments() != null noteId=" + noteId);
            Note note = ((GlobalVariables) getActivity().getApplication()).getNoteByNoteId(noteId);
            editTextNoteValue = view.findViewById(R.id.editTextNoteValue);

            Settings settings = new Settings();
            if (getActivity() != null) {
                settings = ((GlobalVariables) getActivity().getApplication()).getSettings();
            }

            editTextNoteValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.getTextSize());
            editTextNoteValue.setText(note.getValue());
        }
    }

    @Override
    public void onClick(View v) {
        Log.v("Debug1", "EditNoteFragment onClick");

        if (v.getId() == R.id.button_ok) {
            int newNoteId = -1;
            Log.v("Debug1", "EditNoteFragment onClick button_ok");
            String value = editTextNoteValue.getText().toString();
            Date date = new Date();
            if (getActivity() != null) {
                List<Note> notes = ((GlobalVariables) getActivity().getApplication()).getNotes();
                Note note = ((GlobalVariables) getActivity().getApplication()).getNoteByNoteId(noteId);
                note.setDateEdit(date.toInstant().getEpochSecond());
                note.setValue(value);
                if (note.getID() == -1) {
                    note.setDateCreate(date.toInstant().getEpochSecond());

                    newNoteId = ((GlobalVariables) getActivity().getApplication()).getNewId();

                    note.setID(newNoteId);
                    notes.add(note);
                } else {
                    ((GlobalVariables) getActivity().getApplication()).setNoteById(noteId, note);
                }

                notes = ((GlobalVariables) getActivity().getApplication()).getNotes();
                if (getContext() != null)
                    new SharedPref(getContext()).saveNotes(notes);

                if (publisher != null) {
                    Log.v("Debug1", "EditNoteFragment onClick button_ok notify");
                    if (noteId == -1)
                        publisher.notify(newNoteId, TYPE_EVENT_ADD_NOTE);
                    else
                        publisher.notify(noteId, TYPE_EVENT_EDIT_NOTE);
                }
            }
        } else if (v.getId() == R.id.button_cancel) {
            Log.v("Debug1", "EditNoteFragment onClick button_cancel");
        }

        Log.v("Debug1", "EditNoteFragment onClick FragmentTransaction");
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        }
        Log.v("Debug1", "EditNoteFragment onClick end");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("Debug1", "EditNoteFragment onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("Debug1", "EditNoteFragment onStop");
    }
}