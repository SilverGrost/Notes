package ru.geekbrains.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.notes.note.Note;

import static ru.geekbrains.notes.Constant.*;

//Пока храню заметки через SharedPreferences. Понимаю, что криво, но потом переделаю на БД
public class SharedPref {

    private final android.content.SharedPreferences SharedPreferences;

    public SharedPref(Context context) {
        this.SharedPreferences = context.getSharedPreferences(NAME_SHARED_PREFERENCE, Context.MODE_PRIVATE);
    }

    // Чтение заметки
    private Note loadNote(int id) {
        Note note = new Note();
        note.setValue(SharedPreferences.getString(NOTEVALUE + id, note.getValue()));
        note.setID(SharedPreferences.getInt(NOTEID + id, note.getID()));
        note.setDateEdit(SharedPreferences.getLong(NOTEDATE + id, note.getDateEdit()));
        note.setDateCreate(SharedPreferences.getLong(NOTEDATECREATE + id, note.getDateCreate()));
        note.setIdCloud(SharedPreferences.getString(NOTEIDCLOUD + id, note.getIdCloud()));
        return note;
    }

    // Чтение заметок
    public ArrayList<Note> loadNotes() {
        int countNotes = SharedPreferences.getInt(COUNTNOTES, 0);
        ArrayList<Note> notes = new ArrayList<>();
        for (int i = 0; i < countNotes; i++) {
            notes.add(loadNote(i));
        }
        return notes;
    }

    // Сохранение заметки
    private void saveNote(Note note, int id) {
        SharedPreferences.Editor editor = SharedPreferences.edit();
        editor.putString(NOTEVALUE + id, note.getValue());
        editor.putInt(NOTEID + id, note.getID());
        editor.putLong(NOTEDATE + id, note.getDateEdit());
        editor.putLong(NOTEDATECREATE + id, note.getDateCreate());
        editor.putString(NOTEIDCLOUD + id, note.getIdCloud());

        editor.apply();
    }

    // Сохранение заметок
    public void saveNotes(List<Note> notes) {
        Log.v("Debug1", "SharedPref saveNotes start");
        SharedPreferences.Editor editor = SharedPreferences.edit();
        editor.putInt(COUNTNOTES, notes.size());
        for (int i = 0; i < notes.size(); i++) {
            saveNote(notes.get(i), i);
        }
        editor.apply();
        Log.v("Debug1", "SharedPref saveNotes end");
    }

    // Чтение настроек
    public Settings loadSettings() {
        Settings settings = new Settings();
        settings.setOrderType(SharedPreferences.getInt(APPSETTINGSSORTTYPE, DEFAULTSORTTYPEID));
        settings.setTextSizeId(SharedPreferences.getInt(APPSETTINGSTEXTSIZE, DEFAULTTEXTSIZEID));
        settings.setMaxCountLinesId(SharedPreferences.getInt(APPSETTINGSMAXCOUNTLINES, DEFAULTLMAXCOUNTLINESID));
        settings.setCurrentPosition(SharedPreferences.getInt(APPSETTINGSCURRENTPOSITION, DEFAULTCURRENTPOSITION));
        settings.setCloudSync(SharedPreferences.getBoolean(APPSETTINGSCLOUDSYNC, DEFAULTCLOUDSYNC));
        settings.setAuthTypeService(SharedPreferences.getInt(AUTHTYPESERVICE, DEFAULTAUTHTYPESERVICE));
        settings.setUserNameVK(SharedPreferences.getString(USERNAMEVK, DEFAULTUSERNAMEVK));
        return settings;
    }

    // Сохранение настроек
    public void saveSettings(Settings settings) {
        SharedPreferences.Editor editor = SharedPreferences.edit();
        editor.putInt(APPSETTINGSTEXTSIZE, settings.getTextSizeId());
        editor.putInt(APPSETTINGSSORTTYPE, settings.getOrderType());
        editor.putInt(APPSETTINGSMAXCOUNTLINES, settings.getMaxCountLinesId());
        editor.putInt(APPSETTINGSCURRENTPOSITION, settings.getCurrentPosition());
        editor.putBoolean(APPSETTINGSCLOUDSYNC, settings.isCloudSync());
        editor.putInt(AUTHTYPESERVICE, settings.getAuthTypeService());
        editor.putString(USERNAMEVK, settings.getUserNameVK());
        editor.apply();
    }
}
