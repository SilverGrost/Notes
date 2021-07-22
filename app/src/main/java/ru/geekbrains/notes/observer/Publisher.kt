package ru.geekbrains.notes.observer

import java.util.*

// Обработчик подписок
class Publisher {
    private val observers: MutableList<ObserverNote>

    // Подписать
    fun subscribe(observer: ObserverNote) {
        observers.add(observer)
    }

    // Отписать
    fun unsubscribe(observer: ObserverNote) {
        observers.remove(observer)
    }

    // Разослать событие
    fun notify(noteID: Int, typeEvent: Int) {
        for (observer in observers) {
            observer.updateNote(noteID, typeEvent)
        }
    }

    //Конструктор
    init {
        observers = ArrayList()
    }
}