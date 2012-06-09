package org.sample.sensors;

import android.app.Activity;
import android.os.Bundle;

public class sensorActivity extends Activity {
	private SensorReader mSensorReader;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mSensorReader = new SensorReader(this);
        mSensorReader.start();
    }
}
