package com.example.bacor.ipcdemo.messenger;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.bacor.ipcdemo.R;
import com.example.bacor.ipcdemo.utils.Constants;

public class MessengerActivity extends AppCompatActivity {
    private static final String TAG = "MessengerActivity";

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case Constants.FROM_SERVICE:
                    Log.d(TAG, "receive msg from service:  " + msg.getData().getString("reply"));
                    break;
            }
        }
    };

    private Messenger sendMessenger;
    private Messenger replyMessenger = new Messenger(handler);

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            sendMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);

        findViewById(R.id.messenger_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg = Message.obtain(null, Constants.FROM_CLIENT);
                Bundle bundle = new Bundle();
                bundle.putString("msg", "hello, it is from client");
                msg.setData(bundle);
                msg.replyTo = replyMessenger;
                try {
                    sendMessenger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

//        Intent intent = new Intent("com.example.bacor.MessengerService.launch");
//        intent.setPackage(this.getPackageName());
        Intent intent = new Intent(this, MessengerService.class);
        bindService(intent,conn,Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }
}
