package com.example.bacor.ipcdemo.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "book_provider.db";
    private static final int DB_VERSION = 1;

    public static final String BOOK_TABLE = "book";
    public static final String USER_TABLE = "user";

    private String CREATE_BOOK = "CREATE TABLE IF NOT EXISTS " + BOOK_TABLE + "(_id INTEGER PRIMARY KEY, name TEXT)";
    private String CREATE_USER = "CREATE TABLE IF NOT EXISTS " + USER_TABLE + "(_id INTEGER PRIMARY KEY, name TEXT, sex INT)";

    public DbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
        db.execSQL(CREATE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
