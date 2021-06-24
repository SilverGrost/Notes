package ru.geekbrains.notes;

public class Settings {
    private int orderType;
    private int textSizeId;
    private int maxCountLinesId;

    private int currentPosition;

    private float textSize;
    private int maxCountLines;

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public Settings() {
    }

    public Settings(int orderType, int textSizeId, int maxCountLinesId) {
        this.orderType = orderType;
        this.textSizeId = textSizeId;
        this.maxCountLinesId = maxCountLinesId;
    }

    public int getOrderType() {
        return orderType;
    }

    public int getTextSizeId() {
        return textSizeId;
    }

    public void setTextSizeId(int textSizeId) {
        this.textSizeId = textSizeId;
    }

    public int getMaxCountLinesId() {
        return maxCountLinesId;
    }

    public void setMaxCountLinesId(int maxCountLinesId) {
        this.maxCountLinesId = maxCountLinesId;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public int getMaxCountLines() {
        return maxCountLines;
    }

    public void setMaxCountLines(int maxCountLines) {
        this.maxCountLines = maxCountLines;
    }

}
