package ru.geekbrains.notes.note

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import ru.geekbrains.notes.GlobalVariables
import ru.geekbrains.notes.SharedPref
import java.util.concurrent.Executors

class NotesLocalRepositoryImpl(private val context: Context, private val activity: Activity) : NotesRepository {
    private val executor = Executors.newCachedThreadPool()
    private val handler = Handler(Looper.getMainLooper())
    override fun getNotes(callback: Callback<List<Note>>) {
        executor.execute {
            handler.post {
                val notes: List<Note> = SharedPref(context).loadNotes()
                callback.onSuccess(notes)
            }
        }
    }

    override fun setNotes(notes: List<Note>, callback: Callback<Any>) {
        executor.execute {
            SharedPref(context).saveNotes(notes)
            handler.post { callback.onSuccess(notes) }
        }
    }

    override fun clearNotes(notes: MutableList<Note>, callback: Callback<Any>) {
        notes.clear()
        (activity.application as GlobalVariables).notes = notes
        setNotes(notes, callback)
    }

    override fun addNote(notes: MutableList<Note>, note: Note, callback: Callback<Any>) {
        notes.add(note)
        (activity.application as GlobalVariables).notes = notes
        setNotes(notes, callback)
    }

    override fun removeNote(notes:  MutableList <Note>, note: Note, callback: Callback<Any>) {
        executor.execute {
            for (i in notes.indices) {
                if (notes[i].iD == note.iD) {
                    notes.removeAt(i)
                    (activity.application as GlobalVariables).notes = notes
                    break
                }
            }
            SharedPref(context).saveNotes(notes)
            handler.post { callback.onSuccess(true) }
        }
    }

    override fun updateNote(notes: MutableList<Note>, note: Note, callback: Callback<Any>) {
        for (i in notes.indices) {
            if (notes[i].iD == note.iD) {
                notes[i] = note
                (activity.application as GlobalVariables).notes = notes
                setNotes(notes, callback)
                break
            }
        }
    }
}