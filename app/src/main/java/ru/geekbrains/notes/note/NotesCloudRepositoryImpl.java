package ru.geekbrains.notes.note;

import android.util.Log;

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
    private final static String IDCLOUD = "idCloud";
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();


    public NotesCloudRepositoryImpl(int authTypeService, String userName) {
        collectionId = authTypeService + "_" + userName;
    }

    @Override
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void getNotes(Callback<List<Note>> callback) {
        firebaseFirestore.collection(collectionId)
                .get()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        ArrayList<Note> result = new ArrayList<>();

                        if (task.getResult() != null) {

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                String value = (String) document.get(VALUE);

                                Date date_create = null;
                                Object o;
                                o = document.get(FIELD_DATE_CREATE);
                                if (o != null)
                                    date_create = ((Timestamp) o).toDate();

                                Date date_edit = null;
                                o = document.get(FIELD_DATE_EDIT);
                                if (o != null)
                                    date_edit = ((Timestamp) o).toDate();

                                long id = 0;
                                o = document.get(ID);
                                if (o != null)
                                    id = (long) o;

                                String idCloud = (String) document.get(IDCLOUD);

                                Note note = new Note();

                                if (date_create != null)
                                    note.setDateCreate(date_create.toInstant().getEpochSecond());

                                if (date_edit != null)
                                    note.setDateEdit(date_edit.toInstant().getEpochSecond());

                                note.setID((int) id);
                                note.setValue(value);
                                note.setIdCloud(idCloud);
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
            if (notes.get(i).getIdCloud() != null && !notes.get(i).getIdCloud().equals("")) {
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
        data.put(IDCLOUD, note.getIdCloud());

        Log.v("Debug1", "NotesCloudRepositoryImpl addNote note.getID()=" + note.getID() + ", note.getValue()=" + note.getValue() + ", note.getIdCloud()=" + note.getIdCloud());

        firebaseFirestore.collection(collectionId)
                .add(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            String idCloud = task.getResult().getId();
                            callback.onSuccess(idCloud);
                        }
                    }
                });
    }

    @Override
    public void removeNote(List<Note> notes, Note note, Callback<Object> callback) {
        Log.v("Debug1", "NotesCloudRepositoryImpl removeNote note.getID()=" + note.getID() + ", note.getValue()=" + note.getValue() + ", note.getIdCloud()" + note.getIdCloud());
        if (note.getIdCloud() != null && !note.getIdCloud().equals("")) {
            firebaseFirestore.collection(collectionId)
                    .document(note.getIdCloud())
                    .delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            callback.onSuccess(note);
                        }
                    });
        }
    }

    @Override
    public void updateNote(List<Note> notes, Note note, Callback<Object> callback) {

        if (note.getIdCloud() != null && !note.getIdCloud().equals("")) {

            HashMap<String, Object> data = new HashMap<>();
            data.put(ID, note.getID());
            Date dateCreate = new Date(note.getDateCreate() * MILISECOND);
            Date dateEdit = new Date(note.getDateEdit() * MILISECOND);
            data.put(FIELD_DATE_CREATE, dateCreate);
            data.put(FIELD_DATE_EDIT, dateEdit);
            data.put(VALUE, note.getValue());
            data.put(IDCLOUD, note.getIdCloud());

            Log.v("Debug1", "NotesCloudRepositoryImpl updateNote note.getID()=" + note.getID() + ", note.getValue()=" + note.getValue() + ", note.getIdCloud()" + note.getIdCloud());

            if (note.getIdCloud() != null && !note.getIdCloud().equals(""))
            firebaseFirestore.collection(collectionId)
                    .document(note.getIdCloud())
                    .update(data)
                    .addOnCompleteListener(task -> callback.onSuccess(note));
        }
    }
}
