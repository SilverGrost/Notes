package ru.geekbrains.notes.ui.item;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.List;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.Settings;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.note.NotesCloudRepositoryImpl;
import ru.geekbrains.notes.note.NotesLocalRepositoryImpl;
import ru.geekbrains.notes.note.NotesRepository;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;
import ru.geekbrains.notes.ui.MainActivity;
import ru.geekbrains.notes.ui.auth.AuthFragment;

import static ru.geekbrains.notes.Constant.TYPE_EVENT_ADD_NOTE;
import static ru.geekbrains.notes.Constant.TYPE_EVENT_EDIT_NOTE;


public class EditNoteFragment extends Fragment implements View.OnClickListener {

    private static final String ARG = "NOTE_ID";
    public static final String TAG = "EditNoteFragment";
    int noteId = 0;

    private EditText editTextNoteValue;

    private Publisher publisher;

    private View editFragment;

    private int newNoteId = -1;

    public View getEditFragment() {
        return editFragment;
    }

    public static EditNoteFragment newInstance(int noteId) {
        Log.v("Debug1", "EditNoteFragment newInstance noteId=" + noteId);
        EditNoteFragment fragment = new EditNoteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG, noteId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("Debug1", "EditNoteFragment onCreate");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Debug1", "EditNoteFragment onAttach");
        //getActivity().setTitle("???????????? ??????????????");
        MainActivity.setTitle(getActivity(), "???????????? ??????????????");

        if (context instanceof PublisherHolder) {
            publisher = ((PublisherHolder) context).getPublisher();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Debug1", "EditNoteFragment onDetach");
        publisher = null;
    }

    public EditNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v("Debug1", "EditNoteFragment onCreateView");
        setHasOptionsMenu(false);
        View v = inflater.inflate(R.layout.fragment_edit_note, container, false);
        FloatingActionButton button_ok = v.findViewById(R.id.button_ok);
        button_ok.setOnClickListener(this);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("Debug1", "EditNoteFragment onViewCreated");
        editFragment = view;
        fillEditNote(view);
    }

    public void fillEditNote(View view) {
        Log.v("Debug1", "EditNoteFragment fillEditNote");
        if (getArguments() != null && getActivity() != null) {
            noteId = getArguments().getInt(ARG, 0);
            Log.v("Debug1", "EditNoteFragment fillEditNote getArguments() != null noteId=" + noteId);
            Note note = ((GlobalVariables) getActivity().getApplication()).getNoteByNoteId(noteId);
            editTextNoteValue = view.findViewById(R.id.editTextNoteValue);

            Settings settings;
            if (getActivity() != null) {
                settings = ((GlobalVariables) getActivity().getApplication()).getSettings();
                editTextNoteValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.getTextSize());
                editTextNoteValue.setText(note.getValue());
            }
        }
    }

    @Override
    public void onClick(View v) {
        Log.v("Debug1", "EditNoteFragment onClick");

        if (v.getId() == R.id.button_ok) {

            Log.v("Debug1", "EditNoteFragment onClick button_ok noteId=" + noteId);
            String value = editTextNoteValue.getText().toString();
            Date date = new Date();
            if (getActivity() != null) {
                List<Note> notes = ((GlobalVariables) getActivity().getApplication()).getNotes();
                Note note = ((GlobalVariables) getActivity().getApplication()).getNoteByNoteId(noteId);
                note.setDateEdit(date.toInstant().getEpochSecond());
                note.setValue(value);

                boolean cloudSync = false;
                int authTypeService = 0;
                String userName = "";
                if (getActivity() != null) {
                    //???????????????? ?????????????????? ???? ???????????????????? ????????????????????
                    Settings settings = ((GlobalVariables) getActivity().getApplication()).getSettings();
                    authTypeService = settings.getAuthTypeService();
                    userName = AuthFragment.checkCloudStatusByUserName(settings, getContext(), getActivity());
                    if (userName != null && !userName.equals("")) {
                        cloudSync = true;
                    }
                }

                NotesRepository localRepository = new NotesLocalRepositoryImpl(getContext(), getActivity());
                NotesRepository cloudRepository = new NotesCloudRepositoryImpl(authTypeService, userName);


                if (note.getID() == -1) {
                    note.setDateCreate(date.toInstant().getEpochSecond());

                    newNoteId = ((GlobalVariables) getActivity().getApplication()).getNewId();

                    note.setID(newNoteId);

                    boolean finalCloudSync = cloudSync;
                    localRepository.addNote(notes, note, result -> {
                        Log.v("Debug1", "EditNoteFragment onClick button_ok localRepository addNote");

                        if (finalCloudSync) {
                            //?????????????????? ?? ???????????? ?? ???????????????? ???????????????? id
                            cloudRepository.addNote(notes, note, result13 -> {
                                note.setIdCloud((String) result13);
                                Log.v("Debug1", "EditNoteFragment onClick button_ok cloudRepository addNote result=" + result13);

                                //?????????????????? ?? ?????????????????? ?????????????????????? ???????????????????? ???????????????? id
                                localRepository.updateNote(notes, note, result1 -> Log.v("Debug1", "EditNoteFragment onClick button_ok localRepository updateNote"));

                                //?????????????????? ?? ???????????????? ?????????????????????? ???????????????????? ???????????????? id
                                cloudRepository.updateNote(notes, note, result12 -> Log.v("Debug1", "EditNoteFragment onClick button_ok cloudRepository updateNote"));
                            });
                        }
                    });

                } else {
                    localRepository.updateNote(notes, note, result -> Log.v("Debug1", "EditNoteFragment onClick button_ok notify TYPE_EVENT_EDIT_NOTE"));
                    if (cloudSync) {
                        cloudRepository.updateNote(notes, note, result -> Log.v("Debug1", "EditNoteFragment onClick button_ok notify cloudRepository update"));
                    }
                }

                if (publisher != null) {
                    Log.v("Debug1", "EditNoteFragment onClick button_ok notify noteId=" + noteId);
                    if (noteId == -1)
                        publisher.notify(newNoteId, TYPE_EVENT_ADD_NOTE);
                    else
                        publisher.notify(noteId, TYPE_EVENT_EDIT_NOTE);
                }
            }
        }

        Log.v("Debug1", "EditNoteFragment onClick FragmentTransaction");
        if (getActivity() != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        }
        Log.v("Debug1", "EditNoteFragment onClick end");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("Debug1", "EditNoteFragment onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v("Debug1", "EditNoteFragment onStop");
    }
}