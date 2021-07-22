package ru.geekbrains.notes.note.comparator;

import java.util.Comparator;

import ru.geekbrains.notes.note.Note;

public class HeaderSorterComparator implements Comparator<Note> {
    @Override
    public int compare(Note note1, Note note2) {
        return note1.getValue().toUpperCase().compareTo(note2.getValue().toUpperCase());
    }
}
