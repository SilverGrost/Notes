package ru.geekbrains.notes;

import android.app.Application;

import java.util.List;

import ru.geekbrains.notes.note.Note;

public class GlobalVariables extends Application {

    private List<Note> notes;

    private int currentNote;

    public int getCurrentNote() {
        Note note = getNoteById(currentNote);
        if (note.getID() != -1)
            return currentNote;
        else
            return notes.size() - 1;
    }

    public void setCurrentNote(int currentNote) {
        this.currentNote = currentNote;
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public Note getNoteById(int noteId){
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getID() == noteId) {
                return notes.get(i);
            }
        }
        return new Note();
    }

    public int getNewId(){
        int newId = 0;
        if (notes.size() > 0){
            newId = notes.get(0).getID();
            for (int i = 1; i < notes.size(); i++) {
                if (notes.get(i).getID() > newId) {
                    newId = notes.get(i).getID();
                }
            }
            newId++;
        }
        return newId;
    }

    public void setNoteById(int noteId, Note note){
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getID() == noteId) {
                notes.set(i, note);
            }
        }
    }

}
