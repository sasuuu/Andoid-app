package com.example.app.some_app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class EndCallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras != null) {
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            if (state != null && state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                final String phoneNumber = extras
                        .getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                if(!isPhoneNumberSaved(phoneNumber, context)){
                    Intent i = new Intent(context, DatabaseManager.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("phoneNumber", phoneNumber);
                    context.startActivity(i);
                }
            }
        }
    }

    private boolean isPhoneNumberSaved(final String phoneNumber, Context context){
        DBManager dbManager = new DBManager(context);
        List<Contact> contactList = dbManager.getRecords();
        return contactList.stream().anyMatch(contact -> contact.getPhone().equals(phoneNumber));
    }
}
