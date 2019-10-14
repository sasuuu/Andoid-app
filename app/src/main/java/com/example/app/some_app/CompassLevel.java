package com.example.app.some_app;

import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class CompassLevel extends AppCompatActivity implements SensorEventListener {

    private ImageView comp_img;
    private SeekBar seek_z,seek_y;
    private SensorManager sm;
    private float currentRotation_x = 0;
    private boolean hasAccelerometer;
    private boolean hasMagnetometer;
    private TextView rot_x,rot_y, rot_z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass_level);
        comp_img = findViewById(R.id.comp_img);
        rot_x = findViewById(R.id.rotation);
        rot_y = findViewById(R.id.orientation_y);
        rot_z = findViewById(R.id.orientation_z);
        seek_y = findViewById(R.id.seek_y);
        seek_z = findViewById(R.id.seek_z);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        seek_y.setMinimumWidth(height);
        seek_y.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        seek_z.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        checkSensors();
    }

    private void checkSensors(){
        PackageManager manager = getPackageManager();
        hasAccelerometer = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        hasMagnetometer = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
        if(hasAccelerometer && hasMagnetometer) {
            sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
            Toast.makeText(getApplicationContext(), R.string.have_sensors,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), R.string.dont_have_sensors,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(hasAccelerometer && hasMagnetometer) {
            sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME);
        }else{
            Toast.makeText(getApplicationContext(),R.string.dont_have_sensors,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(hasAccelerometer && hasMagnetometer) {
            sm.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float newRotation_x,newRotation_y,newRotation_z;
        newRotation_x=event.values[0];
        newRotation_y=event.values[1];
        newRotation_z=event.values[2];
        rot_x.setText(String.format("%.1f %s",newRotation_x, getString(R.string.degrees)));
        rot_y.setText(String.format("%s %.1f %s",getString(R.string.y_axis), newRotation_y, getString(R.string.degrees)));
        rot_z.setText(String.format("%s %.1f %s",getString(R.string.z_axis), newRotation_z, getString(R.string.degrees)));
        Animation anim = new RotateAnimation(currentRotation_x,-Math.round(newRotation_x),Animation.RELATIVE_TO_SELF,(float)0.5,Animation.RELATIVE_TO_SELF,(float)0.5);
        anim.setDuration(200);
        anim.setFillAfter(true);
        comp_img.startAnimation(anim);
        seek_z.setProgress((int) -newRotation_z+90);
        seek_y.setProgress((int) newRotation_y+90);
        currentRotation_x =-Math.round(newRotation_x);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
