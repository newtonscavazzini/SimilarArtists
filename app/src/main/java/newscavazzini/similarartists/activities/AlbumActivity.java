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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import newscavazzini.similarartists.*;
import newscavazzini.similarartists.adapters.*;
import newscavazzini.similarartists.listeners.TrackClickListener;
import newscavazzini.similarartists.listeners.YoutubePlayStoreClickListener;
import newscavazzini.similarartists.models.track.Track;
import newscavazzini.similarartists.retrofit.RetrofitInitializer;
import newscavazzini.similarartists.retrofit.deserializer.AlbumTracklistDeserializer;
import newscavazzini.similarartists.ui.ToolbarExtension;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AlbumActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Track> mTracks = new ArrayList<>();
    private String mArtistName;
    private String mAlbumName;
    private int mDownloadAttempts;
    private YoutubePlayStoreClickListener youtubePlayStoreClickListener;

    private Toolbar mToolbar;
    private GridView mTracksGv;
    private ToolbarExtension mToolbarExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Album Tracklist");

        mToolbarExtension = new ToolbarExtension(this);

        youtubePlayStoreClickListener = new YoutubePlayStoreClickListener(this);

        if (savedInstanceState != null
                && ((List<Track>)savedInstanceState.getSerializable("tracks")).size() > 0 )  {

            loadFromInstanceState(savedInstanceState);

        } else {

            Intent intent = getIntent();
            if(intent != null && intent.getExtras() != null) {

                mArtistName = intent.getExtras().getString("artistName");
                mAlbumName = intent.getExtras().getString("albumName");

                loadFromNetwork();

            } else {
                finish();
            }

        }

    }

    private void downloadFailed() {
        findViewById(R.id.download_failed_ll).setVisibility(View.VISIBLE);
        findViewById(R.id.try_again).setOnClickListener(this);
    }

    private void setUi(String albumName, String artistName, List<Track> tracks) {

        findViewById(R.id.download_failed_ll).setVisibility(View.GONE);

        this.mTracks = tracks;
        this.mAlbumName = albumName;
        this.mArtistName = artistName;

        mToolbarExtension.show(R.drawable.ic_album_white, albumName, artistName);

        mTracksGv = (GridView) findViewById(R.id.tracks_gv);
        mTracksGv.setAdapter(new TracksAdapter(this, tracks, new TrackClickListener(this, mArtistName)));

        loadingUi(false);
        scrollToTopOfScreen();

    }

    private void tryAgain() {
        this.mDownloadAttempts = 0;

        loadingUi(true);

        View emptyBox = findViewById(R.id.download_failed_ll);
        emptyBox.setVisibility(View.GONE);

        loadFromNetwork();
    }

    private void loadFromInstanceState(Bundle instanceState) {

        String albumName = instanceState.getString("albumName");
        String artistName = instanceState.getString("artistName");
        List<Track> tracks = (List<Track>) instanceState.getSerializable("tracks");

        if (albumName == null && artistName == null && tracks == null) {
            downloadFailed();
            return;
        }

        setUi(albumName, artistName, tracks);

    }

    private void loadFromNetwork() {

        mDownloadAttempts++;

        if (mDownloadAttempts > 3) {
            downloadFailed();
            loadingUi(false);

            return;
        }

        Gson gsonAlbum = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Track>>(){}.getType(), new AlbumTracklistDeserializer())
                .create();

        Call<List<Track>> albumCall = new RetrofitInitializer(gsonAlbum)
                .getAlbumsService()
                .getTracksFromAlbum(mArtistName, mAlbumName, RetrofitInitializer.LAST_FM_KEY);

        albumCall.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, final Response<List<Track>> response) {

                if (response.body() == null || response.body().size() == 0) {
                    loadingUi(true);
                    loadFromNetwork();
                } else {

                    setUi(mAlbumName, mArtistName, response.body());

                }

            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                loadingUi(true);
                loadFromNetwork();
            }
        });

    }

    private void loadingUi(boolean loading) {

        LinearLayout loadingLl = (LinearLayout) findViewById(R.id.loading);
        LinearLayout contentLl = (LinearLayout) findViewById(R.id.content);

        if (loading) {

            mToolbarExtension.clear();
            loadingLl.setVisibility(View.VISIBLE);
            contentLl.setVisibility(View.GONE);

        } else {
            contentLl.setVisibility(View.VISIBLE);
            loadingLl.setVisibility(View.GONE);
        }

    }

    private void scrollToTopOfScreen() {
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollviewMain);
        scrollView.smoothScrollTo(0, 0);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putString("albumName", mAlbumName);
        bundle.putString("artistName", mArtistName);
        bundle.putSerializable("tracks", new ArrayList<>(mTracks));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_youtube, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mArtistName == null || mAlbumName == null) return false;

        switch (item.getItemId()) {

            case R.id.google_play:
                youtubePlayStoreClickListener.openPlayStore(mArtistName + " " + mAlbumName);
                break;

            case R.id.youtube:
                youtubePlayStoreClickListener.openYoutube(mArtistName + " " + mAlbumName);
                break;

        }

        return true;

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