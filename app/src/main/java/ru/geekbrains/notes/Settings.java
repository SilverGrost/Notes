package ru.geekbrains.notes;

public class Settings {
    private int sortType;
    private int textSize;

    public Settings(int sortType, int textSize) {
        this.sortType = sortType;
        this.textSize = textSize;
    }

    public Settings() {
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
}
