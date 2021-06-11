package ru.geekbrains.notes.ui.item;

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

import ru.geekbrains.notes.MyApplication;
import ru.geekbrains.notes.domain.note.Note;
import ru.geekbrains.notes.R;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static ru.geekbrains.notes.Constant.*;


public class EditNoteFragment extends Fragment implements View.OnClickListener {

    private static final String ARG = "NOTE_ID";

    private EditText editTextNoteValue;

    public static EditNoteFragment newInstance(int idNote) {
        Log.v("Debug1", "EditNoteFragment newInstance");
        EditNoteFragment fragment = new EditNoteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG, idNote);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.v("Debug1", "EditNoteFragment onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.v("Debug1", "EditNoteFragment onDetach");
        super.onDetach();
    }

    public EditNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("Debug1", "EditNoteFragment onCreateView");

        View v = inflater.inflate(R.layout.activity_edit_note, container, false);

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

        if (getArguments() != null) {
            int idNote = getArguments().getInt(ARG, 0);
            if (getActivity() != null && getActivity().getApplication() != null) {
                List<Note> notes = ((MyApplication) getActivity().getApplication()).getNotes();
                editTextNoteValue = view.findViewById(R.id.editTextNoteValue);
                if (idNote < notes.size()) {
                    editTextNoteValue.setText(notes.get(idNote).getValue());
                }
            }
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
            if (getArguments() != null) {
                int idNote = getArguments().getInt(ARG, 0);
                Date date = new Date();

                if (getActivity() != null) {
                    List<Note> notes = ((MyApplication) getActivity().getApplication()).getNotes();
                    if (idNote < notes.size()) {
                        Note note = notes.get(idNote);
                        note.setDate(date.toInstant().getEpochSecond());
                        note.setHeader(header);
                        note.setValue(value);
                        notes.set(idNote, note);
                    } else {
                        Note note = new Note(value, header, idNote, date.toInstant().getEpochSecond());
                        notes.add(note);
                    }
                    ((MyApplication) getActivity().getApplication()).setNotes(notes);

                    Intent intentResult = new Intent();
                    getActivity().setResult(RESULT_OK, intentResult);
                    getActivity().finish();
                }
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