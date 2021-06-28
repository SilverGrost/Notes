package ru.geekbrains.notes.note;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ru.geekbrains.notes.SharedPref;

public class NotesLocalRepositoryImpl implements NotesRepository {

    private Context context;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private Handler handler = new Handler(Looper.getMainLooper());

    public NotesLocalRepositoryImpl(Context context) {
        this.context = context;
    }

    /*@Override
    public List<Note> getNotes() {
        return (new SharedPref(context).loadNotes());
    }*/

    @Override
    public void getNotes(Callback<List<Note>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        /*try {
                            Thread.sleep(2000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/

                        List<Note> notes = new SharedPref(context).loadNotes();
                        callback.onSuccess(notes);
                    }
                });
            }
        });
    }

    @Override
    public void setNotes(List<Note> notes, Callback<Object> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                new SharedPref(context).saveNotes(notes);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(notes);
                    }
                });
            }
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
        setNotes(notes, callback);
    }

    @Override
    public void removeNote(List<Note> notes, Note note, Callback<Object> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < notes.size(); i++) {
                    if (notes.get(i).getID() == note.getID()) {
                        notes.remove(i);
                        break;
                    }
                }
                new SharedPref(context).saveNotes(notes);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(true);
                    }
                });
            }
        });
    }

    @Override
    public void updateNote(List<Note> notes, Note note, Callback<Object> callback) {
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getID() == note.getID()) {
                notes.set(i, note);
                setNotes(notes, callback);
                break;
            }
        }
    }
}
