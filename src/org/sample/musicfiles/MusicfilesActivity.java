package org.sample.musicfiles;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.sample.musicfiles.musicretriever.MusicRetriever;
import org.sample.musicfiles.musicretriever.MusicRetriever.Item;
import org.sample.musicfiles.musicretriever.IndexMusicFilesTask;
import org.sample.musicplayer.MusicService;
import org.sample.R;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MusicfilesActivity extends Activity implements
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
		
		this.setContentView(R.layout.musicfiles_activity_layout);
		
		Button play = (Button)(this.findViewById(R.id.button_play));
		play.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MusicService.ACTION_PLAY);
				startService(intent);
				Log.d(TAG, "Button Play clicked");
			}
		});
		
		Button pause = (Button)(this.findViewById(R.id.button_pause));
		pause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MusicService.ACTION_PAUSE);
				startService(intent);
			}
		});
		
		Button stop = (Button)(this.findViewById(R.id.button_stop));
		stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MusicService.ACTION_STOP);
				startService(intent);
			}
		});
		
		Button next = (Button)(this.findViewById(R.id.button_next));
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MusicService.ACTION_SKIP);
				startService(intent);
			}
		});
		
		Intent intent = new Intent(MusicService.ACTION_PREPARE);
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
			//this.setListAdapter(new MusicFileAdapter(files, this));
			
			Toast.makeText(getApplicationContext(), "Musikstücke geladen", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Keine Musikstücke gefunden", Toast.LENGTH_SHORT).show();
		}		
	}
}
