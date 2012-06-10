package org.sample.musicfiles;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MusicFileDBHelper extends SQLiteOpenHelper {

	public static final String TABLE_FILES = "musicfiles";
	public static final String TABLE_TAGS = "tags";
	
	public static final String COLUMN_FILES_ID = "id";
	public static final String COLUMN_FILES_FILENAME = "filename";
	public static final String COLUMN_FILES_FILETYPE = "filetype";
	public static final String COLUMN_FILES_BPM = "bpm";
	public static final String COLUMN_FILES_AID = "aid";
	
	public static final int COLUMN_FILES_ID_INDEX = 0;
	public static final int COLUMN_FILES_FILENAME_INDEX = 1;
	public static final int COLUMN_FILES_FILETYPE_INDEX = 2;
	public static final int COLUMN_FILES_BPM_INDEX = 3;
	public static final int	COLUMN_FILES_AID_INDEX = 4;
	
	public static final String COLUMN_TAGS_ID = "id";
	public static final String COLUMN_TAGS_FILE_ID = "file_id";
	public static final String COLUMN_TAGS_KEY = "key";
	public static final String COLUMN_TAGS_VALUE = "value";
	
	public static final int COLUMN_TAGS_ID_INDEX = 0;
	public static final int COLUMN_TAGS_FILE_ID_INDEX = 1;
	public static final int COLUMN_TAGS_KEY_INDEX = 2;
	public static final int COLUMN_TAGS_VALUE_INDEX = 3;
	
	public static final String DATABASE_NAME = "musicfiles.db";
	public static final int DATABASE_VERSION = 1;
	
	protected static MusicFileDBHelper helper = null;
	protected static SQLiteDatabase database = null;
	
	private static final String TABLE_FILES_CREATE =
			"CREATE TABLE " + TABLE_FILES + " (" +
			COLUMN_FILES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_FILES_FILENAME + " TEXT NOT NULL, " +
			COLUMN_FILES_FILETYPE + " UNSIGNED SMALLINT NOT NULL, " +
			COLUMN_FILES_BPM + " UNSIGNED SMALLINT NULL, " + 
			COLUMN_FILES_AID + " UNSIGNED BIGINT NOT NULL" +
			");";
	
	// obsolete
	private static final String TABLE_TAGS_CREATE =
			"CREATE TABLE " + TABLE_TAGS + " (" +
			COLUMN_TAGS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			COLUMN_TAGS_FILE_ID + " INTEGER NOT NULL, " +
			COLUMN_TAGS_KEY + " VARCHAR(8) NOT NULL, " +
			COLUMN_TAGS_VALUE + " TEXT NOT NULL" +
			");";
	
	public MusicFileDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(TABLE_FILES_CREATE);
		database.execSQL(TABLE_TAGS_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(MusicFileDBHelper.class.getName(), "Upgrading database from version " +
				oldVersion + " to version " + newVersion);
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_TAGS + ";"); 
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_FILES + ";");
		onCreate(database);
	}
	
	public static MusicFileDBHelper getHelper(Context context, boolean createNew) {
		if (createNew || MusicFileDBHelper.helper == null) {
			MusicFileDBHelper.helper = new MusicFileDBHelper(context);
		}
		return MusicFileDBHelper.helper;
	}
	
	public static SQLiteDatabase getDatabase() {
		if (MusicFileDBHelper.helper != null)
			return MusicFileDBHelper.helper.getWritableDatabase();
		return null;
	}
	
	public static String getBPMCondition(int bpm, int tolerance) {
		
		String condition = MusicFileDBHelper.COLUMN_FILES_BPM + ">" + (bpm-tolerance) + " AND " +
				MusicFileDBHelper.COLUMN_FILES_BPM + "<" + (bpm+tolerance);
		return condition;
	}

}
