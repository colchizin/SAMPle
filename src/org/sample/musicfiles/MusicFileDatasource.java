package org.sample.musicfiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sample.musicfiles.musicretriever.MusicRetriever.Item;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class MusicFileDatasource extends Datasource {
	private boolean default_depth = true;
	private String TAG = "MusicFileDatasource";
	
	private SQLiteDatabase		database;
	private String[]			fileColumns = {
			MusicFileDBHelper.COLUMN_FILES_ID,
			MusicFileDBHelper.COLUMN_FILES_FILENAME,
			MusicFileDBHelper.COLUMN_FILES_FILETYPE,
			MusicFileDBHelper.COLUMN_FILES_BPM
	};
	
	
	public MusicFileDatasource(Context context) {
		super(context);
		database = MusicFileDBHelper.getDatabase();
	}
	
	/*
	 * Trägt die übergebene Musikdatei als neuen Eintrag in die Datenbank ein.
	 * Die zugehörige ID wird im Objekt gespeichert und das Objekt wieder
	 * zurückgegeben
	 * @param MusicFile	file	Die zu speichernde Musikdatei
	 * @return MusicFile		Die gespeicherte Musikdatei inkl. ID
	 */
	public MusicFile createMusicFile(MusicFile file) {
		ContentValues values = new ContentValues();
		
		values.put(MusicFileDBHelper.COLUMN_FILES_FILENAME, file.getFilename());
		values.put(MusicFileDBHelper.COLUMN_FILES_BPM, ((String)(file.getTag("bpm"))));
		values.put(MusicFileDBHelper.COLUMN_FILES_FILETYPE, file.getFiletype());
		
		long insertId = database.insert(MusicFileDBHelper.TABLE_FILES, null, values);
		file.setId(insertId);
		
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
			Log.v(TAG, cursor.getString(MusicFileDBHelper.COLUMN_FILES_FILENAME_INDEX));
			cursor.moveToNext();
		}
		
		cursor.close();
		
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
		file.setFiletype(filecursor.getInt(MusicFileDBHelper.COLUMN_FILES_FILETYPE_INDEX));
		
		return file;
	}
	
	public void setDefaultDepth(boolean deep) {
		default_depth = true;
	}
	
	
	
	public void indexMediafiles() {
		ContentResolver contentResolver = context.getContentResolver();
		
		Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	    List<MusicFile> files = new ArrayList<MusicFile>(); 
		
        Log.i(TAG, "Querying media...");
        Log.i(TAG, "URI: " + uri.toString());

        // Perform a query on the content resolver. The URI we're passing specifies that we
        // want to query for all audio media on external storage (e.g. SD card)
        Cursor cur = contentResolver.query(uri, null,
                MediaStore.Audio.Media.IS_MUSIC + " = 1", null, null);
        Log.i(TAG, "Query finished. " + (cur == null ? "Returned NULL." : "Returned a cursor."));

        if (cur == null) {
            // Query failed...
            Log.e(TAG, "Failed to retrieve music: cursor is null :-(");
            return;
        }
        if (!cur.moveToFirst()) {
            // Nothing to query. There is no music on the device. How boring.
            Log.e(TAG, "Failed to move cursor to first row (no query results).");
            return;
        }

        // retrieve the indices of the columns where the ID, title, etc. of the song are
        int artistColumn = cur.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int titleColumn = cur.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int albumColumn = cur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int durationColumn = cur.getColumnIndex(MediaStore.Audio.Media.DURATION);
        int idColumn = cur.getColumnIndex(MediaStore.Audio.Media._ID);
        int idFilename = cur.getColumnIndex(MediaStore.Audio.Media.DATA);

        // add each song to mItems
        do {
        	MusicFile file;
            String filename = cur.getString(idFilename);
            String lastSegment = Uri.parse(filename).getLastPathSegment();
            if (lastSegment.endsWith("mp3")) {
            	file = new MP3File(filename);
            } else {
            	Log.w(TAG, "Invalid file type " + lastSegment + ". Skipping");
            	continue;
            }
            files.add(file);
            
        } while (cur.moveToNext());
        
        cur.close();
        
        for(MusicFile file : files) {
        	this.createMusicFile(file);
        }
        
        System.gc();

        Log.i(TAG, "Done indexing media. Library is ready.");
	}
}
 