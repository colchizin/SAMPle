package org.sample.musicplayer;

import java.io.IOException;

import org.sample.musicfiles.MusicFile;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

public class NativePlayer implements MusicPlayer {
	
	private MediaPlayer player;
	private AudioManager audio;


	public NativePlayer(Context mContext) {		
		audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}

	public void play(MusicFile song) {
		player = new MediaPlayer();
		initPlayer(song.getFilename());
		player.start();
	}

	private void initPlayer(String filename) {
		try {
			player.setDataSource(filename);
			player.prepare();
		} catch (IllegalArgumentException e) {			
			e.printStackTrace();
		} catch (IllegalStateException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}

	public void fadeIn(long fadeTimeMs, MusicFile nextSong) {

		int startVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

		long fadeIntervall = (fadeTimeMs / (startVolume*2));
		Log.d("SAMPleActivity", "fadeTimeMs: " + fadeTimeMs);
		Log.d("SAMPleActivity", "startVolume: " + startVolume);
		Log.d("SAMPleActivity", "fadeIntervall: " + fadeIntervall);

		MusicTimer fOut = new MusicTimer(fadeTimeMs, fadeIntervall,
				startVolume, audio, player, nextSong);

		fOut.start();
	}

	public void pause() {
		player.pause();
	}

	public void stop() {
		player.stop();
	}
	
	public void resume(){
		player.start();
	}

	public void increaseVolume() {
		int curVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
		int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		if (curVolume < maxVolume) {
			audio.setStreamVolume(AudioManager.STREAM_MUSIC, (curVolume+1), 0);
		}
	}
	
	public void decreaseVolume() {
		int curVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);		
		if (curVolume > 0) {
			audio.setStreamVolume(AudioManager.STREAM_MUSIC, (curVolume-1), 0);
		}
	}

}