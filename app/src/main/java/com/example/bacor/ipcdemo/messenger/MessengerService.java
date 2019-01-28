package com.example.bacor.ipcdemo.messenger;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.bacor.ipcdemo.utils.Constants;

public class MessengerService extends Service {
    private static final String TAG = "MessengerService";

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case Constants.FROM_CLIENT:
                    Log.d(TAG, "receive msg from client:  " + msg.getData().getString("msg"));
                    Messenger client = msg.replyTo;
                    Message reply = Message.obtain(null, Constants.FROM_SERVICE);
                    Bundle bundle = new Bundle();
                    bundle.putString("reply", "reply from the service");
                    reply.setData(bundle);
                    try {
                        client.send(reply);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private Messenger messenger = new Messenger(handler);

    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }
}
