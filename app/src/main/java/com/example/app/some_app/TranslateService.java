package com.example.app.some_app;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.wifi.hotspot2.pps.Credential;
import android.os.Environment;
import android.support.annotation.RawRes;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.gax.core.GoogleCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class TranslateService extends IntentService {

    public static final String NOTIFICATION = "com.example.app.some_app.receiver";
    public static final String DATA = "data";
    public static final String RESULT = "result";
    public static final String TARGET_LANGUAGE = "targetLanguage";

    public TranslateService() {
        super("TranslateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int result;
        String data = intent.getStringExtra(DATA);
        String targetLanguage = intent.getStringExtra(TARGET_LANGUAGE);
        String translatedText = translate(data, targetLanguage);
        if (translatedText != null) {
            result = Activity.RESULT_OK;
            publishResults(translatedText, result);
        }else{
            result = Activity.RESULT_CANCELED;
            publishResults("", result);
        }
    }

    private String translate(String textToTranslate, String targetLanguage) {
        try (InputStream is = getResources().openRawResource(R.raw.credentials)){
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(is);
            TranslateOptions options = TranslateOptions.newBuilder()
                    .setCredentials(googleCredentials)
						.build();
            Translate trService = options.getService();
            Translation translation = trService.translate(textToTranslate, Translate.TranslateOption.targetLanguage(targetLanguage));
            return translation.getTranslatedText();
        }
        catch(Exception e) {
            return e.getMessage();
        }
    }

    private void publishResults(String outputData, int result) {
        Intent intent = new Intent(NOTIFICATION);
        intent.putExtra(DATA, outputData);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}
