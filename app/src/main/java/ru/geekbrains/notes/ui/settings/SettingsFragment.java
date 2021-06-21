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
import android.widget.Spinner;


import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.Settings;
import ru.geekbrains.notes.SharedPref;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;

public class SettingsFragment extends Fragment {

    Spinner spinnerSort;
    Spinner spinnerTextSize;

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

        Settings settings = new Settings();
        if (getContext() != null)
            settings = (new SharedPref(getContext()).loadSettings());

        spinnerTextSize = view.findViewById(R.id.spinnerTextSize);
        ArrayAdapter<CharSequence> adapterTextSize = ArrayAdapter.createFromResource(getContext(), R.array.text_size, android.R.layout.simple_spinner_item);
        spinnerTextSize.setAdapter(adapterTextSize);

        spinnerTextSize.setSelection(settings.getTextSize());

        spinnerTextSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                //Toast.makeText(getContext(), "Position=" + position + ", text=" + spinnerTextSize.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                if (getActivity() != null)
                    ((GlobalVariables) getActivity().getApplication()).setTextSizeId(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //Toast.makeText(getContext(), "Position NothingSelected", Toast.LENGTH_SHORT).show();
            }
        });

        spinnerSort = view.findViewById(R.id.spinnerSort);
        ArrayAdapter<CharSequence> adapterSort = ArrayAdapter.createFromResource(getContext(), R.array.type_sort, android.R.layout.simple_spinner_item);
        spinnerSort.setAdapter(adapterSort);

        spinnerSort.setSelection(settings.getSortType());

        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                //Toast.makeText(getContext(), "Position=" + position + ", text=" + spinnerSort.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                if (getActivity() != null)
                    ((GlobalVariables) getActivity().getApplication()).setSortTypeId(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                //Toast.makeText(getContext(), "Position NothingSelected", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("Debug1", "AboutFragment onStop");
    }

    public void onResume() {
        super.onResume();
        Log.v("Debug1", "AboutFragment onResume");
    }

    public void onPause() {
        super.onPause();
        Log.v("Debug1", "AboutFragment onPause");
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.v("Debug1", "AboutFragment onDestroyView");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.v("Debug1", "AboutFragment onDestroy");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof PublisherHolder) {
            publisher = ((PublisherHolder) context).getPublisher();
        }
        Log.v("Debug1", "AboutFragment onAttach");
    }

    @Override
    public void onDetach() {
        Settings settings = new Settings(spinnerTextSize.getSelectedItemPosition(), spinnerSort.getSelectedItemPosition());
        if (getContext() != null)
            new SharedPref(getContext()).saveSettings(settings);

        if (publisher != null) {
            publisher.notify(0);
        }
        publisher = null;

        super.onDetach();
        Log.v("Debug1", "AboutFragment onDetach");
    }
}