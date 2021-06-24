package ru.geekbrains.notes.note.comparator;

import java.util.Comparator;

import ru.geekbrains.notes.note.Note;

public class DateEditSorterComparator implements Comparator<Note> {
    @Override
    public int compare(Note note1, Note note2) {
        return Long.compare(note1.getDateEdit(), note2.getDateEdit());
    }
}
