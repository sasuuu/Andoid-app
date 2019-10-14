package com.example.app.some_app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import lib.folderpicker.FolderPicker;

public class AudioPlayer extends AppCompatActivity {

    private Button prev,next,directory_select,rand,start_stop,volume;
    private TextView dir_path,curr_time,end_time,curr_song;
    private SeekBar progress_bar;
    private MediaPlayer mp;
    private int track_num,start,end;
    private List<String> tracks;
    private boolean randomize_playing;
    private boolean volume_off;
    private Handler myHandler = new Handler();
    private Random randomize;

    private void getUIElements(){
        dir_path = findViewById(R.id.dir_path);
        curr_time = findViewById(R.id.current_time);
        end_time = findViewById(R.id.end_time);
        curr_song = findViewById(R.id.song_name);
        directory_select = findViewById(R.id.dir_select);
        rand = findViewById(R.id.rand_btn);
        prev = findViewById(R.id.previous_track_btn);
        start_stop = findViewById(R.id.start_pause_btn);
        next = findViewById(R.id.next_track_btn);
        volume = findViewById(R.id.volume_btn);
        progress_bar = findViewById(R.id.progress_bar);
    }

    private void setupListeners(){
        progress_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                myHandler.removeCallbacks(UpdateSongTime);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
                myHandler.postDelayed(UpdateSongTime,100);
            }
        });

        directory_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FolderPicker.class);
                startActivityForResult(intent, 9999);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!tracks.isEmpty()){
                    if(randomize_playing){
                        track_num = randomize.nextInt(tracks.size());
                    }else {
                        track_num++;
                        if(track_num==tracks.size()){
                            track_num=0;
                        }
                    }
                    startNewSong();
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp!=null) {
                    if (mp.getCurrentPosition() < 1000) {
                        if (!tracks.isEmpty()) {
                            if (randomize_playing) {
                                track_num = randomize.nextInt(tracks.size());
                            } else {
                                track_num--;
                                if (track_num < 0) {
                                    track_num = 0;
                                }
                            }
                            startNewSong();
                        }
                    } else {
                        mp.seekTo(0);
                    }
                }
            }
        });

        rand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(randomize_playing){
                    rand.setBackgroundResource(R.drawable.no_rand_btn);
                }else{
                    rand.setBackgroundResource(R.drawable.rand_btn);
                }
                randomize_playing=!randomize_playing;
            }
        });

        volume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (volume_off) {
                        mp.setVolume(1, 1);
                        volume.setBackgroundResource(R.drawable.volume_btn);
                    } else {
                        mp.setVolume(0, 0);
                        volume.setBackgroundResource(R.drawable.no_volume_btn);
                    }
                    volume_off = !volume_off;
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        start_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!mp.isPlaying()) {
                        startPlaying();
                    } else {
                        stopPlaying();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void startPlaying(){
        if (tracks.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.no_track, Toast.LENGTH_SHORT).show();
        } else {
            curr_song.setText("(" + (track_num + 1) + "/" + tracks.size() + ") " + tracks.get(track_num));
            if (!(mp.getCurrentPosition() > 0)) {
                startNewSong();
            } else {
                mp.start();
            }
            start_stop.setBackgroundResource(R.drawable.stop_btn);
        }
    }

    private void stopPlaying(){
        mp.pause();
        start_stop.setBackgroundResource(R.drawable.start_btn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        getUIElements();
        getPermissions();
        randomize = new Random();
        tracks = new LinkedList<>();
        mp = new MediaPlayer();
        dir_path.setText(getMusicDirectory());
        if(dir_path.length()>0){
            setupTracks(dir_path.getText().toString());
        }else {
            String defaultMusicDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music";
            dir_path.setText(defaultMusicDir);
            setupTracks(defaultMusicDir);
        }
        restorePlayerOptions();
        if(savedInstanceState != null){
            if(savedInstanceState.getBoolean("isPlaying")){
                start_stop.setBackgroundResource(R.drawable.stop_btn);
                startNewSong(start);
            }
        }
        setupListeners();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        savePlayerOptions();
        try {
            if (mp.isPlaying()) {
                outState.putBoolean("isPlaying", true);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }

    private void savePlayerOptions(){
        saveMusicDirectory();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("track_num", track_num);
        editor.putInt("start", start);
        editor.putBoolean("randomize_playing", randomize_playing);
        editor.putBoolean("volume_off", volume_off);
        editor.apply();
    }

    private void restorePlayerOptions(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        track_num = pref.getInt("track_num", 0);
        start = pref.getInt("start", 0);
        randomize_playing = pref.getBoolean("randomize_playing", false);
        volume_off = pref.getBoolean("volume_off", false);
        if(volume_off){
            volume.setBackgroundResource(R.drawable.no_volume_btn);
        }else{
            volume.setBackgroundResource(R.drawable.volume_btn);
        }
        if(randomize_playing){
            rand.setBackgroundResource(R.drawable.rand_btn);
        }else{
            rand.setBackgroundResource(R.drawable.no_rand_btn);
        }
    }

    private void setupTracks(String path){
        if(path.length() <= 0){
            return;
        }
        tracks.clear();
        start_stop.setBackgroundResource(R.drawable.start_btn);
        try {
            if (mp.isPlaying()) {
                myHandler.removeCallbacks(UpdateSongTime);
                mp.stop();
                mp.release();
                mp=null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            mp = new MediaPlayer();
            progress_bar.setProgress(0);
            end_time.setText("00:00:00");
            curr_time.setText("00:00:00");
        }
        File dir = new File(path);
        File[] files = dir.listFiles();
        if(files!=null) {
            for (File f : files) {
                if(f.getName().contains(".mp3")) {
                    tracks.add(f.getName());
                }
            }
        }
        if(!tracks.isEmpty()) {
            curr_song.setText("("+(track_num+1)+"/"+tracks.size()+") "+tracks.get(track_num));
        }else{
            curr_song.setText("");
        }
        track_num=0;
    }

    private void startNewSong(){
        try {
            mp.release();
            mp = new MediaPlayer();
            curr_song.setText("("+(track_num+1)+"/"+tracks.size()+") "+tracks.get(track_num));
            mp.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/" + tracks.get(track_num));
            mp.prepare();
            mp.start();
            end = mp.getDuration();
            end_time.setText(String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours((long) end),
                    TimeUnit.MILLISECONDS.toMinutes((long) end)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours((long) end)),
                    TimeUnit.MILLISECONDS.toSeconds((long) end)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) end))
                    )
            );
            progress_bar.setMax(end);
            myHandler.postDelayed(UpdateSongTime,100);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            start_stop.setBackgroundResource(R.drawable.start_btn);
        }
    }

    private void startNewSong(int start){
        try {
            mp.release();
            mp = new MediaPlayer();
            curr_song.setText("("+(track_num+1)+"/"+tracks.size()+") "+tracks.get(track_num));
            mp.setDataSource(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music/" + tracks.get(track_num));
            mp.prepare();
            mp.seekTo(start);
            mp.start();
            end = mp.getDuration();
            end_time.setText(String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours((long) end),
                    TimeUnit.MILLISECONDS.toMinutes((long) end)-TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours((long) end)),
                    TimeUnit.MILLISECONDS.toSeconds((long) end)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) end))
                    )
            );
            progress_bar.setMax(end);
            myHandler.postDelayed(UpdateSongTime,100);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            start_stop.setBackgroundResource(R.drawable.start_btn);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            myHandler.removeCallbacks(UpdateSongTime);
            mp.stop();
            mp.release();
            mp=null;
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            myHandler.removeCallbacks(UpdateSongTime);
            mp.stop();
            mp.release();
            mp=null;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == 9999)
            if(resultCode == Activity.RESULT_OK) {
                String data = intent.getExtras().getString("data");
                if(data == null) return;
                if(data != dir_path.getText()) {
                    dir_path.setText(data);
                    saveMusicDirectory();
                    setupTracks(data);
                }
            }
    }

    private void saveMusicDirectory(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("music_directory", dir_path.getText().toString());
        editor.apply();
    }

    private String getMusicDirectory(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        return pref.getString("music_directory", "");
    }

    private void getPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    666);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    666);
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            if(mp!=null) {
                if (!mp.isPlaying() && start > (end - 2000)) {
                    if (randomize_playing) {
                        track_num = randomize.nextInt(tracks.size());
                    } else {
                        track_num++;
                        if (track_num == tracks.size()) {
                            track_num = 0;
                        }
                    }
                    startNewSong();
                }
                start = mp.getCurrentPosition();
                curr_time.setText(String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours((long) start),
                        TimeUnit.MILLISECONDS.toMinutes((long) start) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours((long) start)),
                        TimeUnit.MILLISECONDS.toSeconds((long) start) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) start))
                        )
                );
                progress_bar.setProgress(start);
            }
            myHandler.postDelayed(this, 100);
        }
    };
}
