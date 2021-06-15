package ru.geekbrains.notes.note;

import android.os.Parcel;
import android.os.Parcelable;


public class Note implements Parcelable {
    private String value;
    private String header;
    private int id;
    private long date;

    public Note() {
        this.id = -1;
    }

    public Note(String value, String header, int id, long date) {
        this.value = value;
        this.header = header;
        this.id = id;
        this.date = date;
    }

    protected Note(Parcel in) {
        value = in.readString();
        header = in.readString();
        id = in.readInt();
        date = in.readLong();
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

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getID() {
        return id;
    }

    public void setID(int category) {
        this.id = category;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getValue());
        dest.writeString(getHeader());
        dest.writeInt(getID());
        dest.writeLong(getDate());
    }
}
