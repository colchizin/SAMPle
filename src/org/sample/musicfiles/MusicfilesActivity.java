package org.sample.musicfiles;

import java.util.List;

import org.sample.musicfiles.musicretriever.MusicRetriever;
import org.sample.musicfiles.musicretriever.MusicRetriever.Item;
import org.sample.musicfiles.musicretriever.PrepareMusicRetrieverTask;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.R;;

public class MusicfilesActivity extends ListActivity implements
	PrepareMusicRetrieverTask.MusicRetrieverPreparedListener
{
	public String[] test = {"Hallo", "Welt", "Die", "dritte"};
	
	public static final String TAG = "MusicfilesActivity";
	
	protected MusicRetriever pRetriever = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		pRetriever = new MusicRetriever(this.getContentResolver());
		
		Toast.makeText(getApplicationContext(), "Lade Musikstücke", Toast.LENGTH_SHORT).show();
		
		(new PrepareMusicRetrieverTask(pRetriever, this)).execute();
	}


	public void onMusicRetrieverPrepared() {
		List<Item> items = pRetriever.getItems();
		String[] titles = new String[items.size()];
		
		if (items.size() > 0) {
			for (int i = 0; i<items.size(); i++) {
				titles[i] = items.get(i).getTitle();
			}
			
			this.setListAdapter(new ArrayAdapter<String>(this, R.layout.simple_list_item_1, titles));
			
			Toast.makeText(getApplicationContext(), "Musikstücke geladen", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "Keine Musikstücke gefunden", Toast.LENGTH_SHORT).show();
		}
	}
}
