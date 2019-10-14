package com.example.app.some_app;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Translate extends AppCompatActivity {

    private TextView translatedText;
    private EditText textToTranslate;
    private Button translateButton;
    private Spinner targetLanguage;
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String data = bundle.getString(TranslateService.DATA);
                int resultCode = bundle.getInt(TranslateService.RESULT);
                if (resultCode == RESULT_OK) {
                    translatedText.setText(data);
                } else {
                    translatedText.setText(R.string.translate_failed);
                }
            }
        }
    };

    private void checkPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    666);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    666);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        textToTranslate = findViewById(R.id.textToTranslate);
        translatedText = findViewById(R.id.translatedText);
        translateButton = findViewById(R.id.translateButton);
        targetLanguage = findViewById(R.id.target_language);
        translateButton.setOnClickListener(l -> {
            Intent intent = new Intent(this, TranslateService.class);
            intent.putExtra(TranslateService.DATA, textToTranslate.getText().toString());
            intent.putExtra(TranslateService.TARGET_LANGUAGE, targetLanguage.getSelectedItem().toString());
            startService(intent);
            translatedText.setText(R.string.translating);
        });
        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(
                TranslateService.NOTIFICATION));
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
