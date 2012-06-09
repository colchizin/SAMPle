package org.sample.musicfiles;

import java.util.List;

import android.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MusicFileAdapter extends BaseAdapter {
	protected List<MusicFile> mFiles;
	protected Activity mActivity;
	
	public MusicFileAdapter(List<MusicFile> files, Activity activity) {
		this.mFiles = files;
		this.mActivity = activity;
	}
	
	@Override
	public int getCount() {
		return mFiles.size();
	}

	@Override
	public Object getItem(int idx) {
		return mFiles.get(idx);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater inflater = mActivity.getLayoutInflater();
			convertView = inflater.inflate(R.layout.two_line_list_item, null);
		}
		
		TextView tv1 = ((TextView)(convertView.findViewById(R.id.text1)));
		TextView tv2 = ((TextView)(convertView.findViewById(R.id.text2)));

		MusicFile file = (MusicFile) getItem(position);
		tv1.setText(file.getTitle() + " (" + file.album + ")");
		tv2.setText("BPM: " + file.getBPM() + ", AID: " + file.getAID());
		
		return convertView;
	}
	
}
