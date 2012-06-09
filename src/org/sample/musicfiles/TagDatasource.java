package org.sample.musicfiles;

import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;

public class TagDatasource extends Datasource{
	
	private String[]			tagColumns = {
			MusicFileDBHelper.COLUMN_TAGS_ID,
			MusicFileDBHelper.COLUMN_TAGS_FILE_ID,
			MusicFileDBHelper.COLUMN_TAGS_KEY,
			MusicFileDBHelper.COLUMN_TAGS_VALUE,
	};
	
	/*
	 * Konstruktor. Erstellt das MusicFileDBHelper-Objekt mit dem überebenen
	 * Kontext
	 * @param context	Kontext
	 */
	public TagDatasource(Context context) {
		super(context);
	}
	
	public HashMap<String,String> findAllByFileId(int fileid) {
		String[] selectArgs = {Integer.toString(fileid)};
		
		Cursor cursor = database.query(
				MusicFileDBHelper.TABLE_TAGS,
				tagColumns,
				MusicFileDBHelper.COLUMN_TAGS_FILE_ID + "=?s",
				selectArgs, null, null, null);
		
		return cursorToHashmap(cursor);
	}
	
	public HashMap<String,String> cursorToHashmap(Cursor tagcursor) {
		HashMap<String,String> tags = new HashMap<String,String>();
		
		while(!tagcursor.isAfterLast()) {
			tags.put(tagcursor.getString(MusicFileDBHelper.COLUMN_TAGS_KEY_INDEX),
					 tagcursor.getString(MusicFileDBHelper.COLUMN_TAGS_VALUE_INDEX));
			tagcursor.moveToNext();
		}
		
		return tags;
	}
}
