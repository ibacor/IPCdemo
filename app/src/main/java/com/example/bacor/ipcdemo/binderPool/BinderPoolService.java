package com.example.bacor.ipcdemo.binderPool;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BinderPoolService extends Service {
    private static final String TAG = "BinderPoolService";

    private Binder mbinderPool = new BinderPool.BinderPoolImpl();

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "binderPool bind service");
        return mbinderPool;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
