package ru.geekbrains.notes.ui.settings;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.Settings;
import ru.geekbrains.notes.SharedPref;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.note.NotesCloudRepositoryImpl;
import ru.geekbrains.notes.note.NotesLocalRepositoryImpl;
import ru.geekbrains.notes.note.NotesRepository;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;
import ru.geekbrains.notes.ui.MainActivity;
import ru.geekbrains.notes.ui.auth.AuthFragment;
import ru.geekbrains.notes.ui.auth.UserProfile;

import static ru.geekbrains.notes.Constant.AUTH_RESULT;
import static ru.geekbrains.notes.Constant.TYPE_AUTH_NONE;
import static ru.geekbrains.notes.Constant.TYPE_EVENT_CHANGE_SETTINGS;
import static ru.geekbrains.notes.Constant.TYPE_EVENT_CLOUD_SYNC;

public class SettingsFragment extends Fragment {

    private Spinner spinnerSort;
    private Spinner spinnerTextSize;
    private Spinner spinnerMaxCountLines;
    private Settings settings;
    SwitchCompat aSwitch;

    public static final String TAG = "SettingsFragment";

    private UserProfile userProfile = new UserProfile();

    private Publisher publisher;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Debug1", "SettingsFragment onCreate");
    }

    private void showAlertDialogClearNotes() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle("????????????????!")
                .setMessage("???? ?????????????????????????? ???????????? ???????????????? ???????????? ???????????????")
                .setIcon(R.drawable.ic_clear)
                .setCancelable(false)
                .setPositiveButton("????", (dialog, which) -> {
                    if (SettingsFragment.this.getActivity() != null) {
                        List<Note> notes = ((GlobalVariables) SettingsFragment.this.getActivity().getApplication()).getNotes();

                        int authTypeService = settings.getAuthTypeService();
                        String userName = AuthFragment.checkCloudStatusByUserName(settings, SettingsFragment.this.getContext(), SettingsFragment.this.getActivity());
                        if (userName != null && !userName.equals("")) {
                            NotesRepository cloudRepository = new NotesCloudRepositoryImpl(authTypeService, userName);
                            cloudRepository.clearNotes(notes, result -> {
                                Log.v("Debug1", "SettingsFragment cloudRepository.clearNotes");
                                if ((int) result == notes.size())
                                    if (getView() != null)
                                        Snackbar.make(getView(), "???????????? ?????????????? ????????????", Snackbar.LENGTH_SHORT).show();
                            });
                        }

                        //?????????????? ?? ?????????????????? ??????????????????????
                        NotesRepository localRepository = new NotesLocalRepositoryImpl(getContext(), getActivity());
                        localRepository.clearNotes(notes, result1 -> Log.v("Debug1", "SettingsFragment clearNotes localRepository.clearNotes"));

                    }
                })
                .setNegativeButton("??????", (dialog, which) -> {
                });

        builder.show();
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

        if (getContext() != null) {

            MainActivity.setTitle(getActivity(), "??????????????????");

            if (getActivity() != null) {
                settings = ((GlobalVariables) getActivity().getApplication()).getSettings();
            }

            Button clearAllNotes = view.findViewById(R.id.buttonClearAll);
            clearAllNotes.setOnClickListener(v -> showAlertDialogClearNotes());

            spinnerTextSize = view.findViewById(R.id.spinnerTextSize);

            ArrayAdapter<CharSequence> adapterTextSize = ArrayAdapter.createFromResource(getContext(), R.array.text_size, android.R.layout.simple_spinner_item);
            spinnerTextSize.setAdapter(adapterTextSize);
            spinnerTextSize.setSelection(settings.getTextSizeId());
            spinnerTextSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    // ???????????????????? ?????????????? ???????????????? ????????????????
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
                    // ???????????????????? ?????????????? ???????????????? ????????????????
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
                    // ???????????????????? ?????????????? ???????????????? ????????????????
                    /*if (getActivity() != null)
                        ((GlobalVariables) getActivity().getApplication()).setMaxCountLinesId(position);*/
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });


            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().setFragmentResultListener(AUTH_RESULT, this, (requestKey, result) -> {

                    userProfile = result.getParcelable("UserProfile");
                    Log.v("Debug1", "SettingsFragment onCreateView onFragmentResult userProfile.getDisplayName()=" + userProfile.getDisplayName());
                    Log.v("Debug1", "SettingsFragment onCreateView onFragmentResult userProfile.getEmail()=" + userProfile.getEmail());
                    Log.v("Debug1", "SettingsFragment onCreateView onFragmentResult userProfile.getFamilyName()=" + userProfile.getFamilyName());
                    Log.v("Debug1", "SettingsFragment onCreateView onFragmentResult userProfile.getGivenName()=" + userProfile.getGivenName());
                    Log.v("Debug1", "SettingsFragment onCreateView onFragmentResult userProfile.getiD()=" + userProfile.getId());
                    Log.v("Debug1", "SettingsFragment onCreateView onFragmentResult userProfile.getIdToken()=" + userProfile.getIdToken());
                    Log.v("Debug1", "SettingsFragment onCreateView onFragmentResult userProfile.getPhotoURL()=" + userProfile.getPhotoURL());
                    Log.v("Debug1", "SettingsFragment onCreateView onFragmentResult userProfile.getServerAuthCode()=" + userProfile.getServerAuthCode());
                    Log.v("Debug1", "SettingsFragment onCreateView onFragmentResult userProfile.getTypeAutService()=" + userProfile.getTypeAutService());

                    //settings = (new SharedPref(getActivity().getApplication()).loadSettings());


                    //???????????? ?????????????????? ???? ???????????????????? ????????????????????
                    //Settings settings = new Settings();
                    if (getActivity() != null) {
                        settings = ((GlobalVariables) getActivity().getApplication()).getSettings();
                    }


                    settings.setCloudSync(userProfile.getTypeAutService() != TYPE_AUTH_NONE);
                    settings.setAuthTypeService(userProfile.getTypeAutService());

                    //?????????????????? ?????????????????? ?? ???????????????????? ????????????????????
                    ((GlobalVariables) getActivity().getApplication()).setSettings(settings);

                    if (userProfile.getTypeAutService() == TYPE_AUTH_NONE)
                        aSwitch.setChecked(false);

                    if (publisher != null) {
                        Log.v("Debug1", "SettingsFragment onViewCreated setFragmentResultListener notify");
                        publisher.notify(-1, TYPE_EVENT_CLOUD_SYNC);
                    }

                    //Toast.makeText(requireContext(), "Auth Success", Toast.LENGTH_SHORT).show();
                });
            }

            Button autButton = view.findViewById(R.id.buttonAuth);
            autButton.setOnClickListener(v -> {
                AuthFragment authFragment = AuthFragment.newInstance(settings.getAuthTypeService());
                if (getActivity() != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    fragmentTransaction.add(R.id.frame_container_main, authFragment, "AuthFragment");
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });

            aSwitch = view.findViewById(R.id.switchAuth);


            aSwitch.setChecked(settings.isCloudSync());

            if (settings.getAuthTypeService() != 0) {
                aSwitch.setChecked(true);
                autButton.setVisibility(View.VISIBLE);
            } else {
                aSwitch.setChecked(false);
                autButton.setVisibility(View.INVISIBLE);
            }

            aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    autButton.setVisibility(View.VISIBLE);

                    AuthFragment authFragment = AuthFragment.newInstance(settings.getAuthTypeService());
                    if (getActivity() != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        fragmentTransaction.add(R.id.frame_container_main, authFragment, "AuthFragment");
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }

                } else
                    autButton.setVisibility(View.INVISIBLE);
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

        //settings = (new SharedPref(getActivity().getApplication()).loadSettings());

        //???????????? ?????????????????? ???? ???????????????????? ????????????????????
        //Settings settings = new Settings();
        if (getActivity() != null) {
            settings = ((GlobalVariables) getActivity().getApplication()).getSettings();
        }

        settings.setOrderType(spinnerSort.getSelectedItemPosition());
        settings.setTextSizeId(spinnerTextSize.getSelectedItemPosition());
        settings.setMaxCountLinesId(spinnerMaxCountLines.getSelectedItemPosition());

        settings.setCloudSync(aSwitch.isChecked());

        String[] textSizeArray = getResources().getStringArray(R.array.text_size);
        int textSizeId = settings.getTextSizeId();
        float textSizeFloat = Float.parseFloat(textSizeArray[textSizeId]);
        settings.setTextSize(textSizeFloat);

        String[] maxCountLinesArray = getResources().getStringArray(R.array.MaxCountLines);
        int maxCountLinesId = settings.getMaxCountLinesId();
        int maxCountLines;

        switch (maxCountLinesId) {
            case (0):              //?????? ??????????????????????
                maxCountLines = -1;
                break;
            case (1):               //?????????????????????????????????? ?? ????????????
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