package ru.geekbrains.notes.observer

// Наблюдатель, вызывается updateNote, когда надо отправить событие по изменению заметки
interface ObserverNote {
    fun updateNote(noteID: Int, typeEvent: Int)
}