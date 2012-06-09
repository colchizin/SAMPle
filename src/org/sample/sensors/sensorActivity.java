package org.sample.sensors;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class sensorActivity extends Activity implements StepChangeListener {
	private SensorReader mSensorReader;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mSensorReader = new SensorReader(this, this);
        mSensorReader.start();
    }
    
    public void onStepChanged(int bpm) {
    	Log.i("sensorActivity", String.valueOf(bpm));
    }
}
