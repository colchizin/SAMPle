package org.sample;

import org.sample.musicfiles.MP3File;
import org.sample.musicfiles.MusicFile;
import org.sample.musicplayer.MusicPlayer;
import org.sample.musicplayer.MusicService;
import org.sample.musicplayer.NativePlayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class WalkActivity extends Activity implements OnClickListener {

	private MusicPlayer music;
	private ImageButton skipButton;
	private ImageButton pauseButton;
	
	private TextView artist_text;
	private TextView title_text;
	private TextView bpm_text;
	private TextView spm_text;

	private boolean isPlaying;
	
	MusicService mService;
	private boolean mIsBound;
	ServiceConnection mConnection;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		skipButton = (ImageButton) findViewById(R.id.button_skip);
		skipButton.setOnClickListener(this);
		pauseButton = (ImageButton) findViewById(R.id.button_pause);
		pauseButton.setOnClickListener(this);
		
		artist_text = (TextView) findViewById(R.id.text_artist);
		title_text = (TextView) findViewById(R.id.text_title);
		bpm_text = (TextView) findViewById(R.id.text_bpm);
		spm_text = (TextView) findViewById(R.id.text_spm);
		
		music = new NativePlayer(this);

		MP3File song = new MP3File("/sdcard/Kalimba.mp3");
		song.title="Der Titel";
		song.artist="der k�nstler";
		song.setBPM(235);
		
		setInfo(song,0);
		//music.play(song);
		Intent intent = new Intent(MusicService.ACTION_PLAY);
		startService(intent);
		isPlaying = true;
		doBindService();
	}

	private void setInfo(MusicFile song, int spm) {
		artist_text.setText(song.artist);
		title_text.setText(song.title);
		bpm_text.setText(String.valueOf(song.getBPM()));
		spm_text.setText(String.valueOf(spm));		
	}

	public void onClick(View v) {
		if (v.getId() == R.id.button_skip) {
			// Select another bpm-fitting song
			MP3File nextSong = new MP3File("/sdcard/SleepAway.mp3");
			// music.fadeIn(6000, nextSong);

		} else if (v.getId() == R.id.button_pause) {
			
			if(isPlaying){
				Intent intent = new Intent(MusicService.ACTION_PAUSE);
				startService(intent);
				pauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
				isPlaying=false;
			}else{
				Intent intent = new Intent(MusicService.ACTION_PLAY);
				startService(intent);
				pauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
				isPlaying=true;
			}
		}

	}
	
	void doBindService() {
	    // Establish a connection with the service.  We use an explicit
	    // class name because there is no reason to be able to let other
	    // applications replace our component.
		
		mConnection = new ServiceConnection() {
			public void onServiceConnected(ComponentName arg0, IBinder service) {
				mService = ((MusicService.MusicServiceBinder)(service)).getService();
				MusicFile currentFile = mService.getCurrentFile();
				setInfo(currentFile, mService.getCurrentBPM());
				
				Log.i("WalkActivity", "Info set");
			}

			public void onServiceDisconnected(ComponentName arg0) {
				Log.i("WalkActivity", "Service disconnected");
				mService = null;
			}
		};
		
	    this.bindService(
	    		new Intent(MusicService.ACTION_PREPARE),
	    		mConnection, Context.BIND_AUTO_CREATE
	    );
	    mIsBound = true;
	    Log.i("WalkActivity", "Service boud");
	}

}
