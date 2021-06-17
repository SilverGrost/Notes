package ru.geekbrains.notes.note;

import java.util.Comparator;

public class DateSorterComparator implements Comparator<Note> {
    @Override
    public int compare(Note note1, Note note2) {
        return Long.compare(note1.getDate(), note2.getDate());
    }
}
