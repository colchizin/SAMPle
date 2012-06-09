package org.sample.musicfiles;

public class MP3File extends MusicFile {

	public MP3File(String filename) {
		super(filename);
		this.filetype = FILETYPE_MP3;
		// TODO Auto-generated constructor stub
	}

	@Override
	public MusicFile readFromFile() {
		this.bpm = ID3Parser.parseBPM(filename);
		return this;
	}
}
