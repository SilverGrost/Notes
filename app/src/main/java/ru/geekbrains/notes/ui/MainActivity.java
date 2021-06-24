package ru.geekbrains.notes.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.List;

import ru.geekbrains.notes.BuildConfig;
import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.Settings;
import ru.geekbrains.notes.SharedPref;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.note.NoteRepository;
import ru.geekbrains.notes.note.NoteRepositoryImpl;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;

import ru.geekbrains.notes.ui.item.ViewNoteFragment;
import ru.geekbrains.notes.ui.settings.AboutFragment;
import ru.geekbrains.notes.ui.settings.SettingsFragment;


public class MainActivity extends AppCompatActivity implements PublisherHolder {

    private final Publisher publisher = new Publisher();

    private ActionBarDrawerToggle toggle;

    // Сохранение данных
    @Override
    public void onSaveInstanceState(@NonNull Bundle instanceState) {
        super.onSaveInstanceState(instanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.v("Debug1", "MainActivity onSaveInstanceState ORIENTATION_PORTRAIT");
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment viewNoteFragment = fragmentManager.findFragmentByTag("ViewNoteFragmentPortrait");
            ((GlobalVariables) getApplication()).setViewNoteFragmentState(viewNoteFragment != null);
        }
    }

    // Восстановление данных
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle instanceState) {
        super.onRestoreInstanceState(instanceState);
        Log.v("Debug1", "MainActivity onRestoreInstanceState");
    }

    private void getAllFragment(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        int countFragments = fragments.size();
        Log.v("Debug1", "MainActivity getAllFragment countFragments=" + countFragments);
        for (int i = countFragments - 1; i >= 0; i--) {
            Fragment fragment = fragments.get(i);
            Log.v("Debug1", "MainActivity getAllFragment fragment.getTag()=" + fragment.getTag() + ", fragment.getId()=" + fragment.getId());
        }
    }

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
            ((GlobalVariables) getApplication()).setNotes(notes);

            Settings settings = (new SharedPref(this).loadSettings());
            ((GlobalVariables) getApplication()).setSettings(settings);

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


        } else {
            Log.v("Debug1", "MainActivity onCreate savedInstanceState != null");

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                Log.v("Debug1", "MainActivity onCreate savedInstanceState != null ORIENTATION_LANDSCAPE");
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment viewNoteFragment = fragmentManager.findFragmentByTag("ViewNoteFragmentPortrait");
                if (viewNoteFragment != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentManager.popBackStack();
                    fragmentTransaction.commit();
                }
                getAllFragment(fragmentManager);
            } else {
                Log.v("Debug1", "MainActivity onCreate savedInstanceState != null ORIENTATION_PORTRAIT");
                FragmentManager fragmentManager = getSupportFragmentManager();
                getAllFragment(fragmentManager);
                if (((GlobalVariables) getApplication()).isViewNoteFragmentState()) {
                    Fragment viewNoteFragment = fragmentManager.findFragmentByTag("ViewNoteFragmentPortrait");
                    if (viewNoteFragment != null) {
                        Log.v("Debug1", "MainActivity onCreate savedInstanceState != null ORIENTATION_PORTRAIT viewNoteFragment != null");
                    } else {
                        Log.v("Debug1", "MainActivity onCreate savedInstanceState != null ORIENTATION_PORTRAIT viewNoteFragment == null");
                        viewNoteFragment = ViewNoteFragment.newInstance(((GlobalVariables) getApplication()).getCurrentNote());
                    }
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(R.id.frame_container_main, viewNoteFragment, "ViewNoteFragmentPortrait");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar,0,0) {

            @Override
            public void onDrawerClosed(View view) {
                syncActionBarArrowState();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                toggle.setDrawerIndicatorEnabled(true);
            }
        };

        toggle.setToolbarNavigationClickListener(v -> onBackPressed());

        getSupportFragmentManager().addOnBackStackChangedListener(mOnBackStackChangedListener);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Обработка навигационного меню
        NavigationView navigationView = findViewById(R.id.nav_view);

        // get header
        View navHeader = navigationView.getHeaderView(0);

        TextView textView = navHeader.findViewById(R.id.textView_version_menu);
        if (textView != null) {
            //int versionCode = BuildConfig.VERSION_CODE;
            String versionName = BuildConfig.VERSION_NAME;
            String strAbout = getResources().getString(R.string.menu_string) + versionName;
            textView.setText(strAbout);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (navigateFragment(id)) {
                drawer.closeDrawer(GravityCompat.START);

                if (toggle.isDrawerIndicatorEnabled() &&
                        toggle.onOptionsItemSelected(item)) {
                    return true;
                } else if (item.getItemId() == android.R.id.home &&
                        getSupportFragmentManager().popBackStackImmediate()) {
                    return true;
                } else {
                    return super.onOptionsItemSelected(item);
                }
            }
            return false;
        });

    }

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
            }
            Log.v("Debug1", "MainActivity addFragment fragmentTag=" + fragmentTag);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
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
    public Publisher getPublisher() {
        Log.v("Debug1", "MainActivity getPublisher");
        return publisher;
    }

    private final FragmentManager.OnBackStackChangedListener
            mOnBackStackChangedListener = this::syncActionBarArrowState;

    @Override
    protected void onDestroy() {
        getSupportFragmentManager().removeOnBackStackChangedListener(mOnBackStackChangedListener);
        super.onDestroy();
    }

    private void syncActionBarArrowState() {
        int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        toggle.setDrawerIndicatorEnabled(backStackEntryCount == 0);
    }

}