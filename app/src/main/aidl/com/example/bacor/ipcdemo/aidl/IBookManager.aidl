// IBookManager.aidl
package com.example.bacor.ipcdemo.aidl;

// Declare any non-default types here with import statements
import com.example.bacor.ipcdemo.aidl.Book;
import com.example.bacor.ipcdemo.aidl.IOnNewBookArrivedListener;

interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    List<Book> getBookList();
    void addBook(in Book book);
    void registeListener(IOnNewBookArrivedListener listener);
    void unregisteListener(IOnNewBookArrivedListener listener);
}
