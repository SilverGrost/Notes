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
import android.widget.TextView;

import java.util.List;

import ru.geekbrains.notes.MyApplication;
import ru.geekbrains.notes.domain.note.Note;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.domain.observer.Publisher;
import ru.geekbrains.notes.domain.observer.PublisherHolder;
import ru.geekbrains.notes.domain.ui.SharedPref;

import static ru.geekbrains.notes.domain.Constant.REQUEST_CODE_EDIT_NOTE2;
import static ru.geekbrains.notes.domain.Constant.RESULT_DELETED;

public class ViewNoteFragment extends Fragment implements View.OnClickListener {

    private static final String ARG = "NOTE_ID";
    private int index;
    private Publisher publisher;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Debug1", "ViewNoteFragment onCreate");
        if (getArguments() != null) {
            index = getArguments().getInt(ARG);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Debug1", "ViewNoteFragment onAttach");

        if (context instanceof PublisherHolder) {
            publisher = ((PublisherHolder) context).getPublisher();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Debug1", "ViewNoteFragment onDetach");
        publisher = null;
    }

    public ViewNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("Debug1", "ViewNoteFragment onCreateView");

        View v = inflater.inflate(R.layout.fragment_view_note, container, false);

        Button button_edit = v.findViewById(R.id.button_edit);
        button_edit.setOnClickListener(this);

        Button button_delete = v.findViewById(R.id.button_delete);
        button_delete.setOnClickListener(this);

        return v;
    }

    private void fillViewNote(Note note, View view) {
        TextView textViewNoteValue = view.findViewById(R.id.viewTextNoteValue);
        if (note != null)
            textViewNoteValue.setText(note.getValue());
        else
            textViewNoteValue.setText("");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("Debug1", "ViewNoteFragment onViewCreated");

        viewFragment = view;

        if (getArguments() != null) {
            Note note;
            note = getArguments().getParcelable(ARG);
            fillViewNote(note, viewFragment);
        }
    }

    @Override
    public void onClick(View v) {
        Log.v("Debug1", "ViewNoteFragment onClick");

        if (v.getId() == R.id.button_edit) {
            Log.v("Debug1", "ViewNoteFragment onClick button_edit");

            if (getArguments() != null && getArguments().getParcelable(ARG) != null) {
                Note note = getArguments().getParcelable(ARG);

                Intent intent = new Intent(getActivity(), EditNoteActivity.class);
                intent.putExtra(EditNoteActivity.ARG, note.getID());
                if (getActivity() != null)
                    getActivity().startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE2);
            }
        } else if (v.getId() == R.id.button_delete) {
            Log.v("Debug1", "ViewNoteFragment onClick button_delete");

            if (getActivity() != null && getActivity().getApplication() != null) {
                List<Note> notes = ((MyApplication) getActivity().getApplication()).getNotes();

                if (getArguments() != null && getArguments().getParcelable(ARG) != null) {
                    Note note = getArguments().getParcelable(ARG);

                    int prevID = 0;
                    for (int i = 0; i < notes.size(); i++) {
                        if (notes.get(i).getID() == note.getID()) {
                            notes.remove(i);
                            break;
                        }
                        prevID = i;
                    }

                    ((MyApplication) getActivity().getApplication()).setNotes(notes);
                    if (getContext() != null) {
                        new SharedPref(getContext()).saveNotes(notes);
                        Intent intentResult = new Intent();
                        getActivity().setResult(RESULT_DELETED, intentResult);

                        if (getContext() instanceof ViewNoteActivity) {
                            getActivity().finish();
                        }
                        else{
                            if (notes.size() > prevID){
                                fillViewNote(notes.get(prevID), viewFragment);
                            }
                            else {
                                fillViewNote(new Note(), viewFragment);
                            }
                            if (publisher != null) {
                                publisher.notify(note);
                            }
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

        if (getArguments() != null) {
            Note note = getArguments().getParcelable(ARG);
            Log.v("Debug1", "ViewNoteFragment onStart note.getID()=" + note.getID());
            if (getActivity() != null) {
                List<Note> notes = ((MyApplication) getActivity().getApplication()).getNotes();
                for (int i = 0; i < notes.size(); i++) {
                    if (note.getID() == i)
                        fillViewNote(notes.get(i), viewFragment);
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("Debug1", "ViewNoteFragment onStop");
    }
}