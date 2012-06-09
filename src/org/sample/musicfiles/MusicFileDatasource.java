package org.sample.musicfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MusicFileDatasource extends Datasource {
	private boolean default_depth = true;
	private SQLiteDatabase		database;
	private String[]			fileColumns = {
			MusicFileDBHelper.COLUMN_FILES_ID,
			MusicFileDBHelper.COLUMN_FILES_FILENAME,
			MusicFileDBHelper.COLUMN_FILES_FILETYPE
	};
	
	
	public MusicFileDatasource(Context context) {
		super(context);
	}
	
	/*
	 * Trägt die übergebene Musikdatei als neuen Eintrag in die Datenbank ein.
	 * Die zugehörige ID wird im Objekt gespeichert und das Objekt wieder
	 * zurückgegeben
	 * @param MusicFile	file	Die zu speichernde Musikdatei
	 * @return MusicFile		Die gespeicherte Musikdatei inkl. ID
	 */
	public MusicFile createMusicFile(MusicFile file) {
		// TODO
		return file;
	}
	
	/*
	 * Findet einen Musikdatei-Eintrag anhand der übergebenen id
	 * @param id	Die ID der Musikdatei
	 * @return MusicFile MusicFile
	 */
	public MusicFile findById(int id, boolean deep) {
		String[] selectArgs = {Integer.toString(id)};
		TagDatasource tagSource = new TagDatasource(context);
		
		Cursor filecursor = database.query(
				MusicFileDBHelper.TABLE_FILES,
				fileColumns,
				MusicFileDBHelper.COLUMN_FILES_ID + "=?s",
				selectArgs, null, null, null);
		
		filecursor.moveToFirst();
		
		MusicFile file = cursorToMusicFile(filecursor);
		
		if (deep) {
			HashMap<String,String> tags = tagSource.findAllByFileId(id);
			file.setTags(tags);
		}
		
		filecursor.close();
		
		return file;
	}
	
	/*
	 * Wie findById(int id, boolean deep), nur dass deep=default_depth
	 * angenommen wird
	 */
	public MusicFile findById(int id) {
		return findById(id, default_depth);
	}
	
	public List<MusicFile> findAll(boolean deep) {
		List<MusicFile> fileList = new ArrayList<MusicFile>();
		TagDatasource tagSource = null;
		
		if (deep)
			tagSource = new TagDatasource(context);
		
		Cursor cursor = database.query(
			MusicFileDBHelper.TABLE_FILES,
			fileColumns,
			null,null,null,null,null
		);
		
		cursor.moveToFirst();
		while(!cursor.isAfterLast()) {
			MusicFile file = cursorToMusicFile(cursor);
			if (deep) {
				file.setTags(tagSource.findAllByFileId(file.getId()));
			}
			fileList.add(file);
		}
		
		return fileList;
	}
	
	/*
	 * Wie findAll(boolean deep), nur dass deep=default_depth angenommen wird
	 */
	public List<MusicFile> findAll() {
		return findAll(default_depth);
	}
	
	/*
	 * Baut aus dem Daten, die aus der Datenbank geladen wurden, ein Objekt
	 * vom Typ MusicFile auf und liefert es zurück.
	 * In Abhängigkeit vom in der DB gespeicherten Dateityp wird entweder
	 * - eine MP3-Datei erstellt
	 * - oder, wenn ein unbekannter Dateityp angegeben ist, null
	 *   zurückgeliefert
	 * Die im zweiten Parameter übergebenen Tag-Daten werden ausgelesen,
	 * in einer HashMap gespeichert und an den erstellten File übergeben
	 * 
	 * @param filecursor	Datensatz zur Musikdatei
	 * @param tagcursor		Datensatz der Tags zur Musikdatei
	 * @return MusicFile	MusicFile aus übergebenen Daten oder null
	 */
	protected MusicFile cursorToMusicFile(Cursor filecursor) {
		MusicFile file = null;
		
		switch (filecursor.getInt(MusicFileDBHelper.COLUMN_FILES_FILETYPE_INDEX)) {
		case MusicFile.FILETYPE_MP3:
			 file = new MP3File(filecursor.getString(MusicFileDBHelper.COLUMN_FILES_FILENAME_INDEX));
			 break;
		default:	// Kein unterstütztes Format gefunden
			return null;
		}
		
		file.setId(filecursor.getInt(MusicFileDBHelper.COLUMN_FILES_ID_INDEX));
		
		return file;
	}
	
	public void setDefaultDepth(boolean deep) {
		default_depth = true;
	}
}
 