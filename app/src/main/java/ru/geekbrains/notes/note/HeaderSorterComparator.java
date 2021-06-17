package ru.geekbrains.notes.note;

import java.util.Comparator;

public class HeaderSorterComparator implements Comparator<Note> {
    @Override
    public int compare(Note note1, Note note2) {
        return note1.getValue().compareTo(note2.getValue());
    }
}
