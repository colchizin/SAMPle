package org.sample.musicplayer;

import java.io.IOException;

import org.sample.musicfiles.MusicFile;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

public class MusicTimer extends CountDownTimer {

	private int curVolume;
	private AudioManager audio;
	private MediaPlayer music;
	private MusicFile nextSong;
	private boolean up;
	private long millisInFuture;
	private int startVolume;

	public MusicTimer(long millisInFuture, long countDownInterval,
			int startVolume, AudioManager audio, MediaPlayer music,
			MusicFile nextSong) {
		super(millisInFuture, countDownInterval);
		this.curVolume = startVolume;
		this.audio = audio;
		this.music = music;
		this.nextSong = nextSong;
		up = false;
		this.millisInFuture=millisInFuture;
		this.startVolume=startVolume;

	}

	@Override
	public void onFinish() {
		audio.setStreamVolume(AudioManager.STREAM_MUSIC, startVolume, 0);

	}

	@Override
	public void onTick(long millisUntilFinished) {
		Log.d("SAMPleActivity", "curVolume: "+curVolume);
		Log.d("SAMPleActivity", "millisUntilFinished: "+millisUntilFinished);
		if (up) {			
			curVolume++;
			audio.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, 0);
		} else {
			if (curVolume <= 0 || (millisUntilFinished <= (millisInFuture/3*2))) {
				Log.d("SAMPleActivity", "Change Song!");
				up = true;
				changeSong();
			} else {
				curVolume--;
				audio.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, 0);
			}

		}

	}

	private void changeSong() {		
		try {
			music.stop();
			MediaPlayer music2 = new MediaPlayer();
			music2.setDataSource(nextSong.getFilename());
			music2.prepare();
			music2.start();
			
		} catch (IllegalStateException e) {			
			e.printStackTrace();
		} catch (IOException e) {		
			e.printStackTrace();
		}
	}
}
