package ru.geekbrains.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
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
import static ru.geekbrains.notes.Constant.RESULT_DELETED;

public class MainActivity extends AppCompatActivity implements ListNotesFragment.onNoteClicked, View.OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("Debug1", "MainActivity onCreate");

        NoteRepository noteRepository = new NoteRepositoryImpl();
        List<Note> notes = noteRepository.getNotes(this);
        ((MyApplication) this.getApplication()).setNotes(notes);

        Button buttonAddNote = findViewById(R.id.buttonAddNote);
        buttonAddNote.setOnClickListener(this);
    }

    @Override
    public void onNoteClicked(Note note) {

        Log.v("Debug1", "MainActivity onNoteClicked");

        boolean isLandscape = getResources().getBoolean(R.bool.isLandscape);

        FragmentTransaction fragmentTransaction;


        if (isLandscape) {

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
        /*else if (resultCode == RESULT_DELETED) {
            Log.v("Debug1", "ViewNoteActivity onActivityResult RESULT_DELETED");

            Note note = getIntent().getParcelableExtra(ViewNoteActivity.ARG);
            List<Note> notes = ((MyApplication) this.getApplication()).getNotes();
            notes.remove(note.getID());
            new SharedPref(this).saveNotes(notes);
        }*/
    }

}