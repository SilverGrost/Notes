package ru.geekbrains.notes

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import ru.geekbrains.notes.Constant.APPSETTINGSCLOUDSYNC
import ru.geekbrains.notes.Constant.APPSETTINGSCURRENTPOSITION
import ru.geekbrains.notes.Constant.APPSETTINGSMAXCOUNTLINES
import ru.geekbrains.notes.Constant.APPSETTINGSSORTTYPE
import ru.geekbrains.notes.Constant.APPSETTINGSTEXTSIZE
import ru.geekbrains.notes.Constant.AUTHTYPESERVICE
import ru.geekbrains.notes.Constant.COUNTNOTES
import ru.geekbrains.notes.Constant.DEFAULTAUTHTYPESERVICE
import ru.geekbrains.notes.Constant.DEFAULTCLOUDSYNC
import ru.geekbrains.notes.Constant.DEFAULTCURRENTPOSITION
import ru.geekbrains.notes.Constant.DEFAULTLMAXCOUNTLINESID
import ru.geekbrains.notes.Constant.DEFAULTSORTTYPEID
import ru.geekbrains.notes.Constant.DEFAULTTEXTSIZEID
import ru.geekbrains.notes.Constant.DEFAULTUSERNAMEVK
import ru.geekbrains.notes.Constant.NAME_SHARED_PREFERENCE
import ru.geekbrains.notes.Constant.NOTEDATE
import ru.geekbrains.notes.Constant.NOTEDATECREATE
import ru.geekbrains.notes.Constant.NOTEID
import ru.geekbrains.notes.Constant.NOTEIDCLOUD
import ru.geekbrains.notes.Constant.NOTEVALUE
import ru.geekbrains.notes.Constant.USERNAMEVK
import ru.geekbrains.notes.note.Note
import java.util.*

//Пока храню заметки через SharedPreferences. Понимаю, что криво, но потом переделаю на БД
class SharedPref(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(NAME_SHARED_PREFERENCE, Context.MODE_PRIVATE)

    // Чтение заметки
    private fun loadNote(id: Int): Note {
        val note = Note()
        note.value = sharedPreferences.getString(NOTEVALUE + id, note.value)
        note.iD = sharedPreferences.getInt(NOTEID + id, note.iD)
        note.dateEdit = sharedPreferences.getLong(NOTEDATE + id, note.dateEdit)
        note.dateCreate = sharedPreferences.getLong(NOTEDATECREATE + id, note.dateCreate)
        note.idCloud = sharedPreferences.getString(NOTEIDCLOUD + id, note.idCloud)
        return note
    }

    // Чтение заметок
    fun loadNotes(): ArrayList<Note> {
        val countNotes = sharedPreferences.getInt(COUNTNOTES, 0)
        val notes = ArrayList<Note>()
        for (i in 0 until countNotes) {
            notes.add(loadNote(i))
        }
        return notes
    }

    // Сохранение заметки
    private fun saveNote(note: Note, id: Int) {
        val editor = sharedPreferences.edit()
        editor.putString(NOTEVALUE + id, note.value)
        editor.putInt(NOTEID + id, note.iD)
        editor.putLong(NOTEDATE + id, note.dateEdit)
        editor.putLong(NOTEDATECREATE + id, note.dateCreate)
        editor.putString(NOTEIDCLOUD + id, note.idCloud)
        editor.apply()
    }

    // Сохранение заметок
    fun saveNotes(notes: List<Note>) {
        Log.v("Debug1", "SharedPref saveNotes start")
        val editor = sharedPreferences.edit()
        editor.putInt(COUNTNOTES, notes.size)
        for (i in notes.indices) {
            saveNote(notes[i], i)
        }
        editor.apply()
        Log.v("Debug1", "SharedPref saveNotes end")
    }

    // Чтение настроек
    fun loadSettings(): Settings {
        val settings = Settings()
        settings.orderType = sharedPreferences.getInt(APPSETTINGSSORTTYPE, DEFAULTSORTTYPEID)
        settings.textSizeId = sharedPreferences.getInt(APPSETTINGSTEXTSIZE, DEFAULTTEXTSIZEID)
        settings.maxCountLinesId = sharedPreferences.getInt(APPSETTINGSMAXCOUNTLINES, DEFAULTLMAXCOUNTLINESID)
        settings.currentPosition = sharedPreferences.getInt(APPSETTINGSCURRENTPOSITION, DEFAULTCURRENTPOSITION)
        settings.isCloudSync = sharedPreferences.getBoolean(APPSETTINGSCLOUDSYNC, DEFAULTCLOUDSYNC)
        settings.authTypeService = sharedPreferences.getInt(AUTHTYPESERVICE, DEFAULTAUTHTYPESERVICE)
        settings.userNameVK = sharedPreferences.getString(USERNAMEVK, DEFAULTUSERNAMEVK)
        return settings
    }

    // Сохранение настроек
    fun saveSettings(settings: Settings) {
        val editor = sharedPreferences.edit()
        editor.putInt(APPSETTINGSTEXTSIZE, settings.textSizeId)
        editor.putInt(APPSETTINGSSORTTYPE, settings.orderType)
        editor.putInt(APPSETTINGSMAXCOUNTLINES, settings.maxCountLinesId)
        editor.putInt(APPSETTINGSCURRENTPOSITION, settings.currentPosition)
        editor.putBoolean(APPSETTINGSCLOUDSYNC, settings.isCloudSync)
        editor.putInt(AUTHTYPESERVICE, settings.authTypeService)
        editor.putString(USERNAMEVK, settings.userNameVK)
        editor.apply()
    }

}