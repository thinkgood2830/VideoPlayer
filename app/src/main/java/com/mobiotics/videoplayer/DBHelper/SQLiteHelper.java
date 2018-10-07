package com.mobiotics.videoplayer.DBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Video.db";
    private static final int DATABASE_VERSION = 2;

    public static final String VIDEO_TABLE_NAME      = "mobiotics";
    public static final String VIDEO_COLUMN_ID       = "id";
    public static final String VIDEO_COLUMN_TITLE    = "title";
    public static final String VIDEO_COLUMN_DURATION = "leftduration";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + VIDEO_TABLE_NAME +
                        "(" + VIDEO_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                        VIDEO_COLUMN_TITLE + " TEXT, " +
                        VIDEO_COLUMN_DURATION + " TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + VIDEO_TABLE_NAME);
        onCreate(db);
    }

    public boolean addDB(String name, String lTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(VIDEO_COLUMN_TITLE, name);
        contentValues.put(VIDEO_COLUMN_DURATION, lTime);

        db.insert(VIDEO_TABLE_NAME, null, contentValues);
        return true;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, VIDEO_TABLE_NAME);
        return numRows;
    }

    public boolean updateDB(String title, String lTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VIDEO_COLUMN_TITLE, title);
        contentValues.put(VIDEO_COLUMN_DURATION, lTime);
        db.update(VIDEO_TABLE_NAME, contentValues, VIDEO_COLUMN_TITLE + " = ? ", new String[] {title} );
        return true;
    }

    public Integer deleteDB(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(VIDEO_TABLE_NAME,
                VIDEO_COLUMN_ID + " = ? ",
                new String[] { Integer.toString(id) });
    }

    public Cursor selectDB(String title) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + VIDEO_TABLE_NAME + " WHERE " +
                VIDEO_COLUMN_TITLE + "=?", new String[]{(title)});
        return res;
    }
}
