package ru.geekbrains.notes.domain.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;

import ru.geekbrains.notes.MyApplication;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.domain.note.Note;
import ru.geekbrains.notes.domain.note.NoteRepository;
import ru.geekbrains.notes.domain.note.NoteRepositoryImpl;
import ru.geekbrains.notes.domain.observer.Publisher;
import ru.geekbrains.notes.domain.observer.PublisherHolder;
import ru.geekbrains.notes.domain.ui.item.ViewNoteActivity;
import ru.geekbrains.notes.domain.ui.item.ViewNoteFragment;
import ru.geekbrains.notes.domain.ui.item.EditNoteActivity;
import ru.geekbrains.notes.domain.ui.list.ListNotesFragment;

import static ru.geekbrains.notes.domain.Constant.REQUEST_CODE_EDIT_NOTE;
import static ru.geekbrains.notes.domain.Constant.RESULT_FINISH;

public class MainActivity extends AppCompatActivity implements ListNotesFragment.onNoteClicked, ListNotesFragment.onDateClicked, View.OnClickListener, PublisherHolder {

    private final Publisher publisher = new Publisher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonAddNote = findViewById(R.id.buttonAddNote);
        buttonAddNote.setOnClickListener(this);

        //Если первый раз
        if (savedInstanceState == null) {
            Log.v("Debug1", "MainActivity onCreate savedInstanceState == null");
            //Получаем доступ к репозиторию
            NoteRepository noteRepository = new NoteRepositoryImpl();

            //Получаем заметки из репозитория
            List<Note> notes = noteRepository.getNotes(this);

            //Сохраняем заметки в глобальной переменной
            ((MyApplication) this.getApplication()).setNotes(notes);

        } else {
            Log.v("Debug1", "MainActivity onCreate savedInstanceState != null");
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Log.v("Debug1", "MainActivity onCreate savedInstanceState != null ORIENTATION_LANDSCAPE");
                FragmentTransaction fragmentTransaction;
                fragmentTransaction = getSupportFragmentManager().beginTransaction();

                //Получаем заметки из глобальной переменной
                List<Note> notes = ((MyApplication) this.getApplication()).getNotes();

                // Создаём новый фрагмент для отображения списка
                ListNotesFragment listNotesFragment = ListNotesFragment.newInstance();
                fragmentTransaction.replace(R.id.fragment_list_note_container, listNotesFragment);

                if (notes.size() != 0) {
                    // Создаём новый фрагмент для текущей заметки
                    ViewNoteFragment viewNoteFragment = ViewNoteFragment.newInstance(notes.get(listNotesFragment.getCurrentPosition()));
                    fragmentTransaction.replace(R.id.fragment_item_note_container, viewNoteFragment);
                    fragmentTransaction.commit();
                }

            } else {
                Log.v("Debug1", "MainActivity onCreate savedInstanceState != null NOT_ORIENTATION_LANDSCAPE");
            }
        }
    }

    @Override
    public void onNoteClickedList(Note note) {
        Log.v("Debug1", "MainActivity onNoteClickedList");

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FragmentTransaction fragmentTransaction;
            fragmentTransaction = getSupportFragmentManager().beginTransaction();

            ViewNoteFragment viewNoteFragment = ViewNoteFragment.newInstance(note);
            fragmentTransaction.replace(R.id.fragment_item_note_container, viewNoteFragment);
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

        DatepickerFragment datepickerFragment = DatepickerFragment.newInstance(note);

        FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            fragmentTransaction.add(R.id.ListNotesFragment, datepickerFragment);
        }
        else {
            fragmentTransaction.add(R.id.LandLayout, datepickerFragment);
        }
        fragmentTransaction.commit();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonAddNote) {

            Log.v("Debug1", "MainActivity onClick buttonAddNote");

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

    @Override
    public Publisher getPublisher() {
        Log.v("Debug1", "MainActivity getPublisher");
        return publisher;
    }
}