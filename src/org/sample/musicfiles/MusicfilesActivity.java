package org.sample.musicfiles;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.sample.musicfiles.musicretriever.MusicRetriever;
import org.sample.musicfiles.musicretriever.MusicRetriever.Item;
import org.sample.musicfiles.musicretriever.IndexMusicFilesTask;
import org.sample.musicplayer.MusicService;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.R;

public class MusicfilesActivity extends ListActivity implements
	IndexMusicFilesTask.MusicFileDatasourceIndexedListener,
	FindMusicFilesTask.MusicFilesFoundListener
{
	public String[] test = {"Hallo", "Welt", "Die", "dritte"};
	
	public static final String TAG = "MusicfilesActivity";
	
	protected MusicRetriever pRetriever = null;
	protected MusicFileDatasource pDatasource = null;
	protected ProgressDialog pProgressDialog = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = new Intent();
		intent.setAction("org.sample.musicplayer.MusicService");
		startService(intent);
		
		/*pDatasource = new MusicFileDatasource(this);
		
		MusicFileDBHelper helper = new MusicFileDBHelper(this);
		//helper.onUpgrade(MusicFileDBHelper.getDatabase(),1,1);
				
		//Toast.makeText(getApplicationContext(), "Lade Musikstücke", Toast.LENGTH_SHORT).show();
		pProgressDialog = ProgressDialog.show(this,
				"Lade Musikstücke",
				"Die Musikstücke zur angegebenen Frequenz werden geladen");
		//onMusicFilesFound(pDatasource.findAll(false));
		//(new IndexMusicFilesTask(pDatasource, this)).execute();
		//(new FindMusicFilesTask(pDatasource, MusicFileDBHelper.getBPMCondition(125, 10), this)).execute();
		(new FindMusicFilesTask(pDatasource, null, this)).execute();*/
	}


	public void onMusicRetrieverPrepared() {
		//pProgressDialog.dismiss();
		Toast.makeText(getApplicationContext(), "Musikstücke importiert", Toast.LENGTH_SHORT).show();
		(new FindMusicFilesTask(pDatasource, MusicFileDBHelper.getBPMCondition(125, 10), this)).execute();
	}

	public void onMusicFilesFound(List<MusicFile> files) {
		pProgressDialog.dismiss();
		if (files.size() > 0) {
			//this.setListAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item_1, titles));
			this.setListAdapter(new MusicFileAdapter(files, this));
			
			Toast.makeText(getApplicationContext(), "Musikstücke geladen", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Keine Musikstücke gefunden", Toast.LENGTH_SHORT).show();
		}		
	}
}
