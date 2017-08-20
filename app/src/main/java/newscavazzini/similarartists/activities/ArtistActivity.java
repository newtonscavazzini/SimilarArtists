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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import newscavazzini.similarartists.R;
import newscavazzini.similarartists.adapters.ArtistAdapter;
import newscavazzini.similarartists.adapters.TracksAdapter;
import newscavazzini.similarartists.listeners.AlbumClickListener;
import newscavazzini.similarartists.listeners.ArtistClickListener;
import newscavazzini.similarartists.listeners.TrackClickListener;
import newscavazzini.similarartists.listeners.YoutubePlayStoreClickListener;
import newscavazzini.similarartists.models.album.Album;
import newscavazzini.similarartists.models.artist.Artist;
import newscavazzini.similarartists.models.track.Track;
import newscavazzini.similarartists.retrofit.RetrofitInitializer;
import newscavazzini.similarartists.retrofit.deserializer.AlbumDeserializer;
import newscavazzini.similarartists.retrofit.deserializer.ArtistDeserializer;
import newscavazzini.similarartists.retrofit.deserializer.TrackListDeserializer;
import newscavazzini.similarartists.ui.ToolbarExtension;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ArtistActivity extends AppCompatActivity implements View.OnClickListener {

    private Bundle mExtras;
    private Artist mArtist;
    private List<Track> mTopTracks = new ArrayList<>();
    private List<Album> mTopAlbums = new ArrayList<>();
    private int mDownloadAttempts;
    private YoutubePlayStoreClickListener youtubePlayStoreListener;

    private TextView mArtistBioTv;
    private GridView mSimilarArtistsGv;
    private GridView mTopTracksGv;
    private GridView mTopAlbumsGv;
    private Button mMoreSimilarBtn;
    private Button mFullBioBtn;
    private ToolbarExtension mToolbarExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) setSupportActionBar(toolbar);

        mToolbarExtension = new ToolbarExtension(this);
        youtubePlayStoreListener = new YoutubePlayStoreClickListener(this);

        Intent intent = getIntent();

        if(intent != null && intent.getExtras() != null) {

            mExtras = intent.getExtras();

            if(savedInstanceState != null) {

                loadDataFromInstanceState(savedInstanceState);
                scrollToTopOfScreen();

            } else {

                loadDataFromNetwork();
                scrollToTopOfScreen();

            }

        } else {
            finish();
        }

    }

    private void scrollToTopOfScreen() {
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollview_main);
        scrollView.smoothScrollTo(0, 0);
    }

    private void setAlbumsUi(List<Album> topAlbums) {

        this.mTopAlbums = topAlbums;

        if (topAlbums != null && topAlbums.size() > 0) {

            mTopAlbumsGv = (GridView) findViewById(R.id.albums_gv);
            mTopAlbumsGv.setAdapter(new newscavazzini.similarartists.adapters.AlbumsAdapter(this,
                    topAlbums, new AlbumClickListener(this, mExtras.getString("artistName"))));

        }
        else {
            findViewById(R.id.title_top_albums_tv).setVisibility(View.GONE);
        }

    }

    private void setTracksUi(List<Track> topTracks) {

        this.mTopTracks = topTracks;

        if (topTracks != null && topTracks.size() > 0) {

            mTopTracksGv = (GridView) findViewById(R.id.tracks_gv);
            mTopTracksGv.setAdapter(new TracksAdapter(this, topTracks,
                    new TrackClickListener(this, mExtras.getString("artistName"))));

        }
        else {
            findViewById(R.id.title_top_tracks_tv).setVisibility(View.GONE);
        }

    }

    private void tryAgain() {
        mDownloadAttempts = 0;

        uiLoading(true);
        findViewById(R.id.download_failed_ll).setVisibility(View.GONE);

        loadArtistFromNetwork();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.try_again:
                tryAgain();
                break;

        }
    }

    private void downloadFailed() {

        findViewById(R.id.loading).setVisibility(View.GONE);
        findViewById(R.id.download_failed_ll).setVisibility(View.VISIBLE);

        findViewById(R.id.try_again).setOnClickListener(this);

    }

    private void setArtistUi(Artist artist) {

        if (artist == null) {
            downloadFailed();
        }

        findViewById(R.id.download_failed_ll).setVisibility(View.GONE);

        this.mArtist = artist;

        if(getSupportActionBar() != null) getSupportActionBar().setTitle(artist.getName());

        mToolbarExtension.show(R.drawable.ic_person_white, artist.getName(), artist.getTagsAsString());

        mArtistBioTv = (TextView) findViewById(R.id.artist_bio_tv);
        mArtistBioTv.setText(artist.getBio().getSummary());
        if (artist.getBio().getSummary().equals("")) findViewById(R.id.title_about_tv).setVisibility(View.GONE);

        // Load similar list
        List<Artist> similarArtists = artist.getSimilar().getArtists();
        if (similarArtists != null && similarArtists.size() > 0) {
            mSimilarArtistsGv = (GridView) findViewById(R.id.artists_gv);
            mSimilarArtistsGv.setAdapter(new ArtistAdapter(this, similarArtists, new ArtistClickListener(this)));
        }
        else {
            findViewById(R.id.title_similars_tv).setVisibility(View.GONE);
        }

        mMoreSimilarBtn = (Button) findViewById(R.id.more_similar_btn);
        mMoreSimilarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreSimilars();
            }
        });

        mFullBioBtn = (Button) findViewById(R.id.full_bio_btn);
        mFullBioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFullBio();
            }
        });

        scrollToTopOfScreen();

        uiLoading(false);

    }

    private void openFullBio() {
        Bundle bundle = new Bundle();
        bundle.putString("bio", mArtist.getBio().getContent());
        bundle.putString("artistName", mArtist.getName());
        bundle.putString("artistTags", mArtist.getTagsAsString());

        Intent intent = new Intent(this, FullBioActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void loadArtistFromNetwork() {

        mDownloadAttempts++;

        if (mDownloadAttempts > 3) {
            downloadFailed();

            uiLoading(false);
            return;
        }

        uiLoading(true);

        Gson gsonArtist = new GsonBuilder()
                .registerTypeAdapter(Artist.class, new ArtistDeserializer())
                .create();

        Call<Artist> artistCall = new RetrofitInitializer(gsonArtist)
                .getArtistService()
                .getInfo(mExtras.getString("artistName"), RetrofitInitializer.LAST_FM_KEY, getString(R.string.lang));

        artistCall.enqueue(new Callback<Artist>() {

            @Override
            public void onResponse(Call<Artist> call, Response<Artist> response) {

                Artist artist = response.body();

                if (artist != null && !artist.getName().equals("")) {

                    setArtistUi(artist);

                } else {
                    loadArtistFromNetwork();
                }

            }

            @Override
            public void onFailure(Call<Artist> call, Throwable t) {
                loadArtistFromNetwork();
            }

        });

    }

    private void loadDataFromNetwork() {

        // Download Artist data
        loadArtistFromNetwork();

        // Download Top Tracks list
        Gson gsonTopTracks = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Track>>(){}.getType(), new TrackListDeserializer())
                .create();

        Call<List<Track>> topTracksCall = new RetrofitInitializer(gsonTopTracks)
                .getTracksService()
                .getTopTracks(mExtras.getString("artistName"), RetrofitInitializer.LAST_FM_KEY, 14);

        topTracksCall.enqueue(new Callback<List<Track>>() {
            @Override
            public void onResponse(Call<List<Track>> call, final Response<List<Track>> response) {
                ArtistActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTracksUi(response.body());
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Track>> call, Throwable t) {
                Log.e("Error", "onFailure: " + t.getMessage());
            }
        });

        // Download Top Albums list
        Gson gsonTopAlbums = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Album>>(){}.getType(), new AlbumDeserializer())
                .create();

        Call<List<Album>> topAlbumsCall = new RetrofitInitializer(gsonTopAlbums)
                .getAlbumsService()
                .getTopAlbums(mExtras.getString("artistName"), RetrofitInitializer.LAST_FM_KEY, 4);

        topAlbumsCall.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, final Response<List<Album>> response) {
                ArtistActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAlbumsUi(response.body());
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {
                Log.e("Error", "onFailure: " + t.getMessage());
            }
        });

    }

    private void loadDataFromInstanceState(Bundle savedInstanceState) {

        Artist artist = (Artist) savedInstanceState.getSerializable("artist");
        List<Track> topTracks = (List<Track>) savedInstanceState.getSerializable("topTracks");
        List<Album> topAlbums = (List<Album>) savedInstanceState.getSerializable("topAlbums");

        if (artist != null) {
            setArtistUi(artist);
        }

        else
            downloadFailed();

        if (topTracks != null) setTracksUi(topTracks);
        if (topAlbums != null) setAlbumsUi(topAlbums);


    }

    private void uiLoading(boolean loading) {

        LinearLayout layoutArtist = (LinearLayout) findViewById(R.id.layoutArtist);
        LinearLayout loadingArtist = (LinearLayout) findViewById(R.id.loading);

        if (loading) {
            mToolbarExtension.clear();
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("");
            layoutArtist.setVisibility(View.GONE);
            loadingArtist.setVisibility(View.VISIBLE);
        } else {
            layoutArtist.setVisibility(View.VISIBLE);
            loadingArtist.setVisibility(View.GONE);
        }

    }

    private void showMoreSimilars() {
        Bundle params = new Bundle();
        params.putString("artistName", this.mArtist.getName());
        params.putString("tags", this.mArtist.getTagsAsString());
        Intent intent = new Intent(this, AllSimilarArtistsActivity.class);
        intent.putExtras(params);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_youtube, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mArtist == null) return false;

        switch (item.getItemId()) {

            case R.id.google_play:
                youtubePlayStoreListener.openPlayStore(mArtist.getName());
                break;

            case R.id.youtube:
                youtubePlayStoreListener.openYoutube(mArtist.getName());
                break;

        }

        return true;

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("artist", this.mArtist);
        outState.putSerializable("topTracks", new ArrayList<>(this.mTopTracks));
        outState.putSerializable("topAlbums", new ArrayList<>(this.mTopAlbums));
    }
}