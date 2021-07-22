package ru.geekbrains.notes.note.comparator

import ru.geekbrains.notes.note.Note
import java.util.*

class HeaderSorterComparator : Comparator<Note> {
    override fun compare(note1: Note, note2: Note): Int {
        return note1.value.toUpperCase().compareTo(note2.value.toUpperCase())
    }
}