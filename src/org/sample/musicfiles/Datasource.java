/**
 * 
 */
package org.sample.musicfiles;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author johannes
 *
 */
public abstract class Datasource {
	MusicFileDBHelper helper	= null;
	SQLiteDatabase database		= null;
	Context context				= null;
	/*
	 * Konstruktor. Erstellt das MusicFileDBHelper-Objekt mit dem überebenen
	 * Kontext
	 * @param context	Kontext
	 */
	public Datasource(Context context) {
		helper = MusicFileDBHelper.getHelper(context, false);
		this.context = context;
	}
	
	/*
	 * Stellt eine Datenbankverbindung her
	 * @throws SQLException
	 */
	public void open() throws SQLException  {
		database = MusicFileDBHelper.getDatabase();
	}
	
	/*
	 * Schließt die Datenbankverbindung
	 */
	public void close() {
		helper.close();
	}	
}
