package ru.geekbrains.notes.note;

public interface Callback<T> {

    void onSuccess(T result);
}
