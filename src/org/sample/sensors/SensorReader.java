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
    	// create own context to access sensor service
    	mContext = context; 
    	// access sensors
        mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        // access only acceleration sensor
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
    
    /**
     * Dtor
     */
    protected void finalize() {	
    	stop();
    }

    /**
     * start event listener for acceleration sensor
     */
	public void start() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * stop event listener for acceleration sensor
     */
    public void stop() {
        mSensorManager.unregisterListener(this);
    }
    
    /**
     * handle sensor events
     */
    public void onSensorChanged(SensorEvent event) {
    	
    	// only handle acceleration events
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
            return;
        }

        // calculate scalar 
        float scalar = event.values[0]*event.values[0] + // x
        			   event.values[1]*event.values[1] + // y
        			   event.values[2]*event.values[2]; // z
        
        mSensorValues.add(Float.valueOf(scalar));
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO
	}
	
	/**
	 *	get collected sensor scalar data 
	 */
	public ArrayList<Float> sensorValues() {
		return mSensorValues;
	}

	/**
	 *	clear stored sensor data
	 */
	public void clear() {
		mSensorValues.clear();
	}
};
