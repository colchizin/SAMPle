package org.sample.musicplayer;

import org.sample.musicfiles.MusicFile;

public interface MusicPlayer {
	
	public void play(MusicFile song);
	public void fadeIn(long fadeTime, MusicFile nextSong);
	public void pause();
	

}