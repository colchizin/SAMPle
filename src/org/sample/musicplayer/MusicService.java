package org.sample.musicplayer;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.sample.musicfiles.FindMusicFilesTask;
import org.sample.musicfiles.FindMusicFilesTask.MusicFilesFoundListener;
import org.sample.musicfiles.MusicFile;
import org.sample.musicfiles.MusicFileDBHelper;
import org.sample.musicfiles.MusicFileDatasource;
import org.sample.sensors.SensorReader;
import org.sample.sensors.StepChangeListener;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

public class MusicService extends Service implements
		OnCompletionListener, OnErrorListener,
		MediaPlayer.OnPreparedListener,
		MusicFilesFoundListener,
		StepChangeListener {
	final static String TAG = "SAMPleMusicService";
	
	public static final String ACTION_PLAY = "com.sample.musicplayer.action.PLAY";
	public static final String ACTION_TOGGLE_PLAYBACK = "com.sample.musicplayer.action.TOGGLE_PLAYBACK";
    public static final String ACTION_PAUSE = "com.sample.musicplayer.action.PAUSE";
    public static final String ACTION_STOP = "com.sample.musicplayer.action.STOP";
    public static final String ACTION_SKIP = "com.sample.musicplayer.action.SKIP";
    public static final String ACTION_REWIND = "com.sample.musicplayer.action.REWIND";
    public static final String ACTION_URL = "com.sample.musicplayer.action.URL";
    
    MediaPlayer mMediaPlayer = null;
    NotificationManager mNotificationManager = null;
    AudioManager mAudioManager = null;
    
    MusicFile mNextFile = null;
    SensorReader mSensorReader;
    
    // The component name of MusicIntentReceiver, for use with media button and remote control
    // APIs
    ComponentName mMediaButtonReceiverComponent;
    
    MusicFileDatasource datasource;
    
    enum State {
        Waiting,	// the MediaRetriever is retrieving music
        Stopped,    // media player is stopped and not prepared to play
        Preparing,  // media player is preparing...
        Playing,    // playback active (media player ready!). (but the media player may actually be
                    // paused in this state if we don't have audio focus. But we stay in this state
                    // so that we know we have to resume playback once we get focus back)
        Paused      // playback paused (media player ready!)
    };
    
    void createMediaPlayerIfNeeded() {
        if (mMediaPlayer == null) {
        	mMediaPlayer = new MediaPlayer();

            // Make sure the media player will acquire a wake-lock while playing. If we don't do
            // that, the CPU might go to sleep while the song is playing, causing playback to stop.
            //
            // Remember that to use this, we have to declare the android.permission.WAKE_LOCK
            // permission in AndroidManifest.xml.
        	mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

            // we want the media player to notify us when it's ready preparing, and when it's done
            // playing:
        	mMediaPlayer.setOnPreparedListener(this);
        	mMediaPlayer.setOnCompletionListener(this);
        	mMediaPlayer.setOnErrorListener(this);
        }
        else
        	mMediaPlayer.reset();
    }
    
    public int onStartCommand(Intent intent, int flags, int startid) { 
    	Log.i(TAG, "debug: startup service");
    	return START_STICKY;
    }
    
    public void onCreate() {
        Log.i(TAG, "debug: Creating service");

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mSensorReader = new SensorReader(this,this);
        datasource = new MusicFileDatasource(this);
        mSensorReader.start();
        
        /*(new FindMusicFilesTask(
				this.datasource,
				null,
				this
		)).execute();	*/
        
        // Create the retriever and start an asynchronous task that will prepare it.
        // TODO:
        
        //mMediaButtonReceiverComponent = new ComponentName(this, MusicIntentReceiver.class);
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void processPlayRequest() {
		
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMusicFilesFound(List<MusicFile> files) {
		Random random = new Random();
		int size = files.size();
		if (size > 0) {
			int rand = random.nextInt(files.size());
			mNextFile = files.get(rand);
			
			createMediaPlayerIfNeeded();
			
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			
			try {
				mMediaPlayer.setDataSource(mNextFile.getFilename());
				mMediaPlayer.prepareAsync();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onStepChanged(int bpm) {
		Log.d(TAG, "Step Changed: " + bpm);
		(new FindMusicFilesTask(
				this.datasource,
				MusicFileDBHelper.getBPMCondition(bpm, 10),
				this
		)).execute();
	}

    public void onDestroy() {   
        Log.d(TAG, "debug: service destroyed");
    }
}
