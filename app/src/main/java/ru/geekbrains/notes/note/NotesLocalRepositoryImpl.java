package ru.geekbrains.notes.note;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.SharedPref;

public class NotesLocalRepositoryImpl implements NotesRepository {

    private final Context context;
    private final Activity activity;

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public NotesLocalRepositoryImpl(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


    @Override
    public void getNotes(Callback<List<Note>> callback) {
        executor.execute(() -> handler.post(() -> {

            List<Note> notes = new SharedPref(context).loadNotes();
            callback.onSuccess(notes);
        }));
    }

    @Override
    public void setNotes(List<Note> notes, Callback<Object> callback) {
        executor.execute(() -> {
            new SharedPref(context).saveNotes(notes);
            handler.post(() -> callback.onSuccess(notes));
        });
    }

    @Override
    public void clearNotes(List<Note> notes, Callback<Object> callback) {
        notes.clear();
        setNotes(notes, callback);
    }

    @Override
    public void addNote(List<Note> notes, Note note, Callback<Object> callback) {
        notes.add(note);
        ((GlobalVariables) activity.getApplication()).setNotes(notes);
        setNotes(notes, callback);
    }

    @Override
    public void removeNote(List<Note> notes, Note note, Callback<Object> callback) {
        executor.execute(() -> {
            for (int i = 0; i < notes.size(); i++) {
                if (notes.get(i).getID() == note.getID()) {
                    notes.remove(i);
                    ((GlobalVariables) activity.getApplication()).setNotes(notes);
                    break;
                }
            }
            new SharedPref(context).saveNotes(notes);
            handler.post(() -> callback.onSuccess(true));
        });
    }

    @Override
    public void updateNote(List<Note> notes, Note note, Callback<Object> callback) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getID() == note.getID()) {
                notes.set(i, note);
                ((GlobalVariables) activity.getApplication()).setNotes(notes);
                setNotes(notes, callback);
                break;
            }
        }
    }
}
