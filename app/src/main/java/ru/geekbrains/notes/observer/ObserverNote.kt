package ru.geekbrains.notes.observer;

import ru.geekbrains.notes.note.Note;

// Наблюдатель, вызывается updateNote, когда надо отправить событие по изменению заметки
public interface ObserverNote {
    void updateNote(int noteID, int typeEvent);
}
