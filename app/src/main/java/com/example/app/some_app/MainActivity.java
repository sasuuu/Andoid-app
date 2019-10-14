package com.example.app.some_app;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public void addButtonListener(Button btn,final Class<?> cls){
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),cls);
                startActivity(intent);
            }
        };

        btn.setOnClickListener(l);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button comp_lvl,audio_player,db_manager,translate;
        comp_lvl = findViewById(R.id.compass_level);
        audio_player = findViewById(R.id.audio_player);
        db_manager = findViewById(R.id.db_manager);
        translate = findViewById(R.id.translate_button);
        checkReceiverPermissions();
        addButtonListener(comp_lvl,CompassLevel.class);
        addButtonListener(audio_player,AudioPlayer.class);
        addButtonListener(db_manager,DatabaseManager.class);
        addButtonListener(translate, Translate.class);
    }

    private void checkReceiverPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    666);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupCompImg();
        setupAudioImg();
        setupDbImg();
    }

    private void setupDbImg(){
        ImageView db_img;
        db_img = findViewById(R.id.db_img);
        Animation db_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.db_anim);
        db_img.startAnimation(db_anim);
    }

    private void setupCompImg(){
        ImageView comp_img;
        comp_img = findViewById(R.id.compass_img);
        Animation comp_anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.compass_anim);
        comp_img.startAnimation(comp_anim);
    }

    private void setupAudioImg(){
        ImageView audio_img;
        final int color = getResources().getColor(R.color.colorPrimary);
        audio_img = findViewById(R.id.audio_img);
        audio_img.setBackgroundResource(R.drawable.audio_anim);
        AnimationDrawable audio_anim = (AnimationDrawable) audio_img.getBackground();
        audio_anim.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        ((Animatable)audio_anim).start();
    }
}
