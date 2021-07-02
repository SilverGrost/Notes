package ru.geekbrains.notes.ui.item;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;


import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;

import java.util.Date;
import java.util.List;

import ru.geekbrains.notes.GlobalVariables;
import ru.geekbrains.notes.R;
import ru.geekbrains.notes.Settings;
import ru.geekbrains.notes.note.Note;
import ru.geekbrains.notes.note.NotesCloudRepositoryImpl;
import ru.geekbrains.notes.note.NotesLocalRepositoryImpl;
import ru.geekbrains.notes.note.NotesRepository;
import ru.geekbrains.notes.observer.Publisher;
import ru.geekbrains.notes.observer.PublisherHolder;
import ru.geekbrains.notes.ui.MainActivity;
import ru.geekbrains.notes.ui.auth.AuthFragment;

import static ru.geekbrains.notes.Constant.TYPE_EVENT_ADD_NOTE;
import static ru.geekbrains.notes.Constant.TYPE_EVENT_EDIT_NOTE;


public class EditNoteFragmentDialog extends DialogFragment {

    private static final String ARG = "NOTE_ID";
    public static final String TAG = "EditNoteFragmentDialog";
    int noteId = 0;

    private Publisher publisher;

    private int newNoteId = -1;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.v("Debug1", "EditNoteFragmentDialog onAttach");
        //getActivity().setTitle("Правка заметки");
        MainActivity.setTitle(getActivity(), "Правка заметки");

        if (context instanceof PublisherHolder) {
            publisher = ((PublisherHolder) context).getPublisher();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("Debug1", "EditNoteFragmentDialog onDetach");
        publisher = null;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Вытаскиваем макет диалога
        // https://stackoverflow.com/questions/15151783/stackoverflowerror-when-trying-to-inflate-a-custom-layout-for-an-alertdialog-ins
        final View contentView = requireActivity().getLayoutInflater().inflate(R.layout.fragment_edit_note_dialog, null);

        EditText editTextNoteValue = contentView.findViewById(R.id.editTextNoteValue);

        Settings settings;
        Note note;
        if (getArguments() != null)
            noteId = getArguments().getInt(ARG, 0);

        if (getActivity() != null) {
            note = ((GlobalVariables) getActivity().getApplication()).getNoteByNoteId(noteId);
            settings = ((GlobalVariables) getActivity().getApplication()).getSettings();
            editTextNoteValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, settings.getTextSize());
            editTextNoteValue.setText(note.getValue());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity())
                .setTitle("Title")
                .setView(contentView)
                .setPositiveButton("Ok", (dialogInterface, i) -> {

                    Log.v("Debug1", "EditNoteFragmentDialog onClick button_ok noteId=" + noteId);
                    String value = editTextNoteValue.getText().toString();
                    Date date = new Date();
                    if (getActivity() != null) {
                        List<Note> notes = ((GlobalVariables) getActivity().getApplication()).getNotes();
                        Note note1 = ((GlobalVariables) getActivity().getApplication()).getNoteByNoteId(noteId);
                        note1.setDateEdit(date.toInstant().getEpochSecond());
                        note1.setValue(value);

                        boolean cloudSync = false;
                        int authTypeService = 0;
                        String userName = "";
                        if (getActivity() != null) {
                            //Получаем настройки из глобальной переменной
                            Settings settings1 = ((GlobalVariables) getActivity().getApplication()).getSettings();
                            authTypeService = settings1.getAuthTypeService();
                            userName = AuthFragment.checkCloudStatusByUserName(settings1, getContext(), getActivity());
                            if (userName != null && !userName.equals("")) {
                                cloudSync = true;
                            }
                        }

                        NotesRepository localRepository = new NotesLocalRepositoryImpl(getContext(), getActivity());
                        NotesRepository cloudRepository = new NotesCloudRepositoryImpl(authTypeService, userName);


                        if (note1.getID() == -1) {
                            note1.setDateCreate(date.toInstant().getEpochSecond());

                            newNoteId = ((GlobalVariables) getActivity().getApplication()).getNewId();

                            note1.setID(newNoteId);

                            boolean finalCloudSync = cloudSync;
                            localRepository.addNote(notes, note1, result -> {
                                Log.v("Debug1", "EditNoteFragmentDialog onClick button_ok localRepository addNote");

                                if (finalCloudSync) {
                                    //Добавялем в облако и получаем облачный id
                                    cloudRepository.addNote(notes, note1, result13 -> {
                                        note1.setIdCloud((String) result13);
                                        Log.v("Debug1", "EditNoteFragmentDialog onClick button_ok cloudRepository addNote result=" + result13);

                                        //Обнавляем в локальном репозитории полученный облачный id
                                        localRepository.updateNote(notes, note1, result1 -> Log.v("Debug1", "EditNoteFragmentDialog onClick button_ok localRepository updateNote"));

                                        //Обнавляем в облачном репозитории полученный облачный id
                                        cloudRepository.updateNote(notes, note1, result12 -> Log.v("Debug1", "EditNoteFragmentDialog onClick button_ok cloudRepository updateNote"));
                                    });
                                }
                            });

                        } else {
                            localRepository.updateNote(notes, note1, result -> Log.v("Debug1", "EditNoteFragmentDialog onClick button_ok notify TYPE_EVENT_EDIT_NOTE"));
                            if (cloudSync) {
                                cloudRepository.updateNote(notes, note1, result -> Log.v("Debug1", "EditNoteFragmentDialog onClick button_ok notify cloudRepository update"));
                            }
                        }

                        if (publisher != null) {
                            Log.v("Debug1", "EditNoteFragmentDialog onClick button_ok notify noteId=" + noteId);
                            if (noteId == -1)
                                publisher.notify(newNoteId, TYPE_EVENT_ADD_NOTE);
                            else
                                publisher.notify(noteId, TYPE_EVENT_EDIT_NOTE);
                        }
                    }


                    dismiss();
                    //((MainActivity) requireActivity()).onDialogResult(answer);
                })
                .setNegativeButton("Нет", (dialog, which) -> dismiss());
        return builder.create();
    }

    public static EditNoteFragmentDialog newInstance(int noteId) {
        Log.v("Debug1", "EditNoteFragmentDialog newInstance noteId=" + noteId);
        EditNoteFragmentDialog fragment = new EditNoteFragmentDialog();
        Bundle args = new Bundle();
        args.putInt(ARG, noteId);
        fragment.setArguments(args);
        return fragment;
    }


}