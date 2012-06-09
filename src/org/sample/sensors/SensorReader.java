package org.sample.sensors;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
    
    private float mExponentialMovingAverage;
    private final float mAlpha = (float) 0.2;
    private final float mMaxlistSize = 100;
    
    private LinkedList<Float> mDataList;
    
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
        
        // alpha for EWMA
        mExponentialMovingAverage = 100; // g = 9.81 => g^2 ca. 100
        // data list
        mDataList = new LinkedList<Float>();
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
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
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
        
        // EWMA
        mExponentialMovingAverage = mAlpha * scalar + (1-mAlpha) * mExponentialMovingAverage;
        
        // moving average for last n values
        mDataList.add(scalar);
        
        Log.i("mean", String.valueOf(scalar));
        
        // const window size for moving average 
        while (mDataList.size() > mMaxlistSize) {
        	mDataList.poll();
        }
        
        float movingAverage = mean(mDataList);
        
        SensorData entry = new SensorData();
        entry.timestamp = event.timestamp;
        entry.value = scalar;
        
        mSensorValues.add(entry);
        
        // DEBUG ONLY
        Log.i("SensorReader", entry.timestamp + ":" + String.valueOf(entry.value) 
        		+ "," + String.valueOf(mExponentialMovingAverage)
        		+ "," + String.valueOf(movingAverage));
        try {
        	mLogStream = new FileWriter(mLogFile, true);
            mLogStream.write(entry.timestamp + "," + String.valueOf(entry.value)
        		+ "," + String.valueOf(mExponentialMovingAverage)
        		+ "," + String.valueOf(movingAverage) + "\n");
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
	
	/**
	 * calculate mean
	 */
	private float mean(LinkedList<Float> list) {
		Iterator<Float> i = list.iterator();
		Float sum = (float)0;
		while (i.hasNext()) {
		   sum += i.next();
		}
		Log.i("mean", String.valueOf(sum) + ":" + String.valueOf(list.size()));
		return ((float)(sum/(list.size()+1)));
	}
};
