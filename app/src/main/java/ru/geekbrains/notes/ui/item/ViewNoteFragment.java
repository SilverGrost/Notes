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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ru.geekbrains.notes.MyApplication;
import ru.geekbrains.notes.domain.note.Note;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.ui.SharedPref;

import static android.app.Activity.RESULT_CANCELED;
import static ru.geekbrains.notes.Constant.MILISECOND;
import static ru.geekbrains.notes.Constant.REQUEST_CODE_EDIT_NOTE;
import static ru.geekbrains.notes.Constant.REQUEST_CODE_EDIT_NOTE2;
import static ru.geekbrains.notes.Constant.RESULT_DELETED;


public class ViewNoteFragment extends Fragment implements View.OnClickListener {

    private static final String ARG = "NOTE_ID";

    private TextView textViewNoteValue;

    private View viewFragment;

    public static ViewNoteFragment newInstance(Note note) {
        Log.v("Debug1", "ViewNoteFragment newInstance");
        ViewNoteFragment fragment = new ViewNoteFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG, note);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.v("Debug1", "ViewNoteFragment onAttach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.v("Debug1", "ViewNoteFragment onDetach");
        super.onDetach();
    }

    public ViewNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("Debug1", "ViewNoteFragment onCreateView");

        View v = inflater.inflate(R.layout.activity_view_note, container, false);

        Button button_edit = v.findViewById(R.id.button_edit);
        button_edit.setOnClickListener(this);
        Button button_delete = v.findViewById(R.id.button_delete);
        button_delete.setOnClickListener(this);
        Button button_cancel = v.findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(this);

        return v;
    }

    private void fillList(Note note, View view){
        textViewNoteValue = view.findViewById(R.id.viewTextNoteValue);
        textViewNoteValue.setText(note.getValue());
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("Debug1", "ViewNoteFragment onViewCreated");

        viewFragment = view;

        //List<Note> notes = noteRepository.getNotes(getContext());
        Note note = null;
        if (getArguments() != null) {
            note = getArguments().getParcelable(ARG);
            fillList(note, viewFragment);
        }



    }


    @Override
    public void onClick(View v) {

        Log.v("Debug1", "ViewNoteFragment onClick");

        if (v.getId() == R.id.button_edit) {
            Log.v("Debug1", "ViewNoteFragment onClick button_edit");

            Note note = getArguments().getParcelable(ARG);

            Intent intent = new Intent(getActivity(), EditNoteActivity.class);
            intent.putExtra(EditNoteActivity.ARG, note.getID());
            getActivity().startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE2);
        }
        else
            if (v.getId() == R.id.button_delete) {
                Log.v("Debug1", "ViewNoteFragment onClick button_delete");

                List<Note> notes = ((MyApplication) getActivity().getApplication()).getNotes();

                Note note = getArguments().getParcelable(ARG);
                notes.remove(note.getID());
                new SharedPref(getContext()).saveNotes(notes);

                Intent intentResult = new Intent();
                getActivity().setResult(RESULT_DELETED, intentResult);
                getActivity().finish();
            }
            else
                if (v.getId() == R.id.button_cancel){
                    Log.v("Debug1", "ViewNoteFragment onClick button_cancel");

                    Intent intentResult = new Intent();
                    getActivity().setResult(RESULT_CANCELED, intentResult);
                    getActivity().finish();
                }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("Debug1", "ViewNoteFragment onStart");

        /*Note note = getArguments().getParcelable(ARG);
        List<Note> notes = ((MyApplication) getActivity().getApplication()).getNotes();

        fillList(notes.get(note.getID()), viewFragment);*/
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("Debug1", "ViewNoteFragment onStop");
    }

}