package ru.geekbrains.notes.domain.note;

import android.content.Context;

import java.util.List;

import ru.geekbrains.notes.domain.note.Note;

public interface NoteRepository {

    List<Note> getNotes(Context context);
}
