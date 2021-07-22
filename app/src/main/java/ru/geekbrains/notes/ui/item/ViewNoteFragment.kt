package ru.geekbrains.notes.ui.item

import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import ru.geekbrains.notes.Constant
import ru.geekbrains.notes.GlobalVariables
import ru.geekbrains.notes.R
import ru.geekbrains.notes.Settings
import ru.geekbrains.notes.note.*
import ru.geekbrains.notes.observer.ObserverNote
import ru.geekbrains.notes.observer.Publisher
import ru.geekbrains.notes.observer.PublisherHolder
import ru.geekbrains.notes.ui.MainActivity
import ru.geekbrains.notes.ui.auth.AuthFragment.Companion.checkCloudStatusByUserName

class ViewNoteFragment : Fragment(), ObserverNote {
    private var noteId = 0
    private var publisher: Publisher? = null
    private var publisher2: Publisher? = null
    private var viewFragment: View? = null

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.view_note_fragment, menu)

        //Убираем меню главного фрагмента в портретной орииентации
        if (activity != null) {
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                val itemActionSearch = menu.findItem(R.id.action_search)
                if (itemActionSearch != null) itemActionSearch.isVisible = false
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.v("Debug1", "MainActivity onOptionsItemSelected")
        // Обработка выбора пункта меню приложения (активити)
        val id = item.itemId
        if (id == R.id.action_edit) {
            buttonEditAction()
            return true
        } else if (id == R.id.action_delete) {
            //deleteNote();
            showAlertDialogDeleteNote()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("Debug1", "ViewNoteFragment onCreate")
        if (arguments != null) {
            noteId = requireArguments().getInt(ARG, 0)
            Log.v("Debug1", "ViewNoteFragment onCreate getArguments() != null noteId=$noteId")
        } else {
            if (activity != null && requireActivity().application != null) {
                noteId = (requireActivity().application as GlobalVariables).currentNote
                Log.v("Debug1", "ViewNoteFragment onCreate getArguments() == null noteId=$noteId")
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MainActivity.setTitle(activity, "Просмотр заметки")
        Log.v("Debug1", "ViewNoteFragment onAttach context=$context")
        if (context is PublisherHolder) {
            publisher = (context as PublisherHolder).publisher
        }
        if (context is PublisherHolder) {
            publisher2 = (context as PublisherHolder).publisher
            publisher2!!.subscribe(this)
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.v("Debug1", "ViewNoteFragment onDetach")
        publisher = null
        publisher2 = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_view_note, container, false)
        if (activity != null) {
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setHasOptionsMenu(true)
            } else {
                val parentFragment = parentFragment
                if (parentFragment == null) {
                    setHasOptionsMenu(activity != null)
                }
            }
        }
        Log.v("Debug1", "ViewNoteFragment onCreateView getArguments() != null noteId=$noteId, container=$container")
        return v
    }

    fun fillViewNote(noteId: Int) {
        Log.v("Debug1", "ViewNoteFragment fillViewNote")
        this.noteId = noteId
        if (activity != null) {
            (requireActivity().application as GlobalVariables).currentNote = noteId
            val textViewNoteValue = requireView().findViewById<TextView>(R.id.viewTextNoteValue)
            Log.v("Debug1", "ViewNoteFragment fillViewNote noteId=$noteId")
            if (noteId != -1) {
                val note = (requireActivity().application as GlobalVariables).getNoteByNoteId(noteId)
                textViewNoteValue.text = note.value
            } else textViewNoteValue.text = ""
            var settings = Settings()
            if (activity != null) {
                //Получаем настройки из глобальной переменной
                settings = (requireActivity().application as GlobalVariables).settings
            }
            textViewNoteValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.textSize)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v("Debug1", "ViewNoteFragment onViewCreated")
        viewFragment = view
        if (arguments != null) {
            noteId = requireArguments().getInt(ARG, 0)
            Log.v("Debug1", "ViewNoteFragment onViewCreated getArguments() != null noteId=$noteId")
        } else {
            Log.v("Debug1", "ViewNoteFragment onViewCreated getArguments() == null noteId=$noteId")
        }
    }

    private fun buttonEditAction() {
        Log.v("Debug1", "ViewNoteFragment buttonEditAction")
        EditNoteFragmentDialog.newInstance(noteId)
                .show(childFragmentManager, EditNoteFragmentDialog.TAG)
    }

    private fun showAlertDialogDeleteNote() {
        AlertDialog.Builder(requireContext())
                .setTitle("ВНИМАНИЕ!")
                .setMessage("Вы действительно хотите удалить заметку?")
                .setIcon(R.drawable.ic_clear)
                .setCancelable(false)
                .setPositiveButton("Да") { _: DialogInterface?, _: Int -> deleteNote() }
                .setNegativeButton("Нет") { _: DialogInterface?, _: Int -> }.apply {
                    show()
                }
    }

    private fun deleteNote() {
        if (activity != null && requireActivity().application != null) {
            val notes = (requireActivity().application as GlobalVariables).notes
            var prevID = 0
            var position = 0
            var note: Note? = Note()
            for (i in notes.indices) {
                if (notes[i]!!.iD == noteId) {
                    note = notes[i]
                    notes.removeAt(i)
                    break
                }
                prevID = notes[i]!!.iD
                position = i
            }
            Log.v("Debug1", "ViewNoteFragment deleteNote prevID=$prevID")
            var cloudSync = false
            var authTypeService = 0
            var userName: String? = ""
            if (activity != null) {
                //Получаем настройки из глобальной переменной
                val settings = (requireActivity().application as GlobalVariables).settings
                authTypeService = settings.authTypeService
                userName = checkCloudStatusByUserName(settings, context, requireActivity())
                if (userName != null && userName != "") {
                    cloudSync = true
                }
            }
            Log.v("Debug1", "ViewNoteFragment deleteNote cloudSync=$cloudSync")
            val localRepository: NotesRepository = NotesLocalRepositoryImpl(requireContext(), requireActivity())
            localRepository.removeNote(notes, note, object : Callback<Any> {
                override fun onSuccess(result: Any) {
                    Log.v("Debug1", "ViewNoteFragment deleteNote localRepository removeNote onSuccess")
                    if (view != null) Snackbar.make(view!!, "Заметка удалена с устройства", Snackbar.LENGTH_SHORT).show()
                }
            })
            if (cloudSync) {
                val cloudRepository: NotesRepository = NotesCloudRepositoryImpl(authTypeService, userName!!)
                cloudRepository.removeNote(notes, note, object : Callback<Any> {
                    override fun onSuccess(result: Any) {
                        Log.v("Debug1", "ViewNoteFragment deleteNote cloudRepository removeNote result=$result")
                        if (view != null) Snackbar.make(view!!, "Заметка удалена из облака", Snackbar.LENGTH_SHORT).show()
                    }
                })
            }
            if (publisher != null) {
                Log.v("Debug1", "ViewNoteFragment deleteNote notify")
                publisher!!.notify(position, Constant.TYPE_EVENT_DELETE_NOTE)
            }
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (activity != null) {
                    val fragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentManager.popBackStack()
                    fragmentTransaction.commit()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.v("Debug1", "ViewNoteFragment onStart")
        fillViewNote(noteId)
    }

    override fun onStop() {
        super.onStop()
        Log.v("Debug1", "ViewNoteFragment onStop")
    }

    override fun updateNote(noteID: Int, typeEvent: Int) {
        Log.v("Debug1", "ViewNoteFragment updateNote noteId=$noteId, typeEvent=$typeEvent")
        if (typeEvent != Constant.TYPE_EVENT_DELETE_NOTE) fillViewNote(noteId)
    }

    override fun onResume() {
        super.onResume()
        Log.v("Debug1", "ViewNoteFragment onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.v("Debug1", "ViewNoteFragment onPause")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.v("Debug1", "ViewNoteFragment onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.v("Debug1", "ViewNoteFragment onDestroy")
    }

    companion object {
        private const val ARG = "NOTE_ID"
        const val TAG = "ViewNoteFragment"
        @JvmStatic
        fun newInstance(noteID: Int): ViewNoteFragment {
            Log.v("Debug1", "ViewNoteFragment newInstance noteID=$noteID")
            val fragment = ViewNoteFragment()
            val bundle = Bundle()
            bundle.putInt(ARG, noteID)
            fragment.arguments = bundle
            return fragment
        }
    }
}