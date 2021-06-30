package ru.geekbrains.notes;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.notes.note.Note;

public class GlobalVariables extends Application {

    private List<Note> notes;
    private List<Note> notesCloud;

    private int currentNote;

    private boolean viewNoteFragmentState;
    private Settings settings;

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public boolean isViewNoteFragmentState() {
        return viewNoteFragmentState;
    }

    public void setViewNoteFragmentState(boolean viewNoteFragmentState) {
        this.viewNoteFragmentState = viewNoteFragmentState;
    }

    public int getCurrentNote() {
        Note note = getNoteByNoteId(currentNote);
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

    public List<Note> getNotesWithText(String query) {
        List<Note> result = new ArrayList<>();
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getValue().toUpperCase().contains(query.toUpperCase()))
                result.add(notes.get(i));
        }
        return result;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    public void setNotesCloud(List<Note> notes) {
        this.notesCloud = notes;
    }

    public List<Note> getNotesCloud() {
        return notesCloud;
    }

    public int getScrollPositionByNoteId(int noteId){
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getID() == noteId) {
                return i;
            }
        }
        return 0;
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

    public Note getNoteByNoteId(int noteId){
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getID() == noteId) {
                return notes.get(i);
            }
        }
        return new Note();
    }



    public void setNoteCloudById(int noteId, Note note){
        for (int i = 0; i < notesCloud.size(); i++) {
            if (notesCloud.get(i).getID() == noteId) {
                notesCloud.set(i, note);
            }
        }
    }

    public Note getNoteCloudByNoteId(int noteId){
        for (int i = 0; i < notesCloud.size(); i++) {
            if (notesCloud.get(i).getID() == noteId) {
                return notesCloud.get(i);
            }
        }
        return new Note();
    }


}
