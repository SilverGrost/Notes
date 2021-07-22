package ru.geekbrains.notes.note.comparator

import ru.geekbrains.notes.note.Note
import java.util.*

class DateCreateSorterComparator : Comparator<Note> {
    override fun compare(note1: Note, note2: Note): Int {
        return java.lang.Long.compare(note1.dateCreate, note2.dateCreate)
    }
}