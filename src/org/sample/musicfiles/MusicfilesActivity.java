package org.sample.musicfiles;

import java.io.IOException;
import java.util.List;

import org.sample.musicfiles.musicretriever.MusicRetriever;
import org.sample.musicfiles.musicretriever.MusicRetriever.Item;
import org.sample.musicfiles.musicretriever.IndexMusicFilesTask;

import android.app.ListActivity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.R;

public class MusicfilesActivity extends ListActivity implements
	IndexMusicFilesTask.MusicFileDatasourceIndexedListener
{
	public String[] test = {"Hallo", "Welt", "Die", "dritte"};
	
	public static final String TAG = "MusicfilesActivity";
	
	protected MusicRetriever pRetriever = null;
	protected MusicFileDatasource pDatasource = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		pDatasource = new MusicFileDatasource(this);
		
		MusicFileDBHelper helper = new MusicFileDBHelper(this);
		//helper.onUpgrade(MusicFileDBHelper.getDatabase(),1,1);
				
		Toast.makeText(getApplicationContext(), "Lade Musikstücke", Toast.LENGTH_SHORT).show();
		
		//(new IndexMusicFilesTask(pDatasource, this)).execute();
		this.onMusicRetrieverPrepared();
	}


	public void onMusicRetrieverPrepared() {
		List<MusicFile> files = pDatasource.findAllByBPM(125,10);
		String[] titles = new String[files.size()];
		
		if (files.size() > 0) {
			for (int i = 0; i<files.size(); i++) {
				titles[i] = files.get(i).getFilename();
			}
			
			//this.setListAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item_1, titles));
			this.setListAdapter(new MusicFileAdapter(files, this));
			
			Toast.makeText(getApplicationContext(), "Musikstücke geladen", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Keine Musikstücke gefunden", Toast.LENGTH_SHORT).show();
		}
		
		MediaPlayer player = new MediaPlayer();
		
		try {
			player.setDataSource(files.get(0).getFilename());
			player.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.start();
					Toast.makeText(getApplicationContext(), "Musikstücke abspielen", Toast.LENGTH_SHORT).show();
				}
			});
			player.prepareAsync();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/*MusicFile file = new MP3File("/mnt/sdcard/external_sd/Music/Arctic Monkeys/Favorite Worst Nightmare/01. brainstorm.mp3");
		file.readFromFile();
		Log.i(TAG, file.getFilename() + ": " + file.getBPM());*/
		
		/*ID3Parser.parse("/mnt/sdcard/Music/dr._nagel__nu_remix__dirty_doering_bar25.mp3");
		ID3Parser.parse("/mnt/sdcard/Music/01 Les Violons Ivres [Extended Version].mp3");
		ID3Parser.parse("/mnt/sdcard/Music/04 Vincero [carwash remix].mp3");
		ID3Parser.parse("/mnt/sdcard/Music/Azul cielo M _MMM.mp3");
		ID3Parser.parse("/mnt/sdcard/Music/Beeswax (Star Slinger Remix).mp3");*/
		
		//System.exit(0);
	}
}
