package ru.geekbrains.notes;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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

import ru.geekbrains.notes.domain.note.Note;
import ru.geekbrains.notes.ui.SharedPref;

import static ru.geekbrains.notes.Constant.MILISECOND;


public class DatepickerFragment extends Fragment {

    private static final String ARG = "NOTE_ID";

    public DatepickerFragment() {
        // Required empty public constructor
    }

    public static DatepickerFragment newInstance(Note note) {
        Log.v("Debug1", "DatepickerFragment newInstance");
        DatepickerFragment fragment = new DatepickerFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG, note);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("Debug1", "DatepickerFragment onCreateView");

        View v = inflater.inflate(R.layout.fragment_datepicker, container, false);
        DatePicker datePicker = v.findViewById(R.id.datepicker);

        if (getArguments() != null) {
            Note note;
            note = getArguments().getParcelable(ARG);
            long date = note.getDate() * MILISECOND;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            datePicker.init(year, month, day, (view, year1, monthOfYear, dayOfMonth) -> {

                Log.v("Debug1", "DatepickerFragment onDateChanged into");

                Note note1 = getArguments().getParcelable(ARG);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(year1, monthOfYear, dayOfMonth);
                long newDate = calendar1.getTimeInMillis() / 1000;

                if (getActivity() != null) {
                    List<Note> notes = ((MyApplication) getActivity().getApplication()).getNotes();
                    note1.setDate(newDate);
                    notes.set(note1.getID(), note1);
                    ((MyApplication) getActivity().getApplication()).setNotes(notes);

                    new SharedPref(getActivity()).saveNotes(notes);

                    //getFragmentManager().beginTransaction().remove(DatepickerFragment.this).commit();

                    if (getFragmentManager() != null) {
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.remove(DatepickerFragment.this);
                        fragmentTransaction.commit();

                        LinearLayout linearLayout = getActivity().findViewById(R.id.linearLayoutIntoScrollViewCont);
                        TextView textViewTop = linearLayout.findViewWithTag(note.getID());

                        Log.v("Debug1", "DatepickerFragment onDateChanged into note.getID()=" + note.getID());

                        DateFormat f = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
                        String dateStr = f.format(newDate * 1000);

                        textViewTop.setText(dateStr);
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

        if (getArguments() != null) {
            Note note;
            note = getArguments().getParcelable(ARG);
            long date = note.getDate() / MILISECOND;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
        }
    }
}