package ru.geekbrains.notes.note;

import java.util.Comparator;

public class DateCreateSorterComparator implements Comparator<Note> {
    @Override
    public int compare(Note note1, Note note2) {
        return Long.compare(note1.getDateCreate(), note2.getDateCreate());
    }
}
