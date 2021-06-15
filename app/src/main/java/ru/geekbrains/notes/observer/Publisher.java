package ru.geekbrains.notes.observer;

import java.util.ArrayList;
import java.util.List;

import ru.geekbrains.notes.note.Note;

// Обработчик подписок
public class Publisher {

    private List<ObserverNote> observers;

    //Конструктор
    public Publisher() {
        observers = new ArrayList<>();
    }

    // Подписать
    public void subscribe(ObserverNote observer) {
        observers.add(observer);
    }

    // Отписать
    public void unsubscribe(ObserverNote observer) {
        observers.remove(observer);
    }

    // Разослать событие
    public void notify(int noteID) {
        for (ObserverNote observer: observers) {
            observer.updateNote(noteID);
        }
    }
}