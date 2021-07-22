package ru.geekbrains.notes.note

interface NotesRepository {
    fun getNotes(callback: Callback<List<Note?>?>?)
    fun setNotes(notes: List<Note?>?, callback: Callback<Any?>?)
    fun clearNotes(notes: List<Note?>?, callback: Callback<Any?>?)
    fun addNote(notes: List<Note?>?, note: Note?, callback: Callback<Any>)
    fun removeNote(notes: List<Note?>?, note: Note?, callback: Callback<Any>)
    fun updateNote(notes: List<Note?>?, note: Note?, callback: Callback<Any>)
}