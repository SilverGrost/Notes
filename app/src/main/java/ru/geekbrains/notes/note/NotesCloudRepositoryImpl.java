package ru.geekbrains.notes.note;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static ru.geekbrains.notes.Constant.MILISECOND;

public class NotesCloudRepositoryImpl implements NotesRepository {

    public static final NotesRepository INSTANCE = new NotesCloudRepositoryImpl();
    private final static String NOTES = "notes";
    private final static String DATE_CREATE = "date_create";
    private final static String DATE_EDIT = "date_edit";
    private final static String VALUE = "value";
    private final static String ID = "id";
    private static final String AUTH_SERVICE = "auth_service";
    private static final String USER_NAME = "user_name";
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    public void getNotes(Callback<List<Note>> callback) {
        firebaseFirestore.collection(NOTES)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        ArrayList<Note> result = new ArrayList<>();

                        if (task.getResult() != null) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String value = (String) document.get(VALUE);
                                Date date_create = ((Timestamp) document.get(DATE_CREATE)).toDate();
                                Date date_edit = ((Timestamp) document.get(DATE_EDIT)).toDate();
                                long id = (long) document.get(ID);
                                Note note = new Note();
                                note.setDateCreate(date_create.toInstant().getEpochSecond());
                                note.setDateEdit(date_edit.toInstant().getEpochSecond());
                                note.setID((int) id);
                                note.setValue(value);
                                result.add(note);
                            }
                        }

                        callback.onSuccess(result);

                    } else {
                        task.getException();
                    }
                });
    }

    @Override
    public void setNotes(List<Note> notes, Callback<Object> callback) {
        for (int i = 0; i < notes.size(); i++) {
            addNote(notes, notes.get(i), callback);
        }
    }

    @Override
    public void clearNotes(List<Note> notes, Callback<Object> callback) {

    }

    @Override
    public void addNote(List<Note> notes, Note note, Callback<Object> callback) {
        HashMap<String, Object> data = new HashMap<>();

        //Date date = new Date();

        /*data.put(USER_NAME, );
        data.put(AUTH_SERVICE, );*/
        data.put(ID, note.getID());


        Date dateCreate = new Date(note.getDateCreate() * MILISECOND);
        Date dateEdit = new Date(note.getDateEdit() * MILISECOND);

        data.put(DATE_CREATE, dateCreate);
        data.put(DATE_EDIT, dateEdit);
        data.put(VALUE, note.getValue());


        firebaseFirestore.collection(NOTES)
                .add(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String idCloud = task.getResult().getId();
                        callback.onSuccess(idCloud);
                    }
                });
    }

    @Override
    public void removeNote(List<Note> notes, Note note, Callback<Object> callback) {
        firebaseFirestore.collection(NOTES)
                .document(String.valueOf(note.getID()))
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(note);
                    }
                });
    }

    @Override
    public void updateNote(List<Note> notes, Note note, Callback<Object> callback) {
        HashMap<String, Object> data = new HashMap<>();

        data.put(ID, note.getID());

        Date dateCreate = new Date(note.getDateCreate() * MILISECOND);
        Date dateEdit = new Date(note.getDateEdit() * MILISECOND);

        data.put(DATE_CREATE, dateCreate);
        data.put(DATE_EDIT, dateEdit);
        data.put(VALUE, note.getValue());

        /*firebaseFirestore.collection(NOTES)
                .document(note.getID())
                .update(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        callback.onSuccess(note);
                    }
                });*/
    }
}
