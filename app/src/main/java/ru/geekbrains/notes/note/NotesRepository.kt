package ru.geekbrains.notes.note;

import java.util.List;

public interface NotesRepository {

    void getNotes(Callback<List<Note>> callback);

    void setNotes(List<Note> notes, Callback<Object> callback);

    void clearNotes(List<Note> notes, Callback<Object> callback);

    void addNote(List<Note> notes, Note note, Callback<Object> callback);

    void removeNote(List<Note> notes, Note note, Callback<Object> callback);

    void updateNote(List<Note> notes, Note note, Callback<Object> callback);
}
