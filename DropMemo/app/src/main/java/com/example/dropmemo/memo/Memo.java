package com.example.dropmemo.memo;

public class Memo {
    public int id;
    public String place;
    public String content;
    public boolean isFavorite;
    public long updatedAt;

    public Memo(int id, String place, String content, boolean isFavorite, long updatedAt) {
        this.id = id;
        this.place = place;
        this.content = content;
        this.isFavorite = isFavorite;
        this.updatedAt = updatedAt;
    }
}
