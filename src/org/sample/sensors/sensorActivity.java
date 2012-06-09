package org.sample.sensors;

import android.R;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class sensorActivity extends Activity {
	private SensorReader mSensorReader;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //setContentView(R.layout.main);
        mSensorReader.start();
        TextView textView = (TextView)findViewById(R.id.textView);
        
    }
}
