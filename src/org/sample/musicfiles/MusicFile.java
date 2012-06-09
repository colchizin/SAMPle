package org.sample.musicfiles;

import java.util.HashMap;

public abstract class MusicFile {
	public static final int FILETYPE_MP3 = 1;
	public static final int FILETYPE_OGG = 2;
	
	public HashMap<String,String>	tags;	// Assoziative Liste zum Speichern der ID3-Tags
	
	protected String	filename;
	protected int		id;
	protected int		filetype;
	
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
		return tags.get(tagname);
	}
	
	/*
	 * Liefert den Dateinamen zurück
	 * @return Dateiname
	 */
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setTags(HashMap<String,String> tags) {
		this.tags = tags;
	}
}
