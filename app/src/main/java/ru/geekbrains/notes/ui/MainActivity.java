package ru.geekbrains.notes.ui;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import ru.geekbrains.notes.BuildConfig;
import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.note.NoteRepository;
import ru.geekbrains.notes.note.NoteRepositoryImpl;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;
import ru.geekbrains.notes.ui.item.EditNoteFragment;
import ru.geekbrains.notes.ui.item.ViewNoteFragment;
import ru.geekbrains.notes.ui.list.ListNotesFragment;
import ru.geekbrains.notes.ui.settings.AboutFragment;
import ru.geekbrains.notes.ui.settings.SettingsFragment;


public class MainActivity extends AppCompatActivity implements ListNotesFragment.OnNoteClicked, ListNotesFragment.onDateClicked, PublisherHolder {

    private final Publisher publisher = new Publisher();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.v("Debug1", "MainActivity onCreate");

        initView();


        //Поднимем layout вместе с клавиатурой
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //Если первый раз
        if (savedInstanceState == null) {
            Log.v("Debug1", "MainActivity onCreate savedInstanceState == null");
            //Получаем доступ к репозиторию
            NoteRepository noteRepository = new NoteRepositoryImpl();

            //Получаем заметки из репозитория
            List<Note> notes = noteRepository.getNotes(this);

            //Сохраняем заметки в глобальной переменной
            ((GlobalVariables) this.getApplication()).setNotes(notes);

        } else {
            Log.v("Debug1", "MainActivity onCreate savedInstanceState != null");
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Log.v("Debug1", "MainActivity onCreate savedInstanceState != null ORIENTATION_LANDSCAPE");
            } else {
                Log.v("Debug1", "MainActivity onCreate savedInstanceState != null NOT_ORIENTATION_LANDSCAPE");
            }
        }
    }

    private void initView() {
        Log.v("Debug1", "MainActivity initView");
        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);
    }

    // регистрация drawer
    private void initDrawer(Toolbar toolbar) {
        Log.v("Debug1", "MainActivity initDrawer");
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Обработка навигационного меню
        NavigationView navigationView = findViewById(R.id.nav_view);
        //textView_version_menu

        // get header
        View navHeader = navigationView.getHeaderView(0);

        TextView textView = navHeader.findViewById(R.id.textView_version_menu);
        if (textView != null) {
            int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;
            String strAbout = getResources().getString(R.string.menu_string) + versionName;
            textView.setText(strAbout);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (navigateFragment(id)) {
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
            return false;
        });

    }

    /*private Fragment getVisibleFragment(FragmentManager fragmentManager){
        Log.v("Debug1", "MainActivity getVisibleFragment");
        List<Fragment> fragments = fragmentManager.getFragments();
        int countFragments = fragments.size();
        Log.v("Debug1", "MainActivity getVisibleFragment countFragments=" + countFragments);
        for(int i = countFragments - 1; i >= 0; i--){
            Fragment fragment = fragments.get(i);
            //if(fragment.isVisible()) {
                int fragmentId = fragment.getId();
                String fragmentTag = fragment.getTag();
                Log.v("Debug1", "MainActivity getVisibleFragment fragmentId=" + fragmentId + ", fragmentTag=" + fragmentTag);
                //return fragment;
            //}
        }
        return null;
    }*/

    private void addFragment(int fragmentID) {
        Log.v("Debug1", "MainActivity addFragment fragmentID=" + fragmentID);
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = null;
        if (fragmentID == R.id.frameLayoutAboutFragment) {
            fragmentTag = "AboutFragment";
        } else if (fragmentID == R.id.frameLayoutSettingsFragment) {
            fragmentTag = "SettingsFragment";
        }
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragment == null) {
            Log.v("Debug1", "MainActivity addFragment fragment == null");
            fragment = new SettingsFragment();
            if (fragmentID == R.id.frameLayoutAboutFragment) {
                fragment = new AboutFragment();
            } else if (fragmentID == R.id.frameLayoutSettingsFragment) {
                fragment = new SettingsFragment();
            }
            Log.v("Debug1", "MainActivity addFragment fragmentTag=" + fragmentTag);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frame_container_main, fragment, fragmentTag);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Log.v("Debug1", "MainActivity addFragment fragment != null");
        }
    }

    private boolean navigateFragment(int id) {
        Log.v("Debug1", "MainActivity navigateFragment id=" + id);
        if (id == R.id.action_about) {
            addFragment(R.id.frameLayoutAboutFragment);
            return true;
        } else if (id == R.id.action_settings) {
            addFragment(R.id.frameLayoutSettingsFragment);
            return true;
        }
        return false;
    }

    private Toolbar initToolbar() {
        Log.v("Debug1", "MainActivity initToolbar");
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Обработка выбора пункта меню приложения (активити)
        int id = item.getItemId();

        if (id == R.id.action_search) {//addFragment(new SettingsFragment());
            return true;
        } else if (id == R.id.action_add) {
            EditNoteFragment editNoteFragment = EditNoteFragment.newInstance(-1);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frame_container_main, editNoteFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Здесь определяем меню приложения (активити)
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem search = menu.findItem(R.id.action_search); // поиск пункта меню поиска
        SearchView searchText = (SearchView) search.getActionView(); // строка поиска
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // реагирует на конец ввода поиска
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                return true;
            }

            // реагирует на нажатие каждой клавиши
            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(MainActivity.this, newText, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        return true;
    }


    @Override
    public void onNoteClickedList(int noteId) {
        Log.v("Debug1", "MainActivity onNoteClickedList noteId=" + noteId);

        ViewNoteFragment viewNoteFragment = null;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewNoteFragment = (ViewNoteFragment) getSupportFragmentManager().findFragmentById(R.id.activity_container_note_view);
        } else {
            MainFragment mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.frame_container_main);
            if (mainFragment != null) {
                FragmentManager childFragmentManager = mainFragment.getChildFragmentManager();
                viewNoteFragment = (ViewNoteFragment) childFragmentManager.findFragmentById(R.id.activity_container_note_view);
            }
            //viewNoteFragment = (ViewNoteFragment) mainFragment.getChildFragmentManager().findFragmentById(R.id.activity_container_note_view);
        }

        if (viewNoteFragment == null) {
            Log.v("Debug1", "MainActivity onNoteClickedList viewNoteFragment == null");
            viewNoteFragment = ViewNoteFragment.newInstance(noteId);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.frame_container_main, viewNoteFragment, "ViewNoteFragment");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        } else {
            Log.v("Debug1", "MainActivity onNoteClickedList viewNoteFragment != null");
            viewNoteFragment.fillViewNote(noteId, viewNoteFragment.getViewFragment());
        }
    }

    @Override
    public void onDateClickedList(int noteId) {
        Log.v("Debug1", "MainActivity onDateClickedList");
        DatepickerFragment datepickerFragment = DatepickerFragment.newInstance(noteId);
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_container_main, datepickerFragment, "DatepickerFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public Publisher getPublisher() {
        Log.v("Debug1", "MainActivity getPublisher");
        return publisher;
    }


}