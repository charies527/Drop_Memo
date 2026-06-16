package com.example.dropmemo.memo;

public class Memo {
    public int id;
    public String place;
    public String content;
    public boolean isFavorite;
    public boolean isAlarm;
    public long updatedAt;

    public Memo(int id, String place, String content, boolean isFavorite, boolean isAlarm, long updatedAt) {
        this.id = id;
        this.place = place;
        this.content = content;
        this.isFavorite = isFavorite;
        this.isAlarm = isAlarm;
        this.updatedAt = updatedAt;
    }
}