package com.example.bacor.ipcdemo.aidl;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {
    private static final String TAG = "BookManagerService";

    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<>();

    private AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);

    private IBinder mBinder = new IBookManager.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registeListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.register(listener);
            int N = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.d(TAG, "listener list size: " + N);
        }

        @Override
        public void unregisteListener(IOnNewBookArrivedListener listener) throws RemoteException {
            mListenerList.unregister(listener);
            int N = mListenerList.beginBroadcast();
            mListenerList.finishBroadcast();
            Log.d(TAG, "listener list size: " + N);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            // 检验权限
            // manifest的方式
            int check = checkCallingOrSelfPermission("com.example.bacor.ipcdemo.permission.BOOK_MANAGER_SERVICE");
            if(check == PackageManager.PERMISSION_DENIED)
                return false;
            // 调用者包名的方式
            String packageName = "";
            String[] packages = getPackageManager().getPackagesForUid(getCallingUid());
            if(packages != null && packages.length > 0)
                packageName = packages[0];
            Log.d(TAG, "packageName: " + packageName);
            if(!packageName.startsWith("com.example.bacor"))
                return false;
            return super.onTransact(code, data, reply, flags);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1,"one"));
        mBookList.add(new Book(2,"two"));
        new Thread(new Tasks()).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 检验权限
        // manifest的方式
        int check = checkCallingOrSelfPermission("com.example.bacor.ipcdemo.permission.BOOK_MANAGER_SERVICE");
        if(check == PackageManager.PERMISSION_DENIED)
            return null;

        return mBinder;
    }


    @Override
    public void onDestroy() {
        mIsServiceDestroyed.set(true);
        super.onDestroy();
    }

    private void notifyBookArrived(Book book) throws RemoteException {
        int N = mListenerList.beginBroadcast();
        for (int i = 0; i < N; i++) {
            IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
            if(listener != null){
                listener.onNewBookArrived(book);
            }
        }
        mListenerList.finishBroadcast();
    }

    private class Tasks implements Runnable{
        @Override
        public void run() {
            while(!mIsServiceDestroyed.get()) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size() + 1;
                Book book = new Book(bookId, "book"+bookId);
                mBookList.add(book);
                try {
                    notifyBookArrived(book);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
