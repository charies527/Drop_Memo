package com.example.dropmemo.memo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "memo.db";
    private static final int DB_VERSION = 13;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE memo (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "place TEXT, " +
                "content TEXT, " +
                "is_favorite INTEGER DEFAULT 0, " +
                "is_alarm INTEGER DEFAULT 0, " +
                "updated_at INTEGER, " +
                "latitude REAL, " +
                "longitude REAL)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        addColumnIfMissing(db, "is_favorite", "INTEGER DEFAULT 0");
        addColumnIfMissing(db, "updated_at", "INTEGER DEFAULT 0");
        addColumnIfMissing(db, "is_alarm", "INTEGER DEFAULT 0");
        addColumnIfMissing(db, "latitude", "REAL");
        addColumnIfMissing(db, "longitude", "REAL");
    }

    private void addColumnIfMissing(SQLiteDatabase db, String column, String type) {
        try {
            db.execSQL("ALTER TABLE memo ADD COLUMN " + column + " " + type);
        } catch (Exception ignored) {
        }
    }

    public void insertMemo(String place, String content, boolean isFavorite, boolean isAlarm, Double latitude, Double longitude) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("place", place);
        values.put("content", content);
        values.put("is_favorite", isFavorite ? 1 : 0);
        values.put("is_alarm", isAlarm ? 1 : 0);
        values.put("updated_at", System.currentTimeMillis());
        putNullableDouble(values, "latitude", latitude);
        putNullableDouble(values, "longitude", longitude);
        db.insert("memo", null, values);
    }

    public ArrayList<Memo> getAllMemos() {
        ArrayList<Memo> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, place, content, is_favorite, is_alarm, updated_at, latitude, longitude FROM memo ORDER BY is_favorite DESC, updated_at DESC", null);

        while (cursor.moveToNext()) {
            list.add(readMemo(cursor));
        }

        cursor.close();
        return list;
    }

    public ArrayList<Memo> getAlarmMemos() {
        ArrayList<Memo> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, place, content, is_favorite, is_alarm, updated_at, latitude, longitude FROM memo WHERE is_alarm = 1 AND latitude IS NOT NULL AND longitude IS NOT NULL", null);

        while (cursor.moveToNext()) {
            list.add(readMemo(cursor));
        }

        cursor.close();
        return list;
    }

    private Memo readMemo(Cursor cursor) {
        return new Memo(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3) == 1,
                cursor.getInt(4) == 1,
                cursor.getLong(5),
                cursor.isNull(6) ? null : cursor.getDouble(6),
                cursor.isNull(7) ? null : cursor.getDouble(7)
        );
    }

    public void updateFavorite(int id, boolean isFavorite) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_favorite", isFavorite ? 1 : 0);
        db.update("memo", values, "id=?", new String[]{String.valueOf(id)});
    }

    public void updateAlarm(int id, boolean isAlarm) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_alarm", isAlarm ? 1 : 0);
        db.update("memo", values, "id=?", new String[]{String.valueOf(id)});
    }

    public void updateMemo(int id, String place, String content, boolean isAlarm, Double latitude, Double longitude) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("place", place);
        values.put("content", content);
        values.put("is_alarm", isAlarm ? 1 : 0);
        values.put("updated_at", System.currentTimeMillis());
        putNullableDouble(values, "latitude", latitude);
        putNullableDouble(values, "longitude", longitude);
        db.update("memo", values, "id=?", new String[]{String.valueOf(id)});
    }

    public void deleteMemo(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("memo", "id=?", new String[]{String.valueOf(id)});
    }

    private void putNullableDouble(ContentValues values, String key, Double value) {
        if (value == null) {
            values.putNull(key);
        } else {
            values.put(key, value);
        }
    }
}
