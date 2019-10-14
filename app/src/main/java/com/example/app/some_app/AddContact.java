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
import android.widget.EditText;
import android.widget.Toast;


public class AddContact extends Fragment {

    private EditText name,surname,phone;
    private DBManager db;

    private OnFragmentInteractionListener mListener;

    public AddContact() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DBManager(getContext());

    }

    private void addContact(){
        Contact newContact = new Contact();
        newContact.setId(0L);
        if(name.getText().length()<1){
            Toast.makeText(getContext(),R.string.fill_name,Toast.LENGTH_SHORT).show();
            return;
        }
        newContact.setName(name.getText().toString());
        if(surname.getText().length()<1){
            Toast.makeText(getContext(),R.string.fill_surname,Toast.LENGTH_SHORT).show();
            return;
        }
        newContact.setSurname(surname.getText().toString());
        if(phone.getText().length()<1){
            Toast.makeText(getContext(),R.string.fill_phone,Toast.LENGTH_SHORT).show();
            return;
        }
        newContact.setPhone(phone.getText().toString());
        try {
            db.addRecord(newContact);
        }catch(SQLiteException e){
            Toast.makeText(getContext(),R.string.add_contact_error,Toast.LENGTH_SHORT).show();
        }
        mListener.UpdateContacts();
    }

    public void SetPhoneNumber(String phoneNumber){
        phone.setText(phoneNumber);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_contact, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button add_contact_btn;
        add_contact_btn = getView().findViewById(R.id.add_contact_btn);
        name = getView().findViewById(R.id.name_text);
        surname = getView().findViewById(R.id.surname_text);
        phone = getView().findViewById(R.id.phone_text);
        add_contact_btn.setOnClickListener(l -> addContact());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void UpdateContacts();
    }
}
