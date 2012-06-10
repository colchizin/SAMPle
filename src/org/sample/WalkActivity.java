package org.sample;

import org.sample.musicfiles.MP3File;
import org.sample.musicplayer.MusicPlayer;
import org.sample.musicplayer.NativePlayer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class WalkActivity extends Activity implements OnClickListener {

	private MusicPlayer music;
	private ImageButton skipButton;

	private ImageButton pauseButton;

	private boolean isPlaying;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		skipButton = (ImageButton) findViewById(R.id.button_skip);
		skipButton.setOnClickListener(this);
		pauseButton = (ImageButton) findViewById(R.id.button_pause);
		pauseButton.setOnClickListener(this);
		music = new NativePlayer(this);

		MP3File song = new MP3File("/sdcard/Kalimba.mp3");
		music.play(song);
		isPlaying = true;

	}

	public void onClick(View v) {
		if (v.getId() == R.id.button_skip) {
			// Select another bpm-fitting song
			MP3File nextSong = new MP3File("/sdcard/SleepAway.mp3");
			music.fadeIn(6000, nextSong);

		} else if (v.getId() == R.id.button_pause) {
			if(isPlaying){
				music.pause();
				pauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
				isPlaying=false;
			}else{
				music.resume();
				pauseButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
				isPlaying=true;
			}
		}

	}

}
