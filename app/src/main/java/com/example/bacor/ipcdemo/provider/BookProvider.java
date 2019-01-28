package com.example.bacor.ipcdemo.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class BookProvider extends ContentProvider {
    private static final String TAG = "BookProvider";

    private static final String AUTHORITY = "com.example.bacor.ipcdemo.book.provider";
    private static final int BOOK_CODE = 1;
    private static final int USER_CODE = 2;
    private static final UriMatcher uriMatch = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        uriMatch.addURI(AUTHORITY, "book", BOOK_CODE);
        uriMatch.addURI(AUTHORITY, "user", USER_CODE);
    }

    private SQLiteDatabase mDb;

    @Override
    public boolean onCreate() {
        Log.d(TAG, "provider thread: " + Thread.currentThread().getName());
        mDb = new DbHelper(getContext()).getWritableDatabase();

        mDb.execSQL("delete from book");
        mDb.execSQL("delete from book");
        mDb.execSQL("insert into book values(1, 'book1')");
        mDb.execSQL("insert into book values(2, 'book2')");
        mDb.execSQL("insert into book values(3, 'book3')");
        mDb.execSQL("insert into user values(4, 'user1', 0)");
        mDb.execSQL("insert into user values(5, 'user2', 1)");
        return true;
    }

    @Override
    public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String tableName = getTableName(uri);

        return mDb.query(tableName, projection, selection, selectionArgs, null, null, sortOrder, null);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table = getTableName(uri);

        mDb.insert(table, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table = getTableName(uri);

        int count = mDb.delete(table, selection, selectionArgs);
        if(count > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table = getTableName(uri);

        int row = mDb.update(table, values, selection, selectionArgs);
        if(row > 0)
            getContext().getContentResolver().notifyChange(uri, null);
        return row;
    }

    private String getTableName(Uri uri){
        switch(uriMatch.match(uri)){
            case BOOK_CODE:
                return DbHelper.BOOK_TABLE;
            case USER_CODE:
                return DbHelper.USER_TABLE;
        }
        return null;
    }
}
