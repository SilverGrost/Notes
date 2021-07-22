package ru.geekbrains.notes

import android.app.Application
import ru.geekbrains.notes.note.Note
import java.util.*

class GlobalVariables : Application() {
    private var notes: MutableList<Note>? = null
    private var notesCloud: MutableList<Note>? = null
    var currentNote = 0
        get() {
            val note = getNoteByNoteId(field)
            return if (note.iD != -1) field else notes!!.size - 1
        }
    var isViewNoteFragmentState = false
    var settings: Settings? = null
    fun getNotes(): List<Note>? {
        return notes
    }

    fun getNotesWithText(query: String): List<Note> {
        val result: MutableList<Note> = ArrayList()
        for (i in notes!!.indices) {
            if (notes!![i].value!!.toUpperCase().contains(query.toUpperCase())) result.add(notes!![i])
        }
        return result
    }

    fun setNotes(notes: MutableList<Note>?) {
        this.notes = notes
    }

    fun setNotesCloud(notes: MutableList<Note>?) {
        notesCloud = notes
    }

    fun getNotesCloud(): List<Note>? {
        return notesCloud
    }

    fun getScrollPositionByNoteId(noteId: Int): Int {
        for (i in notes!!.indices) {
            if (notes!![i].iD == noteId) {
                return i
            }
        }
        return 0
    }

    val newId: Int
        get() {
            var newId = 0
            if (notes!!.size > 0) {
                newId = notes!![0].iD
                for (i in 1 until notes!!.size) {
                    if (notes!![i].iD > newId) {
                        newId = notes!![i].iD
                    }
                }
                newId++
            }
            return newId
        }

    fun setNoteById(noteId: Int, note: Note) {
        for (i in notes!!.indices) {
            if (notes!![i].iD == noteId) {
                notes!![i] = note
            }
        }
    }

    fun getNoteByNoteId(noteId: Int): Note {
        for (i in notes!!.indices) {
            if (notes!![i].iD == noteId) {
                return notes!![i]
            }
        }
        return Note()
    }

    fun updateNoteCloudById(noteId: Int, note: Note) {
        for (i in notesCloud!!.indices) {
            if (notesCloud!![i].iD == noteId) {
                notesCloud!![i] = note
            }
        }
    }

    fun getNoteCloudByNoteId(noteId: Int): Note {
        for (i in notesCloud!!.indices) {
            if (notesCloud!![i].iD == noteId) {
                return notesCloud!![i]
            }
        }
        return Note()
    }
}