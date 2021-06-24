package ru.geekbrains.notes.ui.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.List;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.Settings;
import ru.geekbrains.notes.SharedPref;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;

import static ru.geekbrains.notes.Constant.TYPE_EVENT_CHANGE_SETTINGS;

public class SettingsFragment extends Fragment {

    Spinner spinnerSort;
    Spinner spinnerTextSize;
    Spinner spinnerMaxCountLines;

    Button clearAllNotes;

    private Publisher publisher;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Debug1", "SettingsFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.v("Debug1", "SettingsFragment onCreateView");
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("Debug1", "SettingsFragment onViewCreated");
        Settings settings;
        if (getContext() != null) {

            settings = (new SharedPref(getContext()).loadSettings());

            clearAllNotes = view.findViewById(R.id.buttonClearAll);
            clearAllNotes.setOnClickListener(v -> {
                if (getActivity() != null) {
                    List<Note> notes = ((GlobalVariables) getActivity().getApplication()).getNotes();
                    notes.clear();
                    ((GlobalVariables) getActivity().getApplication()).setNotes(notes);
                    new SharedPref(getActivity()).saveNotes(notes);
                }
            });

            spinnerTextSize = view.findViewById(R.id.spinnerTextSize);

            ArrayAdapter<CharSequence> adapterTextSize = ArrayAdapter.createFromResource(getContext(), R.array.text_size, android.R.layout.simple_spinner_item);
            spinnerTextSize.setAdapter(adapterTextSize);
            spinnerTextSize.setSelection(settings.getTextSizeId());
            spinnerTextSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    // показываем позиция нажатого элемента
                    /*if (getActivity() != null)
                        ((GlobalVariables) getActivity().getApplication()).setTextSizeId(position);*/
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    //Toast.makeText(getContext(), "Position NothingSelected", Toast.LENGTH_SHORT).show();
                }
            });

            spinnerSort = view.findViewById(R.id.spinnerSort);
            ArrayAdapter<CharSequence> adapterSort = ArrayAdapter.createFromResource(getContext(), R.array.type_sort, android.R.layout.simple_spinner_item);
            spinnerSort.setAdapter(adapterSort);
            spinnerSort.setSelection(settings.getOrderType());
            spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    // показываем позиция нажатого элемента
                    /*if (getActivity() != null)
                        ((GlobalVariables) getActivity().getApplication()).setSortTypeId(position);*/
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });

            spinnerMaxCountLines = view.findViewById(R.id.spinnerMaxCountLines);
            ArrayAdapter<CharSequence> adapterMaxCountLines = ArrayAdapter.createFromResource(getContext(), R.array.MaxCountLines, android.R.layout.simple_spinner_item);
            spinnerMaxCountLines.setAdapter(adapterMaxCountLines);
            spinnerMaxCountLines.setSelection(settings.getMaxCountLinesId());
            spinnerMaxCountLines.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    // показываем позиция нажатого элемента
                    /*if (getActivity() != null)
                        ((GlobalVariables) getActivity().getApplication()).setMaxCountLinesId(position);*/
                }
                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("Debug1", "SettingsFragment onStop");
    }

    public void onResume() {
        super.onResume();
        Log.v("Debug1", "SettingsFragment onResume");
    }

    public void onPause() {
        super.onPause();
        Log.v("Debug1", "SettingsFragment onPause");
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.v("Debug1", "SettingsFragment onDestroyView");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.v("Debug1", "SettingsFragment onDestroy");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PublisherHolder) {
            publisher = ((PublisherHolder) context).getPublisher();
        }
        Log.v("Debug1", "SettingsFragment onAttach");
    }

    @Override
    public void onDetach() {
        Settings settings = new Settings(spinnerSort.getSelectedItemPosition(), spinnerTextSize.getSelectedItemPosition(), spinnerMaxCountLines.getSelectedItemPosition());

        String[] textSizeArray = getResources().getStringArray(R.array.text_size);
        int textSizeId = settings.getTextSizeId();
        float textSizeFloat = Float.parseFloat(textSizeArray[textSizeId]);
        settings.setTextSize(textSizeFloat);

        String[] maxCountLinesArray = getResources().getStringArray(R.array.MaxCountLines);
        int maxCountLinesId = settings.getMaxCountLinesId();
        int maxCountLines;

        switch (maxCountLinesId){
            case (0):              //Без ограничений
                maxCountLines = -1;
                break;
            case (1):               //Авторазвёртывание в списке
                maxCountLines = 0;
                break;
            default:
                maxCountLines = Integer.parseInt(maxCountLinesArray[maxCountLinesId]);
                break;
        }

        settings.setMaxCountLines(maxCountLines);

        if (getContext() != null) {
            if (getActivity() != null)
                ((GlobalVariables) getActivity().getApplication()).setSettings(settings);
            new SharedPref(getContext()).saveSettings(settings);
        }

        if (publisher != null) {
            Log.v("Debug1", "SettingsFragment onClick onDetach notify");
            publisher.notify(-1, TYPE_EVENT_CHANGE_SETTINGS);
        }
        publisher = null;

        super.onDetach();
        Log.v("Debug1", "SettingsFragment onDetach");
    }
}