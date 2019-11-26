package com.example.diaremake;

public class DailyModelData {
    private long createDate;
    private String date;
    private String weather;
    private String imgUrl;
    private String text;
    private int alignment;
    private int gravity;
    public DailyModelData() {
    }



    public DailyModelData(long createDate, String date, String weather, String imgUrl, String text, int alignment, int gravity) {
        this.createDate = createDate;
        this.date = date;
        this.weather = weather;
        this.imgUrl = imgUrl;
        this.text = text;
        this.alignment = alignment;
        this.gravity = gravity;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }
}
