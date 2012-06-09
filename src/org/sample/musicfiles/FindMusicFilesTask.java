package org.sample.musicfiles;

/*   
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.List;

import org.sample.musicfiles.MusicFileDatasource;

import android.os.AsyncTask;

/**
 * Asynchronous task that prepares a MusicRetriever. This asynchronous task essentially calls
 * {@link MusicRetriever#prepare()} on a {@link MusicRetriever}, which may take some time to
 * run. Upon finishing, it notifies the indicated {@MusicRetrieverPreparedListener}.
 */
public class FindMusicFilesTask extends AsyncTask<Void, Void, Void> {
    MusicFileDatasource mDatasource;
    MusicFilesFoundListener mListener;
    List<MusicFile> mFiles = null;
    String conditions;

    public FindMusicFilesTask(MusicFileDatasource datasource,
    		String conditions, MusicFilesFoundListener listener) {
        mDatasource = datasource;
        mListener = listener;
        this.conditions = conditions;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
    	
    	this.mFiles = mDatasource.findAll(false, this.conditions);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mListener.onMusicFilesFound(mFiles);
    }

    public interface MusicFilesFoundListener {
        public void onMusicFilesFound(List<MusicFile> files);
    }
}