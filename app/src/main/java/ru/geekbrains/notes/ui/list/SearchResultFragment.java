package ru.geekbrains.notes.ui.list;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.note.DateSorterComparator;
import ru.geekbrains.notes.note.HeaderSorterComparator;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.observer.ObserverNote;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;

import static ru.geekbrains.notes.Constant.MILISECOND;


public class SearchResultFragment extends Fragment implements ObserverNote {

    private View viewFragment;
    private Publisher publisher;

    public View getViewFragment() {
        return viewFragment;
    }



    @Override
    public void updateNote(int noteID) {
        Log.v("Debug1", "SearchResultFragment updateNote noteID=" + noteID);
        fillList(viewFragment, mParam1);
    }

    public interface OnNoteClicked {
        void onNoteClickedSearchList(int noteID);
    }

    private OnNoteClicked noteClicked;

    public interface onDateClicked {
        void onDateClickedSearchList(int noteID);
    }

    private onDateClicked onDateClicked;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Debug1", "SearchResultFragment onAttach");

        if (context instanceof OnNoteClicked) {
            noteClicked = (OnNoteClicked) context;
        }

        if (context instanceof onDateClicked) {
            onDateClicked = (onDateClicked) context;
        }

        if (context instanceof PublisherHolder) {
            publisher = ((PublisherHolder) context).getPublisher();
            publisher.subscribe(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Debug1", "SearchResultFragment onDetach");
        noteClicked = null;
        onDateClicked = null;
        if (publisher != null) {
            publisher.unsubscribe(this);
        }
    }

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    public static SearchResultFragment newInstance(String query) {
        Log.v("Debug1", "SearchResultFragment newInstance");
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, query);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Debug1", "SearchResultFragment onCreate");
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v("Debug1", "SearchResultFragment onCreateView");
        return inflater.inflate(R.layout.fragment_search_result, container, false);
    }

    // вызывается после создания макета фрагмента, здесь мы проинициализируем список
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("Debug1", "SearchResultFragment onViewCreated");
        viewFragment = view;
    }

    public void fillList(View view, String query) {
        Log.v("Debug1", "SearchResultFragment fillList");
        LinearLayout linearLayoutNotesList = view.findViewById(R.id.ListNotesFragment);
        LinearLayout linearLayoutIntoScrollViewIntoFragmentSearchResult = view.findViewById(R.id.linearLayoutIntoScrollViewIntoFragmentSearchResult);

        linearLayoutIntoScrollViewIntoFragmentSearchResult.removeAllViews();

        if (getActivity() != null && getActivity().getApplication() != null) {
            List<Note> notes = ((GlobalVariables) getActivity().getApplication()).getNotes();


            //Сортировка
            int textSortId = ((GlobalVariables) getActivity().getApplication()).getSortTypeId();
            Comparator<Note> dateSorter = new DateSorterComparator();
            Comparator<Note> headerSorter = new HeaderSorterComparator();
            switch (textSortId) {
                case (1):
                    notes.sort(dateSorter);
                    break;
                case (0):
                    notes.sort(dateSorter.reversed());
                    break;
                case (3):
                    notes.sort(headerSorter);
                    break;
                case (2):
                    notes.sort(headerSorter.reversed());
                    break;
            }

            int countResults = 0;
            for (int i = 0, notesSize = notes.size(); i < notesSize; i++) {
                Note note = notes.get(i);
                if (note.getValue().contains(query)) {
                    countResults++;
                    View viewTop = LayoutInflater.from(requireContext()).inflate(R.layout.view_item_note_top_textview, linearLayoutNotesList, false);
                    View viewBottom = LayoutInflater.from(requireContext()).inflate(R.layout.view_item_note_bottom_textview, linearLayoutNotesList, false);

                    viewTop.setOnClickListener(v -> {
                        if (onDateClicked != null) {
                            onDateClicked.onDateClickedSearchList(note.getID());
                        }
                    });

                    viewBottom.setOnClickListener(v -> {
                        if (noteClicked != null) {
                            noteClicked.onNoteClickedSearchList(note.getID());
                        }
                    });

                    TextView textViewTop = viewTop.findViewById(R.id.textViewTop);
                    long date = note.getDate() * MILISECOND;

                    DateFormat f = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.getDefault());
                    String dateStr = f.format(date);

                    //textViewTop.setPadding(0,50,0,0);

                    textViewTop.setText(dateStr);
                    textViewTop.setTag(note.getID());

                    Log.v("Debug1", "SearchResultFragment fillList textViewTop.getTag()=" + textViewTop.getTag());

                    TextView textViewBottom = viewBottom.findViewById(R.id.textViewBottom);

                    String[] textSize = getResources().getStringArray(R.array.text_size);
                    int textSizeId = ((GlobalVariables) getActivity().getApplication()).getTextSizeId();
                    float textSizeFloat = Float.parseFloat(textSize[textSizeId]);
                    textViewBottom.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeFloat);

                    textViewBottom.setText(note.getValue());

                    linearLayoutIntoScrollViewIntoFragmentSearchResult.addView(viewTop);
                    linearLayoutIntoScrollViewIntoFragmentSearchResult.addView(viewBottom);
                }
            }
            if (countResults == 0) {
                //textViewNoSearchResult
                View viewNoSearchResult = LayoutInflater.from(requireContext()).inflate(R.layout.view_nosearchresult_textview, linearLayoutNotesList, false);
                TextView textViewNoSearchResult = viewNoSearchResult.findViewById(R.id.textViewNoSearchResult);
                String strNoSearchResult = getResources().getString(R.string.textViewNoSearchResult) + query;
                textViewNoSearchResult.setText(strNoSearchResult);
                linearLayoutIntoScrollViewIntoFragmentSearchResult.addView(textViewNoSearchResult);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("Debug1", "SearchResultFragment onStart");
        fillList(viewFragment, mParam1);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("Debug1", "SearchResultFragment onStop");
    }

    public void onResume() {
        super.onResume();
        Log.v("Debug1", "SearchResultFragment onResume");
    }

    public void onPause() {
        super.onPause();
        Log.v("Debug1", "SearchResultFragment onPause");
    }

}