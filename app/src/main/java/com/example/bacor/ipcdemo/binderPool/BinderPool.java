package com.example.bacor.ipcdemo.binderPool;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

public class BinderPool {
    private static final String TAG = "BinderPool";

    public static final int BINDER_COMPUTE = 1;
    public static final int BINDER_SECURITY = 2;

    private Context mContext;
    private static volatile BinderPool sInstance;
    private CountDownLatch mCountDownLatch;

    private IBinderPool mBinderPool;

    private BinderPool(Context context) {
        mContext = context.getApplicationContext();
        connectService();
    }

    public static BinderPool getInstance(Context context) {
        if (sInstance == null) {
            synchronized (BinderPool.class) {
                if (sInstance == null) {
                    sInstance = new BinderPool(context);
                }
            }
        }
        return sInstance;
    }

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.w(TAG, "binderDied!");
            mBinderPool.asBinder().unlinkToDeath(mDeathRecipient, 0);
            mDeathRecipient = null;
            connectService();
        }
    };

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinderPool = IBinderPool.Stub.asInterface(service);
            try {
                mBinderPool.asBinder().linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private synchronized void connectService() {
        mCountDownLatch = new CountDownLatch(1);
        Intent intent = new Intent(mContext, BinderPoolService.class);
        mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE);
        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 代理查询
    public IBinder queryBinder(int code) {
        try {
            if (mBinderPool != null)
                return mBinderPool.queryBinder(code);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class BinderPoolImpl extends IBinderPool.Stub {
        public BinderPoolImpl() {
            super();
        }

        @Override
        public IBinder queryBinder(int code) throws RemoteException {
            switch (code) {
                case BINDER_COMPUTE:
                    return new ComputeImpl();
                case BINDER_SECURITY:
                    return new SecurityCenterImpl();
            }
            return null;
        }
    }
}
