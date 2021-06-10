package ru.geekbrains.notes.domain.observer;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.notes.domain.note.Note;

// Обработчик подписок
public class Publisher {

    private List<Observer> observers;

    //Конструктор
    public Publisher() {
        observers = new ArrayList<>();
    }

    // Подписать
    public void subscribe(Observer observer) {
        observers.add(observer);
    }

    // Отписать
    public void unsubscribe(Observer observer) {
        observers.remove(observer);
    }

    // Разослать событие
    public void notify(Note note) {
        for (Observer observer: observers) {
            observer.updateNote(note);
        }
    }
}