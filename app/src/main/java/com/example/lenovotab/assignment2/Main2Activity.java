package com.example.lenovotab.assignment2;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class Main2Activity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sm;
    private Sensor sensor;
    private PlotView plot;
    private AnimationDrawable animationDraw;
    private long startTime;
    private boolean first;
    private String sensorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        sm = (SensorManager) getSystemService (Context.SENSOR_SERVICE);
        plot = (PlotView) findViewById(R.id.plot);
        ImageView anim = (ImageView)  findViewById(R.id.animation);

        Intent intent = getIntent();
        sensorName = intent.getStringExtra("sensor");
        if (sensorName.equals("Accelerator")){
            sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (sensor !=null) {
                sm.registerListener(this, sensor, 100000);
                plot.setSensorName(sensorName);
                anim.setBackgroundResource(R.drawable.anime);
            }
        }
        else if (sensorName.equals("Light")){
            sensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
            if (sensor !=null) {
                sm.registerListener(this, sensor, 100000);
                plot.setSensorName(sensorName);
                anim.setBackgroundResource(R.drawable.unicorn);
            }
        }

        animationDraw = (AnimationDrawable) anim.getBackground();
        first = true;
    }

    public void back(View view){
        plot.clearList();
        onBackPressed();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (first) {
            startTime = System.nanoTime();
            first = false;
        }
        long endTime = System.nanoTime();

        if (((endTime-startTime)/1000 > 100) || first) {
            float num;
            if (sensorName.equals("Accelerator")) {
                num = (float) Math.sqrt(event.values[0] * event.values[0] +
                        event.values[1] * event.values[1] +
                        event.values[2] * event.values[2]);
            }else{
                    num = event.values[0];
            }
            plot.addPoint(num);
            startTime = endTime;
            if (plot.run()) {
                animationDraw.start();
            } else {
                animationDraw.stop();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause(){
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    protected  void onResume(){
        super.onResume();
        if (sensor !=null)
            sm.registerListener(this, sensor, 100000);
    }
}
