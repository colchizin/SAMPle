package org.sample.musicplayer;

import org.sample.musicfiles.MusicFile;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;

public class SoundPoolPlayer implements MusicPlayer{

	private AudioManager audio;
	private SoundPool sound;
	
	public SoundPoolPlayer(Context mContext){
		audio = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	
	public void play(MusicFile song) {
		sound = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		final int curVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
		final int soundID =sound.load(song.getFilename(), 1);
		sound.setOnLoadCompleteListener(new OnLoadCompleteListener(){

			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status) {
				
				sound.play(soundID, curVolume, curVolume, 0, 0, 1f);
			}});
				
	}

	public void fadeIn(long fadeTime, MusicFile nextSong) {
		// TODO Auto-generated method stub
		
	}

	public void pause() {
		// TODO Auto-generated method stub
		
	}

	public void stop() {
		// TODO Auto-generated method stub
		
	}

	public void increaseVolume() {
		// TODO Auto-generated method stub
		
	}

	public void decreaseVolume() {
		// TODO Auto-generated method stub
		
	}

	public void resume() {
		// TODO Auto-generated method stub
		
	}

}
