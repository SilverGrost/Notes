package ru.geekbrains.notes.domain.observer;

import ru.geekbrains.notes.domain.note.Note;

// Наблюдатель, вызывается updateNote, когда надо отправить событие по изменению заметки
public interface ObserverNote {
    void updateNote(Note note);
}