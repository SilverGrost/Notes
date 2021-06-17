package ru.geekbrains.notes.note;

import android.content.Context;

import java.util.List;

public interface NoteRepository {

    List<Note> getNotes(Context context);
}
