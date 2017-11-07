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
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import newscavazzini.similarartists.R;
import newscavazzini.similarartists.adapters.AlbumsAdapter;
import newscavazzini.similarartists.adapters.ArtistAdapter;
import newscavazzini.similarartists.adapters.TracksAdapter;
import newscavazzini.similarartists.listeners.AlbumClickListener;
import newscavazzini.similarartists.listeners.ArtistClickListener;
import newscavazzini.similarartists.listeners.TrackClickListener;
import newscavazzini.similarartists.listeners.YoutubePlayStoreClickListener;
import newscavazzini.similarartists.models.album.Album;
import newscavazzini.similarartists.models.artist.Artist;
import newscavazzini.similarartists.models.track.Track;
import newscavazzini.similarartists.mvp.presenter.ArtistActivityPresenter;
import newscavazzini.similarartists.mvp.view.ArtistActivityView;
import newscavazzini.similarartists.ui.ToolbarExtension;

public class ArtistActivity extends AppCompatActivity implements ArtistActivityView {

    private ArtistActivityPresenter presenter;

    private Bundle mExtras;
    private Artist mArtist;
    private List<Track> mTopTracks = new ArrayList<>();
    private List<Album> mTopAlbums = new ArrayList<>();
    private YoutubePlayStoreClickListener youtubePlayStoreListener;

    private ToolbarExtension mToolbarExtension;
    private LinearLayout mLoadingLl;
    private LinearLayout mDownloadFailedLl;
    private LinearLayout mArtistLl;
    private TextView mTopAlbumsTitleTv;
    private TextView mTopTracksTitleTv;
    private TextView mSimilarTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        this.mLoadingLl = (LinearLayout) findViewById(R.id.loading);
        this.mDownloadFailedLl = (LinearLayout) findViewById(R.id.download_failed_ll);
        this.mArtistLl = (LinearLayout) findViewById(R.id.layoutArtist);
        this.mTopAlbumsTitleTv = (TextView) findViewById(R.id.title_top_albums_tv);
        this.mTopTracksTitleTv = (TextView) findViewById(R.id.title_top_tracks_tv);
        this.mSimilarTitleTv = (TextView) findViewById(R.id.title_similars_tv);

        mToolbarExtension = new ToolbarExtension(this);
        youtubePlayStoreListener = new YoutubePlayStoreClickListener(this);

        this.mExtras = getIntent().getExtras();

        this.presenter = new ArtistActivityPresenter(this);
        this.presenter.loadArtistInfo(getIntent().getExtras(), savedInstanceState);

        findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArtistActivity.this.uiLoading(true);
                ArtistActivity.this.presenter.tryAgain(getIntent().getExtras());
            }
        });

    }

    @Override
    public void showArtistInfo(Artist artist) {

        this.mDownloadFailedLl.setVisibility(View.GONE);

        this.mArtist = artist;

        if(getSupportActionBar() != null) getSupportActionBar().setTitle(artist.getName());

        mToolbarExtension.show(R.drawable.ic_person_white, artist.getName(),
                artist.getTagsAsString());

        TextView mArtistBioTv = (TextView) findViewById(R.id.artist_bio_tv);
        mArtistBioTv.setText(artist.getBio().getSummary());
        if (artist.getBio().getSummary().equals(""))
            findViewById(R.id.title_about_tv).setVisibility(View.GONE);

        Button mMoreSimilarBtn = (Button) findViewById(R.id.more_similar_btn);
        mMoreSimilarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.showMoreSimilars(ArtistActivity.this.mArtist);
            }
        });

        Button mFullBioBtn = (Button) findViewById(R.id.full_bio_btn);
        mFullBioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.openFullBio(ArtistActivity.this.mArtist);
            }
        });

        scrollToTopOfScreen();
        uiLoading(false);

    }

    @Override
    public void artistNotFound() {
        this.mLoadingLl.setVisibility(View.GONE);
        this.mDownloadFailedLl.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSimilarArtists(List<Artist> similarArtists) {

        this.mSimilarTitleTv.setVisibility(View.VISIBLE);

        GridView mSimilarArtistsGv = (GridView) findViewById(R.id.artists_gv);
        mSimilarArtistsGv.setAdapter(
                new ArtistAdapter(this, similarArtists,
                        new ArtistClickListener(this)));

    }

    @Override
    public void similarArtistsNotFound() {
        this.mSimilarTitleTv.setVisibility(View.GONE);
    }

    @Override
    public void showAlbums(List<Album> topAlbums) {

        this.mTopAlbums = topAlbums;

        this.mTopAlbumsTitleTv.setVisibility(View.VISIBLE);

        GridView mTopAlbumsGv = (GridView) findViewById(R.id.albums_gv);
        mTopAlbumsGv.setAdapter(new AlbumsAdapter(this, topAlbums,
                new AlbumClickListener(this, mExtras.getString("artistName"))));

    }

    @Override
    public void albumsNotFound() {
        this.mTopAlbumsTitleTv.setVisibility(View.GONE);
    }

    @Override
    public void showTracks(List<Track> topTracks) {

        this.mTopTracks = topTracks;

        this.mTopTracksTitleTv.setVisibility(View.VISIBLE);
        GridView mTopTracksGv = (GridView) findViewById(R.id.tracks_gv);
        mTopTracksGv.setAdapter(new TracksAdapter(this, topTracks,
                new TrackClickListener(this, mExtras.getString("artistName"))));


    }

    @Override
    public void tracksNotFound() {
        this.mTopTracksTitleTv.setVisibility(View.GONE);
    }

    @Override
    public void openFullBio(Bundle bundle) {
        Intent intent = new Intent(this, FullBioActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void showMoreSimilars(Bundle bundle) {
        Intent intent = new Intent(this, AllSimilarArtistsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void openPlayStore(String artistName) {
        this.youtubePlayStoreListener.openPlayStore(artistName);
    }

    @Override
    public void openYoutube(String artistName) {
        this.youtubePlayStoreListener.openYoutube(artistName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_youtube, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return this.presenter.openMenuItem(item.getTitle().toString(), this.mArtist);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("artist", this.mArtist);
        outState.putSerializable("topTracks", new ArrayList<>(this.mTopTracks));
        outState.putSerializable("topAlbums", new ArrayList<>(this.mTopAlbums));
    }

    private void scrollToTopOfScreen() {
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollview_main);
        scrollView.smoothScrollTo(0, 0);
    }

    private void uiLoading(boolean loading) {

        if (loading) {
            mToolbarExtension.clear();
            this.mArtistLl.setVisibility(View.GONE);
            this.mDownloadFailedLl.setVisibility(View.GONE);
            this.mLoadingLl.setVisibility(View.VISIBLE);
        }
        else {
            this.mArtistLl.setVisibility(View.VISIBLE);
            this.mLoadingLl.setVisibility(View.GONE);
        }

    }

}