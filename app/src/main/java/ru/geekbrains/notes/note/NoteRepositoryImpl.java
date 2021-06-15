package ru.geekbrains.notes.note;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.notes.SharedPref;

public class NoteRepositoryImpl implements NoteRepository {

    @Override
    public List<Note> getNotes(Context context) {
        ArrayList<Note> notes = (new SharedPref(context).loadNotes());
        return notes;
    }
}
