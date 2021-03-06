package org.sample.sensors;

import org.sample.sensors.StepChangeListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

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
		public boolean sign;
		public long timestamp;
		public float value;
	};
    
    private final SensorManager mSensorManager;
    private final Sensor mAccelerometer;
    private final StepChangeListener mStepChangeListener;
    private LinkedList<SensorData> mSensorValues;
    private Context mContext;
    
    private float mExponentialMovingAverage;
    private float movingAverageOpt;
    private final float mAlpha = (float) 0.2;
    private final int mWindowSizeMovingAverage = 100;
    private final double mTimeInterval = 10; // in seconds
    private final float mGravityThreshold = 110; // g = 9.8 ; g^2 ca. 100 
    private final double mStepSendInterval = 2; // in seconds
    
    private long mLastChangedSend;
    private int mNumberOfChangedSigns;
    
    // TO DEBUG
    //private File mLogFile;
    //private FileWriter mLogStream; 

    /**
     * Ctor
     */
    public SensorReader(Context context, StepChangeListener listener) {
    	// create own context to access sensor service
    	mContext = context; 
    	// access sensors
        mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
        // access only acceleration sensor
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        // create mSensorValues array
        mSensorValues = new LinkedList<SensorData>();
        // listener
        mStepChangeListener = listener;
        
        // initialization for exponential average
        mExponentialMovingAverage = 100; // g = 9.81 => g^2 ca. 100
        
        movingAverageOpt = 0;
        
        // timestamp for last StepChanged::onStepChanged
        mLastChangedSend = 0;
        
        // store number of sign changes
        mNumberOfChangedSigns = 0;
        
        // DEBUG
        //File root = Environment.getExternalStorageDirectory();
        //mLogFile = new File(root, "sensordata.csv");
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

        // calculate scalar norm of acelleration vector
        float scalar = event.values[0]*event.values[0] + // x
        			   event.values[1]*event.values[1] + // y
        			   event.values[2]*event.values[2]; // z
        
        //Log.i("mean", String.valueOf(scalar));
        
        SensorData entry = new SensorData();
        entry.timestamp = event.timestamp;
        entry.value = scalar;
        
        mSensorValues.add(entry);
        
        // const window size for moving average 
        movingAverageOpt = movingMeanOpt(movingAverageOpt);
        //float movingAverage = movingMean();
        
        //checkSignChange(movingAverage);
        checkSignChange(movingAverageOpt);
        
        countSteps();
        // DEBUG ONLY
        //int steps = countSteps();
        //Log.i("SensorReader", entry.timestamp + ":" + String.valueOf(entry.value) + ", steps: "
        //	+ String.valueOf(steps) +":" + String.valueOf(movingAverageOpt));
        // + ":" + String.valueOf(movingAverage));
        
        /*
        try {
        	mLogStream = new FileWriter(mLogFile, true);
            mLogStream.write(entry.timestamp + "," + String.valueOf(entry.value) + ","
            		+ String.valueOf(mExponentialMovingAverage) + ","
            		+ String.valueOf(movingAverage) + ","
            		+ String.valueOf(steps) + "\n");
            mLogStream.close();
        } catch(IOException e) {
            Log.e("SensorReader", "Could not write file " + e.getMessage()); 
        }*/
	}
	
    /**
     * only needed to fit interface implementation needs
     */
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// not needed
	}
	
	/**
	 *	clear stored sensor data
	 */
	public void clear() {
		mSensorValues.clear();
	}
	
	/**
	 * calculate movingMean of the previous m values
	 */
	private float movingMean() {
		Float sum = (float)0;
		int n = mSensorValues.size();
		int start = n > mWindowSizeMovingAverage ? n-mWindowSizeMovingAverage : 0; 
		for (int i=start; i < n; ++i) {
		   sum += mSensorValues.get(i).value;
		}
		return ((float)(sum/(n-start)));
	}
	
	/**
	 * compute the moving mean without loops by updating
	 */
	private float movingMeanOpt(float curMovingMean){
		int n = mSensorValues.size();
		int start = n > mWindowSizeMovingAverage ? n-mWindowSizeMovingAverage : 0;
		if (start > 0){
			curMovingMean = curMovingMean - mSensorValues.get(start-1).value/mWindowSizeMovingAverage;
			return ((float)(curMovingMean+mSensorValues.get(n-1).value/mWindowSizeMovingAverage));
		}
		else{
			return ((float)((curMovingMean*(n-1)+mSensorValues.get(n-1).value)/n));
		}
		}
	
	/**
	 * calculate exponential weighted moving average
	 */
	private float ewma(float scalar) {
        mExponentialMovingAverage = mAlpha * scalar + (1-mAlpha) * mExponentialMovingAverage;
        return mExponentialMovingAverage;
	}
	
	/**
	 * check sign change
	 */
	private void checkSignChange(float movingAverage) {
		int n = mSensorValues.size();
		float pre = mExponentialMovingAverage - movingAverage;
		float post = ewma(mSensorValues.get(n-1).value) - movingAverage;
		
		boolean isSignChanged = Math.abs(( Math.abs(pre+post) - Math.abs(pre) - Math.abs(post) )) > 0.001 &&
				// zero if pre and post have the same sign
				movingAverage > mGravityThreshold;
				
		mSensorValues.get(n-1).sign = isSignChanged;
		
		if (isSignChanged) {
			mNumberOfChangedSigns+=1;
		}
	}
	
	/**
	 * count steps
	 */
	private int countSteps() {
		int steps=0;
		int n = mSensorValues.size();
		double deltaTime = mSensorValues.get(n-1).timestamp - mSensorValues.get(0).timestamp;
		deltaTime /= 1000000000; // nanosec -> sec
		
		//Log.i("deltaTime", String.valueOf(deltaTime));
		//Log.i("n", String.valueOf(n));
		
		if (deltaTime > mTimeInterval) {
			//Log.i("> mTimeInterval", String.valueOf(deltaTime));
			/*for (int i=0; i<n; ++i) {
				if (mSensorValues.get(i).sign) {
					steps+=1;
				}
			}*/
			steps = mNumberOfChangedSigns/2; // 2 Null-Durchgänge je Schritt
			steps *= (int)Math.ceil(60/mTimeInterval);
			
			// don't send every change
			long currentTimeStamp = System.currentTimeMillis();
			if ( ((currentTimeStamp - mLastChangedSend) / 1000 ) > mStepSendInterval) {
				mLastChangedSend = currentTimeStamp;
				mStepChangeListener.onStepChanged(steps);
				//Log.i("StepChangeListener", "onChanged");
			}
			
			if (mSensorValues.get(0).sign) {
				mNumberOfChangedSigns-=1;
			}
			
			mSensorValues.poll();
		}
		return steps;
	}
};
