package org.sample.musicfiles;

import java.util.List;

import org.sample.musicfiles.musicretriever.MusicRetriever;
import org.sample.musicfiles.musicretriever.MusicRetriever.Item;
import org.sample.musicfiles.musicretriever.IndexMusicFilesTask;

import android.app.ListActivity;
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

	@Override
	public void onMusicRetrieverPrepared() {
		List<MusicFile> files = pDatasource.findAll(false);
		String[] titles = new String[files.size()];
		
		if (files.size() > 0) {
			for (int i = 0; i<files.size(); i++) {
				titles[i] = files.get(i).getFilename();
			}
			
			this.setListAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item_1, titles));
			
			Toast.makeText(getApplicationContext(), "Musikstücke geladen", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Keine Musikstücke gefunden", Toast.LENGTH_SHORT).show();
		}
	}
}
