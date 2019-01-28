package com.example.bacor.ipcdemo.binderPool;

import android.os.RemoteException;

public class SecurityCenterImpl extends ISecurityCenter.Stub {
    private static final char KEY = '1';

    @Override
    public String encrypt(String content) throws RemoteException {
        char[] chars = content.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            chars[i] ^= KEY;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String content) throws RemoteException {
        return encrypt(content);
    }
}
