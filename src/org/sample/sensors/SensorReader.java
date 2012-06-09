package org.sample.sensors;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.util.Log;

/**
 * Class to read acceleration sensors
 * @author sgibb
 *
 */
public class SensorReader implements SensorEventListener {
	/**
	 * Class to store sensor data
	 * @author sgibb 
	 *
	 */
	public class SensorData {
		public long timestamp;
		public float value;
	};
    
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private ArrayList<SensorData> mSensorValues;
    private Context mContext;
    
    // TO DEBUG
    private File mLogFile;
    private FileWriter mLogStream; 

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
        // create mSensorValues array
        mSensorValues = new ArrayList<SensorData>();
        
        // TO DEBUG
        File root = Environment.getExternalStorageDirectory();
        mLogFile = new File(root, "sensordata.csv");
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
        
        SensorData entry = new SensorData();
        entry.timestamp = event.timestamp;
        entry.value = scalar;
        
        mSensorValues.add(entry);
        
        // DEBUG ONLY
        Log.i("SensorReader", entry.timestamp + ":" + Float.valueOf(entry.value).toString());
        try {
        	mLogStream = new FileWriter(mLogFile, true);
            mLogStream.write(entry.timestamp + "," + Float.valueOf(entry.value).toString() + "\n");
            mLogStream.close();
        } catch(IOException e) {
            Log.e("SensorReader", "Could not write file " + e.getMessage()); 
        }
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO
	}
	
	/**
	 *	get collected sensor scalar data 
	 */
	public ArrayList<SensorData> sensorValues() {
		return mSensorValues;
	}

	/**
	 *	clear stored sensor data
	 */
	public void clear() {
		mSensorValues.clear();
	}
};
