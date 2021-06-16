package ru.geekbrains.notes.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.SharedPref;
import ru.geekbrains.notes.note.Note;

import static ru.geekbrains.notes.Constant.MILISECOND;


public class DatepickerFragment extends Fragment {

    private static final String ARG = "NOTE_ID";
    private int noteId;

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


            DatePicker datePicker = v.findViewById(R.id.datepicker);

            Note note = ((GlobalVariables) getActivity().getApplication()).getNoteById(noteId);

            long date = note.getDate() * MILISECOND;

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            datePicker.init(year, month, day, (view, year1, monthOfYear, dayOfMonth) -> {

                Log.v("Debug1", "DatepickerFragment onDateChanged into");

                Calendar calendarNew = Calendar.getInstance();
                calendarNew.set(year1, monthOfYear, dayOfMonth);
                long newDate = calendarNew.getTimeInMillis() / MILISECOND;

                if (DatepickerFragment.this.getActivity() != null) {
                    note.setDate(newDate);
                    ((GlobalVariables) getActivity().getApplication()).setNoteById(noteId, note);
                    List<Note> notes = ((GlobalVariables) getActivity().getApplication()).getNotes();
                    new SharedPref(DatepickerFragment.this.getActivity()).saveNotes(notes);

                    LinearLayout linearLayout = DatepickerFragment.this.getActivity().findViewById(R.id.linearLayoutIntoScrollViewIntoFragmentListNotes);
                    TextView textViewTop = linearLayout.findViewWithTag(note.getID());

                    Log.v("Debug1", "DatepickerFragment onDateChanged into note.getID()=" + note.getID());

                    DateFormat f = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
                    String dateStr = f.format(newDate * MILISECOND);

                    textViewTop.setText(dateStr);

                    //FragmentManager fragmentManager = getFragmentManager();
                    if (getActivity() != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.popBackStack();
                    }

                }
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
            Note note = ((GlobalVariables) getActivity().getApplication()).getNoteById(noteId);
            long date = note.getDate() / MILISECOND;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
        }

    }
}