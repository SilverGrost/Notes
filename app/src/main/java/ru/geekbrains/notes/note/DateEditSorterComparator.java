package ru.geekbrains.notes.note;

import java.util.Comparator;

public class DateEditSorterComparator implements Comparator<Note> {
    @Override
    public int compare(Note note1, Note note2) {
        return Long.compare(note1.getDateEdit(), note2.getDateEdit());
    }
}
