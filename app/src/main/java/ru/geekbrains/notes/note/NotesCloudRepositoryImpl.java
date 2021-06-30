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

    private final String collectionId;
    private final static String FIELD_DATE_CREATE = "date_create";
    private final static String FIELD_DATE_EDIT = "date_edit";
    private final static String VALUE = "value";
    private final static String ID = "id";
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    public NotesCloudRepositoryImpl(int authTypeService, String userName) {
        collectionId = authTypeService + "_" + userName;
    }

    @Override
    public void getNotes(Callback<List<Note>> callback) {
        firebaseFirestore.collection(collectionId)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        ArrayList<Note> result = new ArrayList<>();

                        if (task.getResult() != null) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String value = (String) document.get(VALUE);
                                Date date_create = ((Timestamp) document.get(FIELD_DATE_CREATE)).toDate();
                                Date date_edit = ((Timestamp) document.get(FIELD_DATE_EDIT)).toDate();
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
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getIdCloud() != null) {
                firebaseFirestore.collection(collectionId)
                        .document(notes.get(i).getIdCloud())
                        .delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                callback.onSuccess(true);
                            }
                        });
            }
        }
    }

    @Override
    public void addNote(List<Note> notes, Note note, Callback<Object> callback) {
        HashMap<String, Object> data = new HashMap<>();

        data.put(ID, note.getID());
        Date dateCreate = new Date(note.getDateCreate() * MILISECOND);
        Date dateEdit = new Date(note.getDateEdit() * MILISECOND);
        data.put(FIELD_DATE_CREATE, dateCreate);
        data.put(FIELD_DATE_EDIT, dateEdit);
        data.put(VALUE, note.getValue());

        firebaseFirestore.collection(collectionId)
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
        firebaseFirestore.collection(collectionId)
                .document(note.getIdCloud())
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
        data.put(FIELD_DATE_CREATE, dateCreate);
        data.put(FIELD_DATE_EDIT, dateEdit);
        data.put(VALUE, note.getValue());

        firebaseFirestore.collection(collectionId)
                .document(note.getIdCloud())
                .update(data)
                .addOnCompleteListener(task -> callback.onSuccess(note));
    }
}
