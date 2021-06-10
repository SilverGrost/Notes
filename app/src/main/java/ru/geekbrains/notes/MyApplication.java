package ru.geekbrains.notes;

import android.app.Application;

import java.util.List;

import ru.geekbrains.notes.domain.note.Note;

public class MyApplication extends Application {

    private List<Note> notes;

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
}
