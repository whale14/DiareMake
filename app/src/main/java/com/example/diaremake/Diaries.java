package com.example.diaremake;

public class Diaries {
    private String img;
    private String title;

    public Diaries() {
    }

    public Diaries(String img, String title) {
        this.img = img;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
