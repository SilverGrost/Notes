package ru.geekbrains.notes.note;

import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable {
    private String value;
    private int id;
    private long dateEdit;
    private long dateCreate;

    public Note() {
        this.id = -1;
    }

    public long getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(long dateCreate) {
        this.dateCreate = dateCreate;
    }

    public Note(String value, int id, long dateEdit, long dateCreate) {
        this.value = value;
        this.id = id;
        this.dateEdit = dateEdit;
        this.dateCreate = dateCreate;
    }

    protected Note(Parcel in) {
        value = in.readString();
        id = in.readInt();
        dateEdit = in.readLong();
        dateCreate = in.readLong();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getID() {
        return id;
    }

    public void setID(int category) {
        this.id = category;
    }

    public long getDateEdit() {
        return dateEdit;
    }

    public void setDateEdit(long dateEdit) {
        this.dateEdit = dateEdit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getValue());
        dest.writeInt(getID());
        dest.writeLong(getDateEdit());
        dest.writeLong(getDateCreate());
    }

}
