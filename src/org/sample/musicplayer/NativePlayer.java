package org.sample.musicplayer;

import java.io.IOException;

import org.sample.musicfiles.MusicFile;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.util.Log;

public class NativePlayer implements MusicPlayer {

	private Context mContext;
	private MediaPlayer mp;
	private AudioManager am;
	private AudioTrack at;

	public NativePlayer(Context mContext) {
		this.mContext = mContext;
		mp = new MediaPlayer();
		am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}

	public void play(MusicFile song) {
		initPlayer(song.getFilename());			
		mp.start();		
	}
	
	private void initPlayer(String filename){
		try {
			mp.setDataSource(filename);
			mp.prepare();
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

	public void fadeIn(long fadeTimeMs, MusicFile nextSong) {

		int startVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

		long fadeIntervall =(fadeTimeMs / startVolume);	
		Log.d("SAMPleActivity", "fadeTimeMs: "+fadeTimeMs);
		Log.d("SAMPleActivity", "startVolume: "+startVolume);
		Log.d("SAMPleActivity", "fadeIntervall: "+fadeIntervall);
		
		MusicTimer fOut = new MusicTimer(fadeTimeMs, fadeIntervall, startVolume, am, mp, nextSong);
		
		fOut.start();
		
	}

	
	public void pause() {
		mp.pause();

	}

}