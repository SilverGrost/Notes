package ru.geekbrains.notes.ui.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.geekbrains.notes.R;


public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Debug1", "AboutFragment onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);
        Log.v("Debug1", "AboutFragment onCreateView");
        //getActivity().en
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView = view.findViewById(R.id.textView3);
        textView.setText(HtmlCompat.fromHtml(getString(R.string.textAbout), HtmlCompat.FROM_HTML_MODE_LEGACY));
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("Debug1", "AboutFragment onStart");
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
        Log.v("Debug1", "AboutFragment onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Debug1", "AboutFragment onDetach");
    }
}