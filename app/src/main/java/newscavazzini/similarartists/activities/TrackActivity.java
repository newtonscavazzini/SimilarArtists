/*
    Similar Artists
    Copyright (C) 2017  Newton Scavazzini <newtonscavazzini@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package newscavazzini.similarartists.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import newscavazzini.similarartists.R;
import newscavazzini.similarartists.listeners.YoutubePlayStoreClickListener;
import newscavazzini.similarartists.models.track.Track;
import newscavazzini.similarartists.retrofit.RetrofitInitializer;
import newscavazzini.similarartists.retrofit.deserializer.TrackDeserializer;
import newscavazzini.similarartists.ui.ToolbarExtension;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackActivity extends AppCompatActivity implements View.OnClickListener {

    private String mArtistName;
    private String mTrackName;
    private String mAlbumName;
    private String mWiki;
    private ToolbarExtension toolbarExtension;
    private YoutubePlayStoreClickListener youtubePlayStoreClickListener;
    private int mDownloadAttempts;
    private Bundle mExtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) setSupportActionBar(toolbar);

        toolbarExtension = new ToolbarExtension(this);
        youtubePlayStoreClickListener = new YoutubePlayStoreClickListener(this);

        Intent intent = getIntent();

        if (intent != null && intent.getExtras() != null) {

            this.mExtras = intent.getExtras();

            if (savedInstanceState != null) {

                loadFromInstanceState(savedInstanceState);
                scrollToTopOfScreen();


            }else {

                loadFromNetwork();
            }


        } else {

            finish();

        }
    }

    private void loadingUi(boolean loading) {

        LinearLayout loadingLl = (LinearLayout) findViewById(R.id.loading);
        LinearLayout contentLl = (LinearLayout) findViewById(R.id.content);

        if (loading) {

            toolbarExtension.clear();
            loadingLl.setVisibility(View.VISIBLE);
            contentLl.setVisibility(View.GONE);

        } else {
            contentLl.setVisibility(View.VISIBLE);
            loadingLl.setVisibility(View.GONE);
        }

    }

    private void tryAgain() {
        this.mDownloadAttempts = 0;

        loadingUi(true);

        View emptyBox = findViewById(R.id.download_failed_ll);
        emptyBox.setVisibility(View.GONE);

        loadFromNetwork();
    }

    private void downloadFailed() {
        findViewById(R.id.download_failed_ll).setVisibility(View.VISIBLE);
        findViewById(R.id.try_again).setOnClickListener(this);
    }

    private void setUi(String artistName, String trackName, String albumName, String wiki) {

        findViewById(R.id.download_failed_ll).setVisibility(View.GONE);

        this.mArtistName = artistName;
        this.mTrackName = trackName;
        this.mAlbumName = albumName;
        this.mWiki = wiki;

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(artistName + " - " + trackName);
        }

        TextView trackWikiTv = (TextView) findViewById(R.id.track_wiki_tv);
        TextView wikiTitleTv = (TextView) findViewById(R.id.title_wiki_tv);

        toolbarExtension.show(R.drawable.ic_audiotrack_white, trackName, artistName);

        trackWikiTv.setText(wiki);

        if(wiki.equals("")){
            wikiTitleTv.setVisibility(View.GONE);
        }

        scrollToTopOfScreen();

    }

    private void loadFromNetwork() {

        if (this.mExtras == null) finish();

        mDownloadAttempts++;

        if (mDownloadAttempts > 3) {
            downloadFailed();
            loadingUi(false);

            return;
        }

        String artistName = this.mExtras.getString("artistName");
        final String trackName = this.mExtras.getString("trackName");

        Gson gsonTrack = new GsonBuilder()
                .registerTypeAdapter(Track.class, new TrackDeserializer())
                .create();

        Call<Track> trackCall = new RetrofitInitializer(gsonTrack)
                .getTracksService()
                .getInfo(artistName, trackName, RetrofitInitializer.LAST_FM_KEY, getString(R.string.lang));

        trackCall.enqueue(new Callback<Track>() {
            @Override
            public void onResponse(Call<Track> call, final Response<Track> response) {

                final Track track = response.body();

                TrackActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String artistName = "";
                        String trackName = "";
                        String albumName = "";
                        String wikiContent = "";

                        if (track != null) {

                            if (track.getArtist() != null && track.getArtist().getName() != null) {
                                artistName = track.getArtist().getName();
                            }

                            if (track.getName() != null) {
                                trackName = track.getName();
                            }

                            if (track.getAlbum() != null && track.getAlbum().getTitle() != null) {
                                albumName = track.getAlbum().getTitle();
                            }

                            if (track.getWiki() != null && track.getWiki().getContent() != null) {
                                wikiContent = track.getWiki().getContent();
                            }

                            setUi(artistName, trackName, albumName, wikiContent);
                            loadingUi(false);

                        } else {
                            TrackActivity.this.finish();
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<Track> call, Throwable t) {
                loadingUi(true);
                loadFromNetwork();
            }
        });

    }

    private void loadFromInstanceState(Bundle savedInstanceState) {

        String artistName = savedInstanceState.getString("artistName");
        String trackName = savedInstanceState.getString("trackName");
        String albumName = savedInstanceState.getString("albumName");
        String wiki = savedInstanceState.getString("wiki");

        if (artistName == null && trackName == null && albumName == null && wiki == null) {
            downloadFailed();
            return;
        }

        setUi(artistName, trackName, albumName, wiki);
        loadingUi(false);


    }

    private void scrollToTopOfScreen() {
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollview_main);
        scrollView.smoothScrollTo(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_youtube, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mArtistName == null || mTrackName == null) return false;

        switch (item.getItemId()) {

            case R.id.google_play:
                youtubePlayStoreClickListener.openPlayStore(mArtistName + " " + mTrackName);
                break;

            case R.id.youtube:
                youtubePlayStoreClickListener.openYoutube(mArtistName + " " + mTrackName);
                break;

        }

        return true;

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("artistName", mArtistName);
        outState.putString("trackName", mTrackName);
        outState.putString("albumName", mAlbumName);
        outState.putString("wiki", mWiki);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.try_again:
                tryAgain();
                break;

        }
    }
}