package ru.geekbrains.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

import ru.geekbrains.notes.domain.note.Note;
import ru.geekbrains.notes.domain.note.NoteRepository;
import ru.geekbrains.notes.domain.note.NoteRepositoryImpl;
import ru.geekbrains.notes.ui.SharedPref;
import ru.geekbrains.notes.ui.item.EditNoteActivity;
import ru.geekbrains.notes.ui.item.ViewNoteActivity;
import ru.geekbrains.notes.ui.item.ViewNoteFragment;
import ru.geekbrains.notes.ui.list.ListNotesFragment;

import static ru.geekbrains.notes.Constant.REQUEST_CODE_EDIT_NOTE;
import static ru.geekbrains.notes.Constant.RESULT_FINISH;

public class MainActivity extends AppCompatActivity implements ListNotesFragment.onNoteClicked, ListNotesFragment.onDateClicked, View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("Debug1", "MainActivity onCreate");

        if (savedInstanceState == null) {
            Log.v("Debug1", "MainActivity onCreate savedInstanceState == null");
            NoteRepository noteRepository = new NoteRepositoryImpl();
            List<Note> notes = noteRepository.getNotes(this);
            ((MyApplication) this.getApplication()).setNotes(notes);

            Button buttonAddNote = findViewById(R.id.buttonAddNote);
            buttonAddNote.setOnClickListener(this);
        } else {
            Log.v("Debug1", "MainActivity onCreate savedInstanceState != null");
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Log.v("Debug1", "MainActivity onCreate savedInstanceState != null ORIENTATION_LANDSCAPE");
                FragmentTransaction fragmentTransaction;
                fragmentTransaction = getSupportFragmentManager().beginTransaction();

                List<Note> notes = ((MyApplication) this.getApplication()).getNotes();
                fragmentTransaction.replace(R.id.fragment_item_note_container, ViewNoteFragment.newInstance(notes.get(0)));
                fragmentTransaction.commit();

            } else {
                Log.v("Debug1", "MainActivity onCreate savedInstanceState != null NOT_ORIENTATION_LANDSCAPE");
            }
        }
    }

    @Override
    public void onNoteClickedList(Note note) {
        Log.v("Debug1", "MainActivity onNoteClicked");

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FragmentTransaction fragmentTransaction;
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_item_note_container, ViewNoteFragment.newInstance(note));
            fragmentTransaction.commit();

        } else {
            Intent intent = new Intent(this, ViewNoteActivity.class);
            intent.putExtra(ViewNoteActivity.ARG, note);
            startActivity(intent);
        }
    }

    @Override
    public void onDateClickedList(Note note) {
        Log.v("Debug1", "MainActivity onDateClickedList");

        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            FragmentTransaction fragmentTransaction;
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.ListNotesFragment, DatepickerFragment.newInstance(note), "DP");
            fragmentTransaction.commit();
        }
        else {
            FragmentTransaction fragmentTransaction;
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.LandLayout, DatepickerFragment.newInstance(note));
            fragmentTransaction.commit();
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonAddNote) {

            Log.v("Debug1", "MainActivity onClick");

            List<Note> notes = ((MyApplication) this.getApplication()).getNotes();

            Intent intent = new Intent(this, EditNoteActivity.class);
            intent.putExtra(EditNoteActivity.ARG, notes.size());
            startActivityForResult(intent, REQUEST_CODE_EDIT_NOTE);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.v("Debug1", "MainActivity onActivityResult resultCode=" + resultCode);

        if (requestCode != REQUEST_CODE_EDIT_NOTE) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (resultCode == RESULT_OK) {
            Log.v("Debug1", "MainActivity onActivityResult RESULT_OK");
            List<Note> notes = ((MyApplication) this.getApplication()).getNotes();
            new SharedPref(this).saveNotes(notes);
        }

        if (resultCode == RESULT_FINISH) {
            Log.v("Debug1", "MainActivity onActivityResult RESULT_FINISH");
            /*List<Note> notes = ((MyApplication) this.getApplication()).getNotes();
            new SharedPref(this).saveNotes(notes);*/
        }
    }

}