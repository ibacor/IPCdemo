package com.example.bacor.ipcdemo.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.bacor.ipcdemo.R;
import com.example.bacor.ipcdemo.aidl.Book;

public class ProviderActivity extends AppCompatActivity {
    private static final String TAG = "ProviderActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);

        findViewById(R.id.book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://com.example.bacor.ipcdemo.book.provider/book");
                Cursor bookCursor = getContentResolver().query(uri, null, null, null, null);
                while(bookCursor.moveToNext()){
                    Book book = new Book(bookCursor.getInt(0), bookCursor.getString(1));
                    Log.d(TAG, "query book: " + book);
                }
                bookCursor.close();

                ContentValues values = new ContentValues();
                values.put("_id", 4);
                values.put("name", "book4");
                getContentResolver().insert(uri, values);

                bookCursor = getContentResolver().query(uri, null, null, null, null);
                while(bookCursor.moveToNext()){
                    Book book = new Book(bookCursor.getInt(0), bookCursor.getString(1));
                    Log.d(TAG, "query book: " + book);
                }
                bookCursor.close();
            }
        });

        findViewById(R.id.user).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://com.example.bacor.ipcdemo.book.provider/user");
                Cursor userCursor = getContentResolver().query(uri, null, null, null, null);
                while(userCursor.moveToNext()){
                    User user = new User(userCursor.getInt(0),
                            userCursor.getString(1),
                            userCursor.getInt(2));
                    Log.d(TAG, "query user: " + user);
                }
            }
        });
    }
}
