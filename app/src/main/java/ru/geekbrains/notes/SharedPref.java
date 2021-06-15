package ru.geekbrains.notes;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.notes.note.Note;

import static ru.geekbrains.notes.Constant.*;

//Пока храню заметки через SharedPreferences. Понимаю, что криво, но потмо переделаю на БД
public class SharedPref {

    private final android.content.SharedPreferences SharedPreferences;

    public SharedPref(Context context) {
        this.SharedPreferences = context.getSharedPreferences(NAME_SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }

    // Чтение заметки
    private Note loadNote(int id) {
        Note note = new Note();
        note.setValue(SharedPreferences.getString(NOTEVALUE + id, note.getValue()));
        note.setHeader(SharedPreferences.getString(NOTEHEADER + id, note.getHeader()));
        note.setID(SharedPreferences.getInt(NOTEID + id, note.getID()));
        note.setDate(SharedPreferences.getLong(NOTEDATE + id, note.getDate()));
        return note;
    }

    // Чтение заметок
    public ArrayList<Note> loadNotes() {
        int countNotes = SharedPreferences.getInt(COUNTNOTES, 0);
        ArrayList<Note> notes = new ArrayList<>();
        //Note[] notes = new Note[countNotes];
        for (int i = 0; i < countNotes; i++) {
            notes.add(loadNote(i));
        }
        return notes;
    }

    // Сохранение заметки
    private void saveNote(Note note, int id) {
        SharedPreferences.Editor editor = SharedPreferences.edit();
        editor.putString(NOTEVALUE + id, note.getValue());
        editor.putString(NOTEHEADER + id, note.getHeader());
        editor.putInt(NOTEID + id, note.getID());
        editor.putLong(NOTEDATE + id, note.getDate());
        editor.apply();
    }

    // Сохранение заметок
    public void saveNotes(List<Note> notes) {
        SharedPreferences.Editor editor = SharedPreferences.edit();
        editor.putInt(COUNTNOTES, notes.size());
        for (int i = 0; i < notes.size(); i++) {
            saveNote(notes.get(i), i);
        }
        editor.apply();
    }
}
