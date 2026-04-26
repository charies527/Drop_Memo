package com.cookandroid.real_memo;

public class Memo {
    public int id;
    public String place;
    public String content;
    public boolean isFavorite;

    public Memo(int id, String place, String content, boolean isFavorite) {
        this.id = id;
        this.place = place;
        this.content = content;
        this.isFavorite = isFavorite;
    }
}
