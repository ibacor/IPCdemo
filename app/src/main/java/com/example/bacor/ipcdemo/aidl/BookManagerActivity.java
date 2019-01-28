package com.example.bacor.ipcdemo.aidl;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.bacor.ipcdemo.R;

import java.util.List;

/**
 * 这个aidl demo除了客户端/服务端通信添加、查询书籍外，还有AIDL常涉及到的几个店：
 * 1、观察者模式，server通知client
 * 2、解注册
 * 3、注意耗时操作导致的ANR
 * 4、Binder死亡的情况
 * 5、权限验证
 */
public class BookManagerActivity extends AppCompatActivity {
    private static final String TAG = "BookManagerActivity";

    private static final int MSG_ARRIVED = 1;

    private IBookManager remoteBookManager;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case MSG_ARRIVED:
                    Log.d(TAG, "new book arrived: " + msg.obj);
                    break;
            }
        }
    };

    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "binderDied! thread name: " + Thread.currentThread().getName());
            if (remoteBookManager == null)
                return;
            remoteBookManager.asBinder().unlinkToDeath(deathRecipient, 0);
            remoteBookManager = null;
            // 重新连接Service
            Intent intent = new Intent(BookManagerActivity.this, BookManagerService.class);
            bindService(intent, conn, Context.BIND_AUTO_CREATE);
        }
    };

    private IOnNewBookArrivedListener listener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            handler.obtainMessage(MSG_ARRIVED, book).sendToTarget();
        }
    };

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteBookManager = IBookManager.Stub.asInterface(service);
            try {
                remoteBookManager.asBinder().linkToDeath(deathRecipient, 0);

                remoteBookManager.registeListener(listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            remoteBookManager = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiivity_aidl);

        findViewById(R.id.aidl_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 调用远程方法
                        try {
                            List<Book> list = remoteBookManager.getBookList();
                            Log.d(TAG, "list:  " + list.toString());
                            remoteBookManager.addBook(new Book(10, "ten"));
                            List<Book> newList = remoteBookManager.getBookList();
                            Log.d(TAG, "list:  "+ newList.toString());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        // 解注册
        if(remoteBookManager != null && remoteBookManager.asBinder().isBinderAlive()) {
            try {
                remoteBookManager.unregisteListener(listener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        unbindService(conn);
        super.onDestroy();
    }
}
