package org.sample.sensors;

import org.sample.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class sensorActivity extends Activity implements StepChangeListener {
	private SensorReader mSensorReader;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.sensoractivitylayout);
        
        mSensorReader = new SensorReader(this, this);
        mSensorReader.start();
    }
    
    public void onStepChanged(int bpm) {
    	Log.i("sensorActivity", String.valueOf(bpm));
    	TextView view = (TextView)findViewById(R.id.sensorTextView);
    	view.setText(String.valueOf(bpm) + " BPM");
    }
}
