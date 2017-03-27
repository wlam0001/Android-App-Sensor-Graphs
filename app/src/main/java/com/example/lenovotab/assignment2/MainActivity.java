package com.example.lenovotab.assignment2;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity{
    private SensorManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button accelButton = (Button) findViewById(R.id.accelerometer);
        Button lightButton = (Button) findViewById(R.id.light);

        sm = (SensorManager) getSystemService (Context.SENSOR_SERVICE);
        Sensor accelSensor = null;
        Sensor lightSensor = null;

        String accelText = "Accelerator\n";
        String lightText = "Light\n" ;

        if (sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) !=null) {
            accelSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            accelText += "Status: Available\n";
            accelText += "Range: " + accelSensor.getMaximumRange() + "m/s^2" + " | " +
                    "Resolution: " + accelSensor.getResolution() + "m/s^2" + " | " +
                    "Version: " + accelSensor.getVersion();

        }
        else{
            accelButton.setEnabled(false);
            accelText += "Status: Not Available\n";
        }


        if (sm.getDefaultSensor(Sensor.TYPE_LIGHT) !=null) {
            lightSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
            lightText += "Status: Available\n";
            lightText += "Range: " + lightSensor.getMaximumRange() + "lx" + " | " +
                    "Resolution: " + lightSensor.getResolution() + "lx" + " | " +
                    "Version: " + lightSensor.getVersion();
        }
        else{
            lightButton.setEnabled(false);
            lightText += "Status: Not Available\n";
        }

        accelButton.setText(accelText);
        lightButton.setText(lightText);
    }

    public void accel(View view){
        Intent intent = new Intent(this, Main2Activity.class);
        intent.putExtra("sensor", "Accelerator");
        startActivity(intent);
    }
    public void light(View view){
        Intent intent = new Intent(this, Main2Activity.class);
        intent.putExtra("sensor", "Light");
        startActivity(intent);
    }

}
