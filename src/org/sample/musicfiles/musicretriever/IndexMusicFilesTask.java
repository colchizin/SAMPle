package org.sample.musicfiles.musicretriever;

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

import org.sample.musicfiles.MusicFileDatasource;

import android.os.AsyncTask;

/**
 * Asynchronous task that prepares a MusicRetriever. This asynchronous task essentially calls
 * {@link MusicRetriever#prepare()} on a {@link MusicRetriever}, which may take some time to
 * run. Upon finishing, it notifies the indicated {@MusicRetrieverPreparedListener}.
 */
public class IndexMusicFilesTask extends AsyncTask<Void, Void, Void> {
    MusicFileDatasource mDatasource;
    MusicFileDatasourceIndexedListener mListener;

    public IndexMusicFilesTask(MusicFileDatasource datasource,
            MusicFileDatasourceIndexedListener listener) {
        mDatasource = datasource;
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        mDatasource.indexMediafiles();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        mListener.onMusicRetrieverPrepared();
    }

    public interface MusicFileDatasourceIndexedListener {
        public void onMusicRetrieverPrepared();
    }
}