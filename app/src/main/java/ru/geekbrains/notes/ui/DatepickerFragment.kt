package ru.geekbrains.notes.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import ru.geekbrains.notes.Constant.MILISECOND
import ru.geekbrains.notes.GlobalVariables
import ru.geekbrains.notes.R
import ru.geekbrains.notes.SharedPref
import ru.geekbrains.notes.observer.Publisher
import ru.geekbrains.notes.observer.PublisherHolder
import java.util.*

class DatepickerFragment : Fragment() {
    private var noteId = 0
    private var publisher: Publisher? = null
    private var yearFromDP = 0
    private var monthOfYearFromDP = 0
    private var dayOfMonthFromDP = 0
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_datepicker, container, false)
        if (arguments != null && activity != null) {
            noteId = requireArguments().getInt(ARG, 0)
            Log.v("Debug1", "DatepickerFragment onCreateView noteId=$noteId")
            val note = (requireActivity().application as GlobalVariables).getNoteByNoteId(noteId)
            val date: Long = note.dateEdit * MILISECOND
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date
            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH]
            val day = calendar[Calendar.DAY_OF_MONTH]
            val buttonOk = v.findViewById<Button>(R.id.button_dp_ok)
            buttonOk.setOnClickListener {
                Log.v("Debug1", "DatepickerFragment button_ok")
                val calendarNew = Calendar.getInstance()
                calendarNew[yearFromDP, monthOfYearFromDP] = dayOfMonthFromDP
                val newDate: Long = calendarNew.timeInMillis / MILISECOND
                if (this@DatepickerFragment.activity != null) {
                    note.dateEdit = newDate
                    (this@DatepickerFragment.activity!!.application as GlobalVariables).setNoteById(noteId, note)
                    val notes = (this@DatepickerFragment.activity!!.application as GlobalVariables).getNotes()
                    SharedPref(this@DatepickerFragment.activity!!).saveNotes(notes!!)
                    if (publisher != null) {
                        publisher!!.notify(noteId, 3)
                    }
                    if (this@DatepickerFragment.activity != null) {
                        val fragmentManager = this@DatepickerFragment.activity!!.supportFragmentManager
                        fragmentManager.popBackStack()
                    }
                }
            }
            val buttonCancel = v.findViewById<Button>(R.id.button_dp_cancel)
            buttonCancel.setOnClickListener {
                if (this@DatepickerFragment.activity != null) {
                    val fragmentManager = this@DatepickerFragment.activity!!.supportFragmentManager
                    fragmentManager.popBackStack()
                }
            }
            val datePicker = v.findViewById<DatePicker>(R.id.datepicker)
            datePicker.init(year, month, day) { _: DatePicker?, year1: Int, monthOfYear: Int, dayOfMonth: Int ->
                Log.v("Debug1", "DatepickerFragment onDateChanged into")
                yearFromDP = year1
                monthOfYearFromDP = monthOfYear
                dayOfMonthFromDP = dayOfMonth
            }
        }
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v("Debug1", "DatepickerFragment onViewCreated")
        if (arguments != null && activity != null) {
            noteId = requireArguments().getInt(ARG, 0)
            Log.v("Debug1", "DatepickerFragment onViewCreated noteId=$noteId")
            val note = (requireActivity().application as GlobalVariables).getNoteByNoteId(noteId)
            val date: Long = note.dateEdit / MILISECOND
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = date
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        MainActivity.setTitle(activity, "Правка даты")
        Log.v("Debug1", "DatepickerFragment onAttach")
        if (context is PublisherHolder) {
            publisher = (context as PublisherHolder).publisher
        }
    }

    override fun onDetach() {
        super.onDetach()
        Log.v("Debug1", "DatepickerFragment onDetach")
        publisher = null
    }

    companion object {
        private const val ARG = "NOTE_ID"
        const val TAG = "DatepickerFragment"
        @JvmStatic
        fun newInstance(noteId: Int): DatepickerFragment {
            Log.v("Debug1", "DatepickerFragment newInstance noteId=$noteId")
            val fragment = DatepickerFragment()
            val bundle = Bundle()
            bundle.putInt(ARG, noteId)
            fragment.arguments = bundle
            return fragment
        }
    }
}