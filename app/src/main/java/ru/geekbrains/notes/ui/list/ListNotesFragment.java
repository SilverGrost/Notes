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

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import ru.geekbrains.notes.MyApplication;
import ru.geekbrains.notes.domain.note.Note;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.domain.note.NoteRepository;
import ru.geekbrains.notes.domain.note.NoteRepositoryImpl;

import static ru.geekbrains.notes.Constant.MILISECOND;


public class ListNotesFragment extends Fragment {

    private View viewFragment;

    public interface onNoteClicked {
        void onNoteClickedList(Note note);
    }

    public interface onDateClicked {
        void onDateClickedList(Note note);
    }

    private NoteRepository noteRepository;

    private ListNotesFragment.onNoteClicked onNoteClicked;

    private ListNotesFragment.onDateClicked onDateClicked;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Debug1", "ListNotesFragment onAttach");

        if (context instanceof ListNotesFragment.onNoteClicked) {
            onNoteClicked = (ListNotesFragment.onNoteClicked) context;
        }

        if (context instanceof ListNotesFragment.onDateClicked) {
            onDateClicked = (ListNotesFragment.onDateClicked) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Debug1", "ListNotesFragment onDetach");
        onNoteClicked = null;
        onDateClicked = null;
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

    private void fillList(List<Note> notes, View view) {
        LinearLayout linearLayoutNotesList = view.findViewById(R.id.activity_list_notes);
        LinearLayout linearLayoutIntoScrollView = view.findViewById(R.id.linearLayoutIntoScrollViewCont);

        linearLayoutIntoScrollView.removeAllViews();

        for (Note note : notes) {
            View viewTop = LayoutInflater.from(requireContext()).inflate(R.layout.view_item_note_top_textview, linearLayoutNotesList, false);
            View viewBottom = LayoutInflater.from(requireContext()).inflate(R.layout.view_item_note_bottom_textview, linearLayoutNotesList, false);

            viewBottom.setOnClickListener(v -> {
                if (onNoteClicked != null) {
                    onNoteClicked.onNoteClickedList(note);
                }
            });

            viewTop.setOnClickListener(v -> {
                if (onDateClicked != null) {
                    onDateClicked.onDateClickedList(note);
                }
            });

            TextView textViewTop = viewTop.findViewById(R.id.textViewTop);
            long date = note.getDate() * MILISECOND;

            DateFormat f = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
            String dateStr = f.format(date);

            textViewTop.setText(dateStr);
            textViewTop.setTag(note.getID());

            Log.v("Debug1", "ListNotesFragment fillList textViewTop.getTag()=" + textViewTop.getTag());

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
        if (getActivity() != null) {
            if (getActivity().getApplication() != null) {
                List<Note> notes = ((MyApplication) getActivity().getApplication()).getNotes();
                fillList(notes, viewFragment);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("Debug1", "ListNotesFragment onStop");
    }


    public void onResume() {
        super.onResume();
        Log.v("Debug1", "ListNotesFragment onResume");
    }

    public void onPause() {
        super.onPause();
        Log.v("Debug1", "ListNotesFragment onPause");
    }

}