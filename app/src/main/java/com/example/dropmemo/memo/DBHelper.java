package com.example.dropmemo.memo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "memo.db";

    private static final int DB_VERSION = 6;

    public DBHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE memo (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "place TEXT, " +
                "content TEXT, " +
                "is_favorite INTEGER DEFAULT 0, " +
                "updated_at INTEGER)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("ALTER TABLE memo ADD COLUMN is_favorite INTEGER DEFAULT 0");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            db.execSQL("ALTER TABLE memo ADD COLUMN updated_at INTEGER DEFAULT 0");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertMemo(String place, String content, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("place", place);
        values.put("content", content);
        values.put("is_favorite", isFavorite ? 1 : 0);
        values.put("updated_at", System.currentTimeMillis());

        db.insert("memo", null, values);
    }

    public ArrayList<Memo> getAllMemos() {
        ArrayList<Memo> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM memo ORDER BY is_favorite DESC, updated_at DESC", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String place = cursor.getString(1);
            String content = cursor.getString(2);

            int isFavoriteInt = cursor.getInt(3);
            boolean isFavorite = (isFavoriteInt == 1);

            long updatedAt = cursor.getLong(4);

            list.add(new Memo(id, place, content, isFavorite, updatedAt));
        }

        cursor.close();
        return list;
    }

    public void updateFavorite(int id, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("is_favorite", isFavorite ? 1 : 0);

        db.update("memo", values, "id=?", new String[]{String.valueOf(id)});
    }

    public void updateMemo(int id, String place, String content) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("place", place);
        values.put("content", content);

        values.put("updated_at", System.currentTimeMillis());

        db.update("memo", values, "id=?",
                new String[]{String.valueOf(id)});
    }

    public void deleteMemo(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("memo", "id=?", new String[]{String.valueOf(id)});
    }
}