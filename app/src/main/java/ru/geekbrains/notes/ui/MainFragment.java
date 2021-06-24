package ru.geekbrains.notes.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ru.geekbrains.notes.R;
import ru.geekbrains.notes.ui.item.EditNoteFragment;
import ru.geekbrains.notes.ui.list.SearchResultFragment;

public class MainFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        Log.v("Debug1", "MainFragment onCreateView");
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Debug1", "MainFragment onCreate");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        Log.v("Debug1", "MainFragment onCreateOptionsMenu");
        inflater.inflate(R.menu.main_fragment, menu);
        MenuItem search = menu.findItem(R.id.action_search); // поиск пункта меню поиска
        SearchView searchText = (SearchView) search.getActionView(); // строка поиска
        searchText.clearFocus();
        searchText.setOnQueryTextListener(this);
    }

    // вызывается после создания макета фрагмента, здесь мы проинициализируем список
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("Debug1", "MainFragment onViewCreated");
        FloatingActionButton buttonAddNote = view.findViewById(R.id.buttonAddNote);
        if (buttonAddNote != null)
            buttonAddNote.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.v("Debug1", "MainFragment onClick");
        if (v.getId() == R.id.buttonAddNote) {
            addButtonProcess();
        }
    }

    private void addButtonProcess(){
        EditNoteFragment editNoteFragment = EditNoteFragment.newInstance(-1);
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.replace(R.id.frame_container_main, editNoteFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("Debug1", "MainFragment onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("Debug1", "MainFragment onStop");
    }

    public void onResume() {
        super.onResume();
        Log.v("Debug1", "MainFragment onResume");
    }

    public void onPause() {
        super.onPause();
        Log.v("Debug1", "MainFragment onPause");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Debug1", "MainFragment onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Debug1", "MainFragment onDetach");
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.v("Debug1", "MainFragment onQueryTextSubmit query=" + query);
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            SearchResultFragment searchResultFragment;
            searchResultFragment = (SearchResultFragment) fragmentManager.findFragmentByTag("SearchResultFragment");
            if (searchResultFragment == null) {
                Log.v("Debug1", "MainFragment onQueryTextSubmit searchResultFragment == null");
                searchResultFragment = SearchResultFragment.newInstance(query);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.replace(R.id.frame_container_main, searchResultFragment, "SearchResultFragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            } else {
                Log.v("Debug1", "MainFragment onQueryTextSubmit searchResultFragment != null");
                searchResultFragment.initRecyclerViewSearchResult(searchResultFragment.getRecyclerView(), query);
            }
        }
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.v("Debug1", "MainFragment onQueryTextChange");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v("Debug1", "MainFragment onOptionsItemSelected");
        // Обработка выбора пункта меню приложения (активити)
        int id = item.getItemId();
        if (id == R.id.action_search) {//addFragment(new SettingsFragment());
            Toast.makeText(getActivity(), "action_search", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}