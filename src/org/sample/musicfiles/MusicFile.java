package org.sample.musicfiles;

import java.util.HashMap;

public abstract class MusicFile {
	public static final int FILETYPE_MP3 = 1;
	public static final int FILETYPE_OGG = 2;
	
	public HashMap<String,String>	tags;	// Assoziative Liste zum Speichern der ID3-Tags
	
	protected String	filename;	// Dateiname im Dateisystem
	protected String	uri;		// Content-Uri
	protected long		id;			// ID in der BPM-Tabelle
	protected long		aid;		// ID in der Android-Tabelle
	protected int		filetype;
	
	protected int		bpm;
	public String		artist;
	public String		title;
	public String		album;
	public long			duration;
	
	/*
	 * Der Konstruktor setzt den Dateinamen fest.
	 * @param filename	Dateiname der Musikdatei
	 */
	public MusicFile(String filename) {
		this.filename = filename;
	}
	
	/*
	 * Liest aus der Im Konstruktor übergebenen Datei die Daten ein.
	 * Vorerst nur die Tags, mehr interessiert uns ja nicht.
	 * Da das für unterschiedliche Dateitypen unterschiedlich abläuft muss das
	 * in einer abgeleiteten Klasse erfolgen.
	 * @return liefert dieses Objekt zurück
	 */
	public abstract MusicFile readFromFile();
	
	/*
	 * Liefert den durch den übergebenen String definierten Tag zurück
	 * @param	tagname	Name des gesuchten Tags
	 * @return	Wert des Tags oder null
	 */
	public Object getTag(String tagname) {
		if (tags != null)
			return tags.get(tagname);
		return null;
	}
	
	/*
	 * Liefert den Dateinamen zurück
	 * @return Dateiname
	 */
	public String getFilename() {
		return filename;
	}
	
	public int getFiletype() {
		return filetype;
	}

	public void setFiletype(int filetype) {
		this.filetype = filetype;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setTags(HashMap<String,String> tags) {
		this.tags = tags;
	}

	public int getBPM() {
		return this.bpm;
	}

	public void setBPM(int bpm) {
		this.bpm = bpm;
	}
	
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public void setMetadata(String title, String artist, String album, long duration) {
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.duration = duration;
	}

	public CharSequence getTitle() {
		// TODO Auto-generated method stub
		return this.title;
	}
	
	public long getAID() {
		return this.aid;
	}
	
	public void setAID(long aid) {
		this.aid = aid;
	}
}
