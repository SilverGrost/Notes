package ru.geekbrains.notes.ui.item

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import ru.geekbrains.notes.Constant
import ru.geekbrains.notes.GlobalVariables
import ru.geekbrains.notes.R
import ru.geekbrains.notes.Settings
import ru.geekbrains.notes.note.Callback
import ru.geekbrains.notes.note.NotesCloudRepositoryImpl
import ru.geekbrains.notes.note.NotesLocalRepositoryImpl
import ru.geekbrains.notes.note.NotesRepository
import ru.geekbrains.notes.observer.Publisher
import ru.geekbrains.notes.observer.PublisherHolder
import ru.geekbrains.notes.ui.MainActivity
import ru.geekbrains.notes.ui.auth.AuthFragment.Companion.checkCloudStatusByUserName
import java.util.*

class EditNoteFragmentDialog : DialogFragment(), View.OnClickListener {
    var noteId = 0
    private var editTextNoteValue: EditText? = null
    private var publisher: Publisher? = null
    var editFragment: View? = null
        private set
    private var newNoteId = -1
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.v("Debug1", "EditNoteFragmentDialog onAttach")
        MainActivity.setTitle(activity, "Правка заметки")
        if (context is PublisherHolder) {
            publisher = (context as PublisherHolder).publisher
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.v("Debug1", "EditNoteFragmentDialog onDetach")
        publisher = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.v("Debug1", "EditNoteFragment onCreateView")
        setHasOptionsMenu(false)
        val v = inflater.inflate(R.layout.fragment_edit_note_dialog, container, false)
        val buttonOk = v.findViewById<Button>(R.id.button_ok)
        buttonOk.setOnClickListener(this)
        val buttonCancel = v.findViewById<Button>(R.id.button_cancel)
        buttonCancel.setOnClickListener(this)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v("Debug1", "EditNoteFragment onViewCreated")
        editFragment = view
        fillEditNote(view)
    }

    fun fillEditNote(view: View) {
        Log.v("Debug1", "EditNoteFragment fillEditNote")
        if (arguments != null && activity != null) {
            noteId = requireArguments().getInt(ARG, 0)
            Log.v("Debug1", "EditNoteFragment fillEditNote getArguments() != null noteId=$noteId")
            val note = (requireActivity().application as GlobalVariables).getNoteByNoteId(noteId)
            editTextNoteValue = view.findViewById(R.id.editTextNoteValue)
            val settings: Settings
            if (activity != null) {
                settings = (requireActivity().application as GlobalVariables).settings
                with(editTextNoteValue) {
                    this?.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.textSize)
                    this?.setText(note.value)
                }
            }
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onClick(v: View) {
        Log.v("Debug1", "EditNoteFragmentDialog onClick")
        if (v.id == R.id.button_ok) {
            Log.v("Debug1", "EditNoteFragmentDialog onClick button_ok noteId=$noteId")
            val value = editTextNoteValue!!.text.toString()
            val date = Date()
            if (activity != null) {
                val notes = (activity!!.application as GlobalVariables).notes
                val note = (activity!!.application as GlobalVariables).getNoteByNoteId(noteId)
                note.dateEdit = date.toInstant().epochSecond
                note.value = value
                var cloudSync = false
                var authTypeService = 0
                var userName: String? = ""
                if (activity != null) {
                    //Получаем настройки из глобальной переменной
                    val settings = (activity!!.application as GlobalVariables).settings
                    authTypeService = settings.authTypeService
                    userName = checkCloudStatusByUserName(settings, context, activity!!)
                    if (userName != null && userName != "") {
                        cloudSync = true
                    }
                }
                val localRepository: NotesRepository = NotesLocalRepositoryImpl(context!!, activity!!)
                val cloudRepository: NotesRepository = NotesCloudRepositoryImpl(authTypeService, userName!!)
                if (note.iD == -1) {
                    note.dateCreate = date.toInstant().epochSecond
                    newNoteId = (activity!!.application as GlobalVariables).newId
                    note.iD = newNoteId
                    val finalCloudSync = cloudSync
                    localRepository.addNote(notes, note, object : Callback<Any> {
                        override fun onSuccess(result: Any) {
                            Log.v("Debug1", "EditNoteFragmentDialog onClick button_ok localRepository addNote")
                            if (finalCloudSync) {
                                //Добавялем в облако и получаем облачный id
                                cloudRepository.addNote(notes, note, object : Callback<Any> {
                                    override fun onSuccess(result: Any) {
                                        val result13 = null
                                        note.idCloud = result13 as String
                                        Log.v("Debug1", "EditNoteFragmentDialog onClick button_ok cloudRepository addNote result=$result13")

                                        //Обнавляем в локальном репозитории полученный облачный id
                                        localRepository.updateNote(notes, note, object : Callback<Any> {
                                            override fun onSuccess(result: Any) {
                                                Log.v("Debug1", "EditNoteFragmentDialog onClick button_ok localRepository updateNote")
                                            }
                                        })

                                        //Обнавляем в облачном репозитории полученный облачный id
                                        cloudRepository.updateNote(notes, note, object : Callback<Any> {
                                            override fun onSuccess(result: Any) {
                                                Log.v("Debug1", "EditNoteFragmentDialog onClick button_ok cloudRepository updateNote")
                                            }
                                        })
                                    }
                                })
                            }
                        }
                    })
                } else {
                    localRepository.updateNote(notes, note, object : Callback<Any> {
                        override fun onSuccess(result: Any) {
                            Log.v("Debug1", "EditNoteFragment onClick button_ok notify TYPE_EVENT_EDIT_NOTE")
                        }
                    })
                    if (cloudSync) {
                        cloudRepository.updateNote(notes, note, object : Callback<Any> {
                            override fun onSuccess(result: Any) {
                                Log.v("Debug1", "EditNoteFragment onClick button_ok notify cloudRepository update")
                            }
                        })
                    }
                }
                if (publisher != null) {
                    Log.v("Debug1", "EditNoteFragmentDialog onClick button_ok notify noteId=$noteId")
                    if (noteId == -1) publisher!!.notify(newNoteId, Constant.TYPE_EVENT_ADD_NOTE) else publisher!!.notify(noteId, Constant.TYPE_EVENT_EDIT_NOTE)
                }
            }
        } else if (v.id == R.id.button_ok) {
            dismiss()
        }
        dismiss()
        Log.v("Debug1", "EditNoteFragmentDialog onClick end")
    }

    companion object {
        private const val ARG = "NOTE_ID"
        const val TAG = "EditNoteFragmentDialog"
        @JvmStatic
        fun newInstance(noteId: Int): EditNoteFragmentDialog {
            Log.v("Debug1", "EditNoteFragmentDialog newInstance noteId=$noteId")
            val fragment = EditNoteFragmentDialog()
            val args = Bundle()
            args.putInt(ARG, noteId)
            fragment.arguments = args
            return fragment
        }
    }
}