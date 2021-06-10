package ru.geekbrains.notes.domain.observer;

import ru.geekbrains.notes.domain.note.Note;

// Наблюдатель, вызывается updateNote, когда надо отправить событие по изменению заметки
public interface Observer {
    void updateNote(Note note);
}
