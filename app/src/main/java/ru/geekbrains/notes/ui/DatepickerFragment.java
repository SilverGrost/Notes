package ru.geekbrains.notes.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.List;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.SharedPref;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;

import static ru.geekbrains.notes.Constant.MILISECOND;


public class DatepickerFragment extends Fragment {

    private static final String ARG = "NOTE_ID";
    private int noteId;
    private Publisher publisher;
    int yearFromDP = 0;
    int monthOfYearFromDP = 0;
    int dayOfMonthFromDP = 0;

    public DatepickerFragment() {
        // Required empty public constructor
    }

    public static DatepickerFragment newInstance(int noteId) {
        Log.v("Debug1", "DatepickerFragment newInstance noteId=" + noteId);
        DatepickerFragment fragment = new DatepickerFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG, noteId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_datepicker, container, false);
        if (getArguments() != null && getActivity() != null) {
            noteId = getArguments().getInt(ARG, 0);
            Log.v("Debug1", "DatepickerFragment onCreateView noteId=" + noteId);

            Note note = ((GlobalVariables) getActivity().getApplication()).getNoteByNoteId(noteId);
            long date = note.getDateEdit() * MILISECOND;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            Button button_ok = v.findViewById(R.id.button_dp_ok);
            button_ok.setOnClickListener(v1 -> {
                Log.v("Debug1", "DatepickerFragment button_ok");
                Calendar calendarNew = Calendar.getInstance();
                calendarNew.set(yearFromDP, monthOfYearFromDP, dayOfMonthFromDP);
                long newDate = calendarNew.getTimeInMillis() / MILISECOND;
                if (DatepickerFragment.this.getActivity() != null) {
                    note.setDateEdit(newDate);
                    ((GlobalVariables) DatepickerFragment.this.getActivity().getApplication()).setNoteById(noteId, note);
                    List<Note> notes = ((GlobalVariables) DatepickerFragment.this.getActivity().getApplication()).getNotes();
                    new SharedPref(DatepickerFragment.this.getActivity()).saveNotes(notes);
                    if (publisher != null) {
                        publisher.notify(noteId, 3);
                    }
                    if (DatepickerFragment.this.getActivity() != null) {
                        FragmentManager fragmentManager = DatepickerFragment.this.getActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();
                    }
                }
            });

            Button button_cancel = v.findViewById(R.id.button_dp_cancel);
            button_cancel.setOnClickListener(v12 -> {
                if (DatepickerFragment.this.getActivity() != null) {
                    FragmentManager fragmentManager = DatepickerFragment.this.getActivity().getSupportFragmentManager();
                    fragmentManager.popBackStack();
                }
            });

            DatePicker datePicker = v.findViewById(R.id.datepicker);
            datePicker.init(year, month, day, (view, year1, monthOfYear, dayOfMonth) -> {
                Log.v("Debug1", "DatepickerFragment onDateChanged into");
                yearFromDP = year1;
                monthOfYearFromDP = monthOfYear;
                dayOfMonthFromDP = dayOfMonth;
            });
        }
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("Debug1", "DatepickerFragment onViewCreated");
        if (getArguments() != null && getActivity() != null) {
            noteId = getArguments().getInt(ARG, 0);
            Log.v("Debug1", "DatepickerFragment onViewCreated noteId=" + noteId);
            Note note = ((GlobalVariables) getActivity().getApplication()).getNoteByNoteId(noteId);
            long date = note.getDateEdit() / MILISECOND;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Debug1", "DatepickerFragment onAttach");
        if (context instanceof PublisherHolder) {
            publisher = ((PublisherHolder) context).getPublisher();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Debug1", "DatepickerFragment onDetach");
        publisher = null;
    }
}