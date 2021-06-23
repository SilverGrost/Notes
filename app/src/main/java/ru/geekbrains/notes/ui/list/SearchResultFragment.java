package ru.geekbrains.notes.ui.list;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.Comparator;
import java.util.List;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.Settings;
import ru.geekbrains.notes.note.DateCreateSorterComparator;
import ru.geekbrains.notes.note.DateEditSorterComparator;
import ru.geekbrains.notes.note.HeaderSorterComparator;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.observer.ObserverNote;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;
import ru.geekbrains.notes.ui.DatepickerFragment;
import ru.geekbrains.notes.ui.MainFragment;
import ru.geekbrains.notes.ui.item.ViewNoteFragment;

import static ru.geekbrains.notes.Constant.ODREB_BY_DATE_CREATE;
import static ru.geekbrains.notes.Constant.ODREB_BY_DATE_CREATE_DESC;
import static ru.geekbrains.notes.Constant.ODREB_BY_DATE_EDIT;
import static ru.geekbrains.notes.Constant.ODREB_BY_DATE_EDIT_DESC;
import static ru.geekbrains.notes.Constant.ODREB_BY_DATE_VALUE;
import static ru.geekbrains.notes.Constant.ODREB_BY_DATE_VALUE_DESC;


public class SearchResultFragment extends Fragment implements ObserverNote {

    private Publisher publisher;

    private RecyclerView recyclerView;
    private List<Note> notes;
    private View viewSearchResult;

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public List<Note> getNotes() {
        return notes;
    }

    @Override
    public void updateNote(int noteID, int typeEvent) {
        Log.v("Debug1", "SearchResultFragment updateNote noteID=" + noteID);
        initRecyclerViewSearchResult(recyclerView, mParamQuery);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Debug1", "SearchResultFragment onAttach");
        if (context instanceof PublisherHolder) {
            publisher = ((PublisherHolder) context).getPublisher();
            publisher.subscribe(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Debug1", "SearchResultFragment onDetach");
        if (publisher != null) {
            publisher.unsubscribe(this);
        }
    }

    private static final String ARG_PARAM1 = "param1";

    private String mParamQuery;

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
            mParamQuery = getArguments().getString(ARG_PARAM1);
            if (getActivity() != null)
                notes = ((GlobalVariables) getActivity().getApplication()).getNotesWithText(mParamQuery);
        }
    }

    public List<Note> sortNotes(List<Note> notes) {
        Log.v("Debug1", "ListNotesFragment sortNotes");
        if (getActivity() != null) {
            int textSortId = ((GlobalVariables) getActivity().getApplication()).getSettings().getSortType();
            Comparator<Note> dateSorter = new DateEditSorterComparator();
            Comparator<Note> dateCreateSorter = new DateCreateSorterComparator();
            Comparator<Note> headerSorter = new HeaderSorterComparator();
            switch (textSortId) {
                case (ODREB_BY_DATE_EDIT):
                    notes.sort(dateSorter);
                    break;
                case (ODREB_BY_DATE_EDIT_DESC):
                    notes.sort(dateSorter.reversed());
                    break;
                case (ODREB_BY_DATE_CREATE):
                    notes.sort(dateCreateSorter);
                    break;
                case (ODREB_BY_DATE_CREATE_DESC):
                    notes.sort(dateCreateSorter.reversed());
                    break;
                case (ODREB_BY_DATE_VALUE):
                    notes.sort(headerSorter);
                    break;
                case (ODREB_BY_DATE_VALUE_DESC):
                    notes.sort(headerSorter.reversed());
                    break;
            }
        }
        return notes;
    }

    public void setEmptyResultTextView(View view) {
        Log.v("Debug1", "SearchResultFragment setEmptyResultTextView");
        TextView textViewNoSearchResult = view.findViewById(R.id.textViewNoSearchResultRV);
        if (textViewNoSearchResult != null) {
            if (notes.size() != 0) {
                textViewNoSearchResult.setVisibility(View.GONE);
            } else {
                textViewNoSearchResult.setVisibility(View.VISIBLE);
                String strNoSearchResult = getResources().getString(R.string.textViewNoSearchResult) + mParamQuery;
                textViewNoSearchResult.setText(strNoSearchResult);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v("Debug1", "SearchResultFragment onCreateView");
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        viewSearchResult = view;
        recyclerView = view.findViewById(R.id.recycler_view_lines_search_result);
        //setEmptyResultTextView(viewSearchResult);
        if (recyclerView != null) {
            initRecyclerViewSearchResult(recyclerView, mParamQuery);
        }
        return view;
    }

    public void initRecyclerViewSearchResult(RecyclerView recyclerView, String query) {
        Log.v("Debug1", "SearchResultFragment initRecyclerView");

        if (getActivity() != null) {
            notes = ((GlobalVariables) getActivity().getApplication()).getNotesWithText(query);
            setEmptyResultTextView(viewSearchResult);
            // Эта установка служит для повышения производительности системы
            recyclerView.setHasFixedSize(true);
            // Будем работать со встроенным менеджером
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);

            notes = sortNotes(notes);
            Settings settings = new Settings();
            if (getActivity() != null) {
                settings = ((GlobalVariables) getActivity().getApplication()).getSettings();
            }

            // Установим адаптер
            final RVAdapter RVAdapter = new RVAdapter(notes, settings);
            recyclerView.setAdapter(RVAdapter);
            RVAdapter.notifyDataSetChanged();

            // Установим слушателя на текст
            RVAdapter.SetOnNoteClicked((view, position) -> {
                int noteId = (int) view.getTag();
                Log.v("Debug1", "SearchResultFragment initRecyclerView onNoteClickedList noteId=" + noteId);
                ViewNoteFragment viewNoteFragment = null;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    if (getActivity() != null)
                        viewNoteFragment = (ViewNoteFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.activity_container_note_view);
                } else {
                    MainFragment mainFragment = null;
                    if (getActivity() != null)
                        mainFragment = (MainFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.frame_container_main);
                    if (mainFragment != null) {
                        FragmentManager childFragmentManager = mainFragment.getChildFragmentManager();
                        viewNoteFragment = (ViewNoteFragment) childFragmentManager.findFragmentById(R.id.activity_container_note_view);
                    }
                }
                if (viewNoteFragment == null) {
                    Log.v("Debug1", "SearchResultFragment initRecyclerView viewNoteFragment == null");
                    viewNoteFragment = ViewNoteFragment.newInstance(noteId);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(R.id.frame_container_main, viewNoteFragment, "ViewNoteFragment");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    Log.v("Debug1", "SearchResultFragment initRecyclerView viewNoteFragment != null");
                    viewNoteFragment.fillViewNote(noteId, viewNoteFragment.getViewFragment());
                }
            });
            // Установим слушателя на дату
            RVAdapter.SetOnDateClicked((view, position) -> {
                int noteId = (int) view.getTag();
                Log.v("Debug1", "SearchResultFragment initRecyclerView onDateClickedList noteId=" + noteId);
                DatepickerFragment datepickerFragment = DatepickerFragment.newInstance(noteId);
                if (getActivity() != null) {
                    FragmentTransaction fragmentTransaction;
                    fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(R.id.frame_container_main, datepickerFragment, "DatepickerFragment");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }
    }


    // вызывается после создания макета фрагмента, здесь мы проинициализируем список
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("Debug1", "SearchResultFragment onViewCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("Debug1", "SearchResultFragment onStart");
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