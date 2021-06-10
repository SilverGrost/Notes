package ru.geekbrains.notes.ui.list;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ru.geekbrains.notes.MyApplication;
import ru.geekbrains.notes.domain.note.Note;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.domain.note.NoteRepository;
import ru.geekbrains.notes.domain.note.NoteRepositoryImpl;

import static ru.geekbrains.notes.Constant.MILISECOND;


public class ListNotesFragment extends Fragment {

    private View viewFragment;

    public interface onNoteClicked {
        void onNoteClicked(Note note);
    }

    private NoteRepository noteRepository;

    private ListNotesFragment.onNoteClicked onNoteClicked;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.v("Debug1", "ListNotesFragment onAttach");

        if (context instanceof ListNotesFragment.onNoteClicked) {
            onNoteClicked = (ListNotesFragment.onNoteClicked) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Debug1", "ListNotesFragment onDetach");
        onNoteClicked = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Debug1", "ListNotesFragment onCreate");
        noteRepository = new NoteRepositoryImpl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("Debug1", "ListNotesFragment onCreateView");
        return inflater.inflate(R.layout.activity_list_notes, container, false);
    }

    private void fillList(List<Note> notes, View view){
        LinearLayout linearLayoutNotesList = view.findViewById(R.id.activity_list_notes);
        LinearLayout linearLayoutIntoScrollView = view.findViewById(R.id.linearLayoutIntoScrollViewCont);

        linearLayoutIntoScrollView.removeAllViews();

        for (Note note: notes) {

            View viewTop = LayoutInflater.from(requireContext()).inflate(R.layout.view_item_note_top_textview, linearLayoutNotesList, false);
            View viewBottom = LayoutInflater.from(requireContext()).inflate(R.layout.view_item_note_bottom_textview, linearLayoutNotesList, false);

            viewBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onNoteClicked != null) {
                        onNoteClicked.onNoteClicked(note);
                    }
                }
            });

            TextView textViewTop = viewTop.findViewById(R.id.textViewTop);
            long date = note.getDate() * MILISECOND;
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String dateStr = sdf.format(new Date(date));
            textViewTop.setText(dateStr);

            TextView textViewBottom = viewBottom.findViewById(R.id.textViewBottom);
            textViewBottom.setText(note.getHeader());

            linearLayoutIntoScrollView.addView(viewTop);
            linearLayoutIntoScrollView.addView(viewBottom);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("Debug1", "ListNotesFragment onViewCreated");

        viewFragment = view;

        List<Note> notes = noteRepository.getNotes(getContext());

        fillList(notes, view);
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.v("Debug1", "ListNotesFragment onStart");
        List<Note> notes = ((MyApplication) getActivity().getApplication()).getNotes();
        fillList(notes, viewFragment);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("Debug1", "ListNotesFragment onStop");
    }
}