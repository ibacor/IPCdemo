package com.example.bacor.ipcdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.bacor.ipcdemo.aidl.BookManagerActivity;
import com.example.bacor.ipcdemo.binderPool.BinderPoolActivity;
import com.example.bacor.ipcdemo.messenger.MessengerActivity;
import com.example.bacor.ipcdemo.provider.ProviderActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.messenger).setOnClickListener(this);
        findViewById(R.id.aidl).setOnClickListener(this);
        findViewById(R.id.provider).setOnClickListener(this);
        findViewById(R.id.binderpool).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch(v.getId()){
            case R.id.messenger:
                intent.setClass(this,MessengerActivity.class);
                break;
            case R.id.aidl:
                intent.setClass(this, BookManagerActivity.class);
                break;
            case R.id.provider:
                intent.setClass(this, ProviderActivity.class);
                break;
            case R.id.binderpool:
                intent.setClass(this, BinderPoolActivity.class);
                break;
        }
        startActivity(intent);
    }
}
