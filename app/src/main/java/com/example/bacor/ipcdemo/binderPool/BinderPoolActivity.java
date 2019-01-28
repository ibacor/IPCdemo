package com.example.bacor.ipcdemo.binderPool;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.bacor.ipcdemo.R;

public class BinderPoolActivity extends AppCompatActivity {
    private static final String TAG = "BinderPoolActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binderpool);

        findViewById(R.id.compute_binder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new doCompute()).start();
            }
        });

        findViewById(R.id.security_binder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new doSecurity()).start();
            }
        });
    }

    private class doCompute implements Runnable {
        @Override
        public void run() {
            BinderPool binderPool = BinderPool.getInstance(BinderPoolActivity.this);
            IBinder binder = binderPool.queryBinder(BinderPool.BINDER_COMPUTE);
            ICompute compute = ComputeImpl.asInterface(binder);

            try {
                Log.d(TAG, "1+2 = " + compute.add(1, 2));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private class doSecurity implements Runnable{
        @Override
        public void run() {
            BinderPool binderPool = BinderPool.getInstance(BinderPoolActivity.this);
            IBinder binder = binderPool.queryBinder(BinderPool.BINDER_SECURITY);
            ISecurityCenter securityCenter = SecurityCenterImpl.asInterface(binder);

            String msg = "hello world!";
            try {
                String password = securityCenter.encrypt(msg);
                Log.d(TAG, "password: " + password);
                Log.d(TAG, "msg: " + securityCenter.decrypt(password));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}