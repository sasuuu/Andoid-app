package com.example.app.some_app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class DBManager extends SQLiteOpenHelper {

    private DBMethods dbMethods;

    DBManager(Context context){
        super(context,"database.db",null,1);
        dbMethods = new DBMethods();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table contacts(id integer primary key autoincrement, name text,surname text,phone text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    void addRecord(Contact contact){
        dbMethods.addContact(getWritableDatabase(),contact);
    }

    List<Contact> getRecords(){
        return dbMethods.getContacts(getReadableDatabase());
    }

    void removeRecord(int id){
        dbMethods.removeContact(getReadableDatabase(),id);
    }
}
