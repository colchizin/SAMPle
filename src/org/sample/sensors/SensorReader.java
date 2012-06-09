package org.sample.sensors;

import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Klasse zum Auslesen des Beschleunigungssensors
 * @author sgibb 
 *
 */
public class SensorReader implements SensorEventListener {
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private ArrayList<Float> mSensorValues;
    private Context mContext;

    /**
     * Ctor
     */
    public SensorReader(Context context) {
    	mContext = context; 
        mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
    
    /**
     * Dtor
     */
    protected void finalize() {	
    	stop();
    }

    /**
     * start event listener for for acceleration sensor
     */
	public void start() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        mSensorManager.unregisterListener(this);
    }
    

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
            return;

        float scalar = event.values[0]*event.values[0] + // x
        			   event.values[1]*event.values[1] + // y
        			   event.values[2]*event.values[2]; // z
        mSensorValues.add(Float.valueOf(scalar));
	}
	
    
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO
	}
	
	public ArrayList<Float> sensorValues() {
		return mSensorValues;
	}
	

	public void clear() {
		mSensorValues.clear();
	}
};
