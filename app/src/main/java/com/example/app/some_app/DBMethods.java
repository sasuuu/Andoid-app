package com.example.app.some_app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.LinkedList;
import java.util.List;

class DBMethods {

    void addContact(SQLiteDatabase db,Contact contact){
        ContentValues newValues = new ContentValues();
        newValues.put("name",contact.getName());
        newValues.put("surname",contact.getSurname());
        newValues.put("phone",contact.getPhone());
        db.insertOrThrow("contacts",null,newValues);
    }

    private Contact getContact(Cursor cursor){
        Contact contact = new Contact();
        contact.setId(cursor.getLong(0));
        contact.setName(cursor.getString(1));
        contact.setSurname(cursor.getString(2));
        contact.setPhone(cursor.getString(3));
        return contact;
    }

    List<Contact> getContacts(SQLiteDatabase db){
        List<Contact> contacts = new LinkedList<Contact>();
        String[] columns={"id","name","surname","phone"};
        Cursor cursor = db.query("contacts",columns,null,null,null,null,null);
        while(cursor.moveToNext()){
            contacts.add(getContact(cursor));
        }
        cursor.close();
        return contacts;
    }

    void removeContact(SQLiteDatabase db,int id){
        String[] args={""+id};
        db.delete("contacts","id=?",args);
    }
}
