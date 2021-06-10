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
import android.widget.TextView;

import ru.geekbrains.notes.domain.note.Note;
import ru.geekbrains.notes.R;

import static android.app.Activity.RESULT_CANCELED;
import static ru.geekbrains.notes.Constant.REQUEST_CODE_EDIT_NOTE;


public class ViewNoteFragment extends Fragment implements View.OnClickListener {

    private static final String ARG = "NOTE_ID";

    private TextView textViewNoteValue;

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

        return v;
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("Debug1", "ViewNoteFragment onViewCreated");

        textViewNoteValue = view.findViewById(R.id.viewTextNoteValue);
        Note note = getArguments().getParcelable(ARG);
        textViewNoteValue.setText(note.getValue());
    }


    @Override
    public void onClick(View v) {

        Log.v("Debug1", "ViewNoteFragment onClick");

        if (v.getId() == R.id.button_edit) {
            Log.v("Debug1", "ViewNoteFragment onClick button_edit");

            Note note = getArguments().getParcelable(ARG);

            Intent intent = new Intent(getActivity(), EditNoteActivity.class);
            intent.putExtra(EditNoteActivity.ARG, note.getID());
            startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
        }
        else
            if (v.getId() == R.id.button_delete) {
                Log.v("Debug1", "ViewNoteFragment onClick button_delete");

                Intent intentResult = new Intent();
                getActivity().setResult(RESULT_CANCELED, intentResult);
                getActivity().finish();
            }
    }
}