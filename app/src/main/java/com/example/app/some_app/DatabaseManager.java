package com.example.app.some_app;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

public class DatabaseManager extends AppCompatActivity implements AddContact.OnFragmentInteractionListener {

    private DBManager db;
    private EditText name,surname,phone;
    private TableLayout tl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_manager);
//        Button add_contact_btn;
//        db = new DBManager(this);
//        add_contact_btn = findViewById(R.id.add_contact_btn);
//        name = findViewById(R.id.name_text);
//        surname = findViewById(R.id.surname_text);
//        phone = findViewById(R.id.phone_text);
//        tl = findViewById(R.id.contact_list);
//        add_contact_btn.setOnClickListener(l -> addContact());
        checkExtras();
    }

    private void checkExtras(){
        Intent intent = getIntent();
        String phoneNumber = intent.getStringExtra("phoneNumber");
        if(phoneNumber != null && !phoneNumber.isEmpty()){
            AddContact fragment = (AddContact) getSupportFragmentManager()
                    .findFragmentById(R.id.add_contact_fragment);

            if (fragment != null && fragment.isInLayout()) {
                fragment.SetPhoneNumber(phoneNumber);
            }
        }
    }

    @Override
    public void UpdateContacts() {
        Contacts fragment = (Contacts) getSupportFragmentManager()
                .findFragmentById(R.id.contacts_fragment);

        if (fragment != null && fragment.isInLayout()) {
            fragment.contactsViewUpdate();
        }
    }

    private void addContact(){
        Contact newContact = new Contact();
        newContact.setId(0L);
        if(name.getText().length()<1){
            Toast.makeText(getApplicationContext(),R.string.fill_name,Toast.LENGTH_SHORT).show();
            return;
        }
        newContact.setName(name.getText().toString());
        if(surname.getText().length()<1){
            Toast.makeText(getApplicationContext(),R.string.fill_surname,Toast.LENGTH_SHORT).show();
            return;
        }
        newContact.setSurname(surname.getText().toString());
        if(phone.getText().length()<1){
            Toast.makeText(getApplicationContext(),R.string.fill_phone,Toast.LENGTH_SHORT).show();
            return;
        }
        newContact.setPhone(phone.getText().toString());
        try {
            db.addRecord(newContact);
        }catch(SQLiteException e){
            Toast.makeText(getApplicationContext(),R.string.add_contact_error,Toast.LENGTH_SHORT).show();
        }
        contactsViewUpdate();
    }

    private TextView getTextView(String text){
        TextView textView = new TextView(this);
        textView.setPadding(10,0,10,0);
        textView.setText(text);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        return textView;
    }

    private Button getButton(final int id){
        Button button = new Button(this);
        button.setText(R.string.remove);
        button.setPadding(10,0,10,0);
        button.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    db.removeRecord(id);
                }catch(SQLiteException e){
                    Toast.makeText(getApplicationContext(),R.string.remove_contact_error,Toast.LENGTH_SHORT).show();
                }
                contactsViewUpdate();
            }
        });
        return button;
    }

    private void updateRow(final Contact cont){
        TableRow tr = new TableRow(this);
        tr.setPadding(10,0,10,0);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(getTextView(cont.getName()),0);
        tr.addView(getTextView(cont.getSurname()),1);
        tr.addView(getTextView(cont.getPhone()),2);
        tr.addView(getButton(cont.getId().intValue()),3);
        tl.addView(tr,new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.FILL_PARENT));
    }

    public void contactsViewUpdate(){
        tl.removeViewsInLayout(1,tl.getChildCount()-1);
        try {
            for (final Contact cont : db.getRecords()) {
                updateRow(cont);
            }
        }catch(SQLiteException e){
            Toast.makeText(getApplicationContext(),R.string.load_contacts_error,Toast.LENGTH_SHORT).show();
        }
    }
}
