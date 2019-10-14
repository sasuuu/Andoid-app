package com.example.app.some_app;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class Contacts extends Fragment {

    private TableLayout tl;
    private DBManager db;

    public Contacts() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DBManager(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tl = getView().findViewById(R.id.contact_list);
        contactsViewUpdate();
    }

    private TextView getTextView(String text){
        TextView textView = new TextView(getContext());
        textView.setPadding(10,0,10,0);
        textView.setText(text);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        return textView;
    }

    private Button getButton(final int id){
        Button button = new Button(getContext());
        button.setText(R.string.remove);
        button.setPadding(10,0,10,0);
        button.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    db.removeRecord(id);
                }catch(SQLiteException e){
                    Toast.makeText(getContext(),R.string.remove_contact_error,Toast.LENGTH_SHORT).show();
                }
                contactsViewUpdate();
            }
        });
        return button;
    }

    private void updateRow(final Contact cont){
        TableRow tr = new TableRow(getContext());
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
            Toast.makeText(getContext(),R.string.load_contacts_error,Toast.LENGTH_SHORT).show();
        }
    }
}
