package ru.geekbrains.notes.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import java.util.List;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.note.NoteRepository;
import ru.geekbrains.notes.note.NoteRepositoryImpl;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;
import ru.geekbrains.notes.ui.item.ViewNoteFragment;
import ru.geekbrains.notes.ui.list.ListNotesFragment;


public class MainActivity extends AppCompatActivity implements ListNotesFragment.OnNoteClicked, ListNotesFragment.onDateClicked, PublisherHolder {

    private final Publisher publisher = new Publisher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Поднимем layout вместе с клавиатурой
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //Если первый раз
        if (savedInstanceState == null) {
            Log.v("Debug1", "MainActivity onCreate savedInstanceState == null");
            //Получаем доступ к репозиторию
            NoteRepository noteRepository = new NoteRepositoryImpl();

            //Получаем заметки из репозитория
            List<Note> notes = noteRepository.getNotes(this);

            //Сохраняем заметки в глобальной переменной
            ((GlobalVariables) this.getApplication()).setNotes(notes);

        } else {
            Log.v("Debug1", "MainActivity onCreate savedInstanceState != null");
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Log.v("Debug1", "MainActivity onCreate savedInstanceState != null ORIENTATION_LANDSCAPE");
            } else {
                Log.v("Debug1", "MainActivity onCreate savedInstanceState != null NOT_ORIENTATION_LANDSCAPE");
            }
        }
    }

    @Override
    public void onNoteClickedList(int noteId) {
        Log.v("Debug1", "MainActivity onNoteClickedList noteId=" + noteId);

        ViewNoteFragment viewNoteFragment = null;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewNoteFragment = (ViewNoteFragment) getSupportFragmentManager().findFragmentById(R.id.activity_container_note_view);
        } else {
            MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container_main);
            if (mainFragment != null) {
                FragmentManager childFragmentManager = mainFragment.getChildFragmentManager();
                viewNoteFragment = (ViewNoteFragment) childFragmentManager.findFragmentById(R.id.activity_container_note_view);
            }
            //viewNoteFragment = (ViewNoteFragment) mainFragment.getChildFragmentManager().findFragmentById(R.id.activity_container_note_view);
        }

        if (viewNoteFragment == null) {
            Log.v("Debug1", "MainActivity onNoteClickedList viewNoteFragment == null");
            viewNoteFragment = ViewNoteFragment.newInstance(noteId);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frame_container_main, viewNoteFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Log.v("Debug1", "MainActivity onNoteClickedList viewNoteFragment != null");
            viewNoteFragment.fillViewNote(noteId, viewNoteFragment.getViewFragment());
        }
    }

    @Override
    public void onDateClickedList(int noteId) {
        Log.v("Debug1", "MainActivity onDateClickedList");
        DatepickerFragment datepickerFragment = DatepickerFragment.newInstance(noteId);
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_container_main, datepickerFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public Publisher getPublisher() {
        Log.v("Debug1", "MainActivity getPublisher");
        return publisher;
    }


}