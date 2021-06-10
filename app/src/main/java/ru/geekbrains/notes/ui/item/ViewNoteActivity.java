package ru.geekbrains.notes.ui.item;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import ru.geekbrains.notes.R;
import ru.geekbrains.notes.domain.note.Note;

public class ViewNoteActivity extends AppCompatActivity {

    public static final String ARG = "NOTE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("Debug1", "ViewNoteActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view_note);

        if (savedInstanceState == null) {

            Note note = getIntent().getParcelableExtra(ARG);

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_note_view, ViewNoteFragment.newInstance(note))
                    .commit();

        }
    }


}