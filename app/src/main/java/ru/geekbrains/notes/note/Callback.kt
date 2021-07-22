package ru.geekbrains.notes.note

interface Callback<T> {
    fun onSuccess(result: T)
}