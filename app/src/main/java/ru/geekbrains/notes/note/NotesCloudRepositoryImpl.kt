package ru.geekbrains.notes.note

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import ru.geekbrains.notes.Constant
import java.util.*

class NotesCloudRepositoryImpl(authTypeService: Int, userName: String) : NotesRepository {
    private val collectionId: String
    private val firebaseFirestore = FirebaseFirestore.getInstance()
    override fun getNotes(callback: Callback<List<Note>>) {
        firebaseFirestore.collection(collectionId)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    val result = ArrayList<Note>()
                    if (task.isSuccessful) {
                        val resultTask = task.result ?: return@addOnCompleteListener
                        for (document in resultTask.documents) {
                            val value = document[VALUE] as String?
                            var date_create: Date? = null
                            var o: Any?
                            o = document[FIELD_DATE_CREATE]
                            if (o != null) date_create = (o as Timestamp).toDate()
                            var date_edit: Date? = null
                            o = document[FIELD_DATE_EDIT]
                            if (o != null) date_edit = (o as Timestamp).toDate()
                            var id: Long = 0
                            o = document[ID]
                            if (o != null) id = o as Long
                            val idCloud = document[IDCLOUD] as String?
                            val note = Note()
                            if (date_create != null) note.dateCreate = date_create.toInstant().epochSecond
                            if (date_edit != null) note.dateEdit = date_edit.toInstant().epochSecond
                            note.iD = id.toInt()
                            note.value = value
                            note.idCloud = idCloud
                            result.add(note)
                        }
                        callback.onSuccess(result)
                    } else {
                        task.exception
                    }
                }
    }

    override fun setNotes(notes: List<Note>, callback: Callback<Any>) {
        for (i in notes.indices) {
            addNote(notes, notes[i], callback)
        }
    }

    override fun clearNotes(notes: List<Note>, callback: Callback<Any>) {
        for (i in notes.indices) {
            if (notes[i].idCloud != null && notes[i].idCloud != "") {
                firebaseFirestore.collection(collectionId)
                        .document(notes[i].idCloud!!)
                        .delete()
                        .addOnCompleteListener { task: Task<Void?> ->
                            if (task.isSuccessful) {
                                callback.onSuccess(i)
                            }
                        }
            }
        }
    }

    override fun addNote(notes: List<Note>, note: Note, callback: Callback<Any>) {
        val data = HashMap<String, Any?>()
        data[ID] = note.iD
        val dateCreate = Date(note.dateCreate * Constant.MILISECOND)
        val dateEdit = Date(note.dateEdit * Constant.MILISECOND)
        data[FIELD_DATE_CREATE] = dateCreate
        data[FIELD_DATE_EDIT] = dateEdit
        data[VALUE] = note.value
        data[IDCLOUD] = note.idCloud
        Log.v("Debug1", "NotesCloudRepositoryImpl addNote note.getID()=" + note.iD + ", note.getValue()=" + note.value + ", note.getIdCloud()=" + note.idCloud)
        firebaseFirestore.collection(collectionId)
                .add(data)
                .addOnCompleteListener { task: Task<DocumentReference?> ->
                    if (task.isSuccessful) {
                        if (task.result != null) {
                            val idCloud = task.result!!.id
                            callback.onSuccess(idCloud)
                        }
                    }
                }
    }

    override fun removeNote(notes: List<Note>, note: Note, callback: Callback<Any>) {
        Log.v("Debug1", "NotesCloudRepositoryImpl removeNote note.getID()=" + note.iD + ", note.getValue()=" + note.value + ", note.getIdCloud()" + note.idCloud)
        if (note.idCloud != null && note.idCloud != "") {
            firebaseFirestore.collection(collectionId)
                    .document(note.idCloud!!)
                    .delete()
                    .addOnCompleteListener { task: Task<Void?> ->
                        if (task.isSuccessful) {
                            callback.onSuccess(note)
                        }
                    }
        }
    }

    override fun updateNote(notes: List<Note>, note: Note, callback: Callback<Any>) {
        if (note.idCloud != null && note.idCloud != "") {
            val data = HashMap<String, Any?>()
            data[ID] = note.iD
            val dateCreate = Date(note.dateCreate * Constant.MILISECOND)
            val dateEdit = Date(note.dateEdit * Constant.MILISECOND)
            data[FIELD_DATE_CREATE] = dateCreate
            data[FIELD_DATE_EDIT] = dateEdit
            data[VALUE] = note.value
            data[IDCLOUD] = note.idCloud
            Log.v("Debug1", "NotesCloudRepositoryImpl updateNote note.getID()=" + note.iD + ", note.getValue()=" + note.value + ", note.getIdCloud()" + note.idCloud)
            if (note.idCloud != null && note.idCloud != "") firebaseFirestore.collection(collectionId)
                    .document(note.idCloud!!)
                    .update(data)
                    .addOnCompleteListener { task: Task<Void?>? -> callback.onSuccess(note) }
        }
    }

    companion object {
        private const val FIELD_DATE_CREATE = "date_create"
        private const val FIELD_DATE_EDIT = "date_edit"
        private const val VALUE = "value"
        private const val ID = "id"
        private const val IDCLOUD = "idCloud"
    }

    init {
        collectionId = authTypeService.toString() + "_" + userName
    }
}