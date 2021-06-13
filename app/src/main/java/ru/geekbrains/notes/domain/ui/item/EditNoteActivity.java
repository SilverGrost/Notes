package ru.geekbrains.notes.domain.ui.item;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import ru.geekbrains.notes.R;

public class EditNoteActivity extends AppCompatActivity {

    public static final String ARG = "NOTE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Log.v("Debug1", "EditNoteActivity onCreate");

        if (savedInstanceState == null) {

            int noteId = getIntent().getIntExtra(ARG, 0);
            Log.v("Debug1", "EditNoteActivity onCreate savedInstanceState == null noteId=" + noteId);

            FragmentTransaction fragmentTransaction;
            EditNoteFragment editNoteFragment = EditNoteFragment.newInstance(noteId);
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.activity_container_note_edit, editNoteFragment);
            fragmentTransaction.commit();

            Log.v("Debug1", "EditNoteActivity onCreate savedInstanceState == null end");
        }

        Log.v("Debug1", "EditNoteActivity onCreate end");

    }
}