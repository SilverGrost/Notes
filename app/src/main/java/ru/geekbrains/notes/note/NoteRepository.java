package ru.geekbrains.notes.note;

import android.content.Context;

import java.util.List;

import ru.geekbrains.notes.note.Note;

public interface NoteRepository {

    List<Note> getNotes(Context context);
}
