package org.sample.musicplayer;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.sample.musicfiles.FindMusicFilesTask;
import org.sample.musicfiles.FindMusicFilesTask.MusicFilesFoundListener;
import org.sample.musicfiles.MusicFile;
import org.sample.musicfiles.MusicFileDatasource;
import org.sample.sensors.SensorReader;
import org.sample.sensors.StepChangeListener;

import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

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
    
    public static final int notificationID = 1;
    
    int lastBPM;
    long lastChangeTimestamp;
    boolean mLocked = false;
    
    final int songSwitchThreshold = 30000;
    final float stepThreshold = 0.1f;
    
    // The component name of MusicIntentReceiver, for use with media button and remote control
    // APIs
    ComponentName mMediaButtonReceiverComponent;
    
    MusicFileDatasource datasource;
    
    enum State {
        Retrieving,	// the MediaRetriever is retrieving music
        Stopped,    // media player is stopped and not prepared to play
        Preparing,  // media player is preparing...
        Playing,    // playback active (media player ready!). (but the media player may actually be
                    // paused in this state if we don't have audio focus. But we stay in this state
                    // so that we know we have to resume playback once we get focus back)
        Paused      // playback paused (media player ready!)
    };
    
    protected State mState = State.Stopped;
    protected boolean mStartPlayingAfterRetrieve;
    protected String mWhatToPlayAfterRetrieve;
    
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
    
    
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.i(TAG, "debug: start command service");
    	
        String action = intent.getAction();
        if (action.equals(ACTION_TOGGLE_PLAYBACK)) processTogglePlaybackRequest();
        else if (action.equals(ACTION_PLAY)) processPlayRequest();
        else if (action.equals(ACTION_PAUSE)) processPauseRequest();
        else if (action.equals(ACTION_SKIP)) processSkipRequest();
        else if (action.equals(ACTION_STOP)) processStopRequest();
        else if (action.equals(ACTION_REWIND)) processRewindRequest();

        return START_NOT_STICKY; // Means we started the service, but don't want it to
                                 // restart in case it's killed.
    }
    
    public void onCreate() {
        Log.i(TAG, "debug: Creating service");
        
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mSensorReader = new SensorReader(this,this);
        datasource = new MusicFileDatasource(this);
        mSensorReader.start();
        //mMediaButtonReceiverComponent = new ComponentName(this, MusicIntentReceiver.class);
    }
    

    public void onDestroy() {   
        Log.d(TAG, "debug: service destroyed");
    }
    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
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
	
	void processTogglePlaybackRequest() {
        if (mState == State.Paused || mState == State.Stopped) {
            processPlayRequest();
        } else {
            processPauseRequest();
        }
    }
	
	void processPlayRequest() {
        if (mState == State.Retrieving) {
            // If we are still retrieving media, just set the flag to start playing when we're
            // ready
            mWhatToPlayAfterRetrieve = null; // play a random song
            mStartPlayingAfterRetrieve = true;
            return;
        }


        // actually play the song

        if (mState == State.Stopped) {
            // If we're stopped, just go ahead to the next song and start playing
            playNextSong(null);
        }
        else if (mState == State.Paused) {
            // If we're paused, just continue playback and restore the 'foreground service' state.
            mState = State.Playing;
            //setUpAsForeground(mSongTitle + " (playing)");
            //configAndStartMediaPlayer(); // TODO
        }
    }
	
	void processPauseRequest() {
        if (mState == State.Retrieving) {
            // If we are still retrieving media, clear the flag that indicates we should start
            // playing when we're ready
            mStartPlayingAfterRetrieve = false;
            return;
        }

        if (mState == State.Playing) {
            // Pause media player and cancel the 'foreground service' state.
            mState = State.Paused;
            mMediaPlayer.pause();
            //relaxResources(false); // while paused, we always retain the MediaPlayer
            // do not give up audio focus
        }

        // Tell any remote controls that our playback state is 'paused'.
        //if (mRemoteControlClientCompat != null) {
        //    mRemoteControlClientCompat
        //            .setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
        //}
    }
	
	void processRewindRequest() {
        if (mState == State.Playing || mState == State.Paused)
            mMediaPlayer.seekTo(0);
    }

    void processSkipRequest() {
        if (mState == State.Playing || mState == State.Paused) {
            // tryToGetAudioFocus();
            playNextSong(null);
        }
    }
	
	void processStopRequest() {
        processStopRequest(false);
    }
	
	void processStopRequest(boolean force) {
        if (mState == State.Playing || mState == State.Paused || force) {
            mState = State.Stopped;

            // let go of all resources...
            //relaxResources(true);
            //giveUpAudioFocus();

            // Tell any remote controls that our playback state is 'paused'.
            //if (mRemoteControlClientCompat != null) {
            //    mRemoteControlClientCompat
            //            .setPlaybackState(RemoteControlClient.PLAYSTATE_STOPPED);
            //}

            // service is no longer necessary. Will be started again if needed.
            stopSelf();
        }
    }
	
	public void playNextSong(String filename) {
		
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		playNextSong(null);	
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
				mLocked = false;
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
		if (mLocked)
			return;
		
		mLocked = true;
		int lowerThreshold = Math.round(this.lastBPM*(1-this.stepThreshold));
		int upperThreshold = Math.round(this.lastBPM*(1+this.stepThreshold));
		long now = System.currentTimeMillis();
		
		if ((now-this.lastChangeTimestamp) < this.songSwitchThreshold) {
			Log.d(TAG, "Too little Time has passed. " + Math.round((now-this.lastChangeTimestamp)/1000) + "s");
			mLocked = false;
			return;
		}
			
		if (bpm <= upperThreshold && bpm >= lowerThreshold) {
			Log.d(TAG, "Step not sufficiently changed. " + bpm + " " + upperThreshold + " " + lowerThreshold);
			mLocked = false;
			return;
		}
			
		Log.d(TAG, "Step Changed: " + bpm);
		this.lastBPM = bpm;
		this.lastChangeTimestamp = now;
		
		(new FindMusicFilesTask(
				this.datasource,
				null, //MusicFileDBHelper.getBPMCondition(bpm, 10),
				this
		)).execute();
	}
}
