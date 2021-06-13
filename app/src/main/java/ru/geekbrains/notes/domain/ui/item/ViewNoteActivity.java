package ru.geekbrains.notes.domain.ui.item;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.domain.note.Note;
import ru.geekbrains.notes.domain.ui.SharedPref;

import static ru.geekbrains.notes.domain.Constant.REQUEST_CODE_EDIT_NOTE2;
import static ru.geekbrains.notes.domain.Constant.RESULT_FINISH;

public class ViewNoteActivity extends AppCompatActivity {
    public static final String ARG = "NOTE_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v("Debug1", "ViewNoteActivity onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.v("Debug1", "ViewNoteActivity onCreate savedInstanceState != null ORIENTATION_LANDSCAPE RESULT_FINISH");

            Intent intentResult = new Intent();
            setResult(RESULT_FINISH, intentResult);
            finish();
        } else {
            Log.v("Debug1", "ViewNoteActivity onCreate savedInstanceState != null NOT_ORIENTATION_LANDSCAPE");

            int noteId = getIntent().getIntExtra(ARG, 0);

            FragmentTransaction fragmentTransaction;
            ViewNoteFragment viewNoteFragment = ViewNoteFragment.newInstance(noteId);
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.activity_container_note_view, viewNoteFragment);
            fragmentTransaction.commit();

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("Debug1", "ViewNoteActivity onActivityResult resultCode=" + resultCode);

        if (requestCode != REQUEST_CODE_EDIT_NOTE2) {
            super.onActivityResult(requestCode, resultCode, data);
        } else if (resultCode == RESULT_OK) {
            Log.v("Debug1", "ViewNoteActivity onActivityResult RESULT_OK");
            List<Note> notes = ((GlobalVariables) this.getApplication()).getNotes();
            new SharedPref(this).saveNotes(notes);
        }
    }
}