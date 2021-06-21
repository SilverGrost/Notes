package ru.geekbrains.notes.note;

import android.content.Context;

import java.util.List;

import ru.geekbrains.notes.SharedPref;

public class NoteRepositoryImpl implements NoteRepository {

    @Override
    public List<Note> getNotes(Context context) {
        return (new SharedPref(context).loadNotes());
    }
}
