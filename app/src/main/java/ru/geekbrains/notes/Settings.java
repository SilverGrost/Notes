package ru.geekbrains.notes;

public class Settings {
    private int orderType;
    private int textSizeId;
    private int maxCountLinesId;
    private boolean cloudSync;
    private int authTypeService;
    private float textSize;
    private int maxCountLines;
    private int currentPosition;

    public int getAuthTypeService() {
        return authTypeService;
    }

    public void setAuthTypeService(int authTypeService) {
        this.authTypeService = authTypeService;
    }

    public boolean isCloudSync() {
        return cloudSync;
    }

    public void setCloudSync(boolean cloudSync) {
        this.cloudSync = cloudSync;
    }

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

    public Settings(int orderType, int textSizeId, int maxCountLinesId, int authTypeService) {
        this.orderType = orderType;
        this.textSizeId = textSizeId;
        this.maxCountLinesId = maxCountLinesId;
        this.authTypeService = authTypeService;
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
