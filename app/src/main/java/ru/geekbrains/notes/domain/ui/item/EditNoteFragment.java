package ru.geekbrains.notes.domain.ui.item;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;
import java.util.List;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.domain.note.Note;
import ru.geekbrains.notes.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static ru.geekbrains.notes.domain.Constant.*;


public class EditNoteFragment extends Fragment implements View.OnClickListener {

    private static final String ARG = "NOTE_ID";
    int noteId = 0;

    private EditText editTextNoteValue;

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Debug1", "EditNoteFragment onDetach");
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

        Log.v("Debug1", "EditNoteFragment onCreateView end");

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("Debug1", "EditNoteFragment onViewCreated");

        if (getArguments() != null && getActivity() != null) {
            noteId = getArguments().getInt(ARG, 0);
            Log.v("Debug1", "EditNoteFragment onViewCreated getArguments() != null noteId=" + noteId);
            Note note = ((GlobalVariables) getActivity().getApplication()).getNoteById(noteId);
            editTextNoteValue = view.findViewById(R.id.editTextNoteValue);
            editTextNoteValue.setText(note.getValue());
        }
    }

    @Override
    public void onClick(View v) {
        Log.v("Debug1", "EditNoteFragment onClick");

        if (v.getId() == R.id.button_ok) {
            Log.v("Debug1", "EditNoteFragment onClick button_ok");

            String value = editTextNoteValue.getText().toString();

            int lenHeader;
            int lenValue = value.length();
            if (lenValue > LENHEADER)
                lenHeader = LENHEADER;
            else
                lenHeader = value.length();

            String header = (editTextNoteValue.getText().toString().substring(0, lenHeader) + "...");

            Date date = new Date();

            if (getActivity() != null) {
                List<Note> notes = ((GlobalVariables) getActivity().getApplication()).getNotes();

                Note note = ((GlobalVariables) getActivity().getApplication()).getNoteById(noteId);

                note.setDate(date.toInstant().getEpochSecond());
                note.setHeader(header);
                note.setValue(value);

                if (note.getID() == -1) {
                    note.setID(((GlobalVariables) getActivity().getApplication()).getNewId());
                    notes.add(note);
                } else {
                    ((GlobalVariables) getActivity().getApplication()).setNoteById(noteId, note);
                }

                Intent intentResult = new Intent();
                getActivity().setResult(RESULT_OK, intentResult);
                getActivity().finish();
            }
        } else if (v.getId() == R.id.button_cancel) {
            Log.v("Debug1", "EditNoteFragment onClick button_cancel");

            Intent intentResult = new Intent();
            if (getActivity() != null) {
                getActivity().setResult(RESULT_CANCELED, intentResult);
                getActivity().finish();
            }
        }
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