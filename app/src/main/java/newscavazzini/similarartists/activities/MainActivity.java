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

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import newscavazzini.similarartists.*;
import newscavazzini.similarartists.adapters.ArtistAdapter;
import newscavazzini.similarartists.listeners.ArtistClickListener;
import newscavazzini.similarartists.models.artist.Artist;
import newscavazzini.similarartists.mvp.presenter.MainActivityPresenter;
import newscavazzini.similarartists.mvp.view.MainActivityView;

public class MainActivity extends AppCompatActivity implements MainActivityView {

    private MainActivityPresenter presenter;
    private List<Artist> mTopArtists = new ArrayList<>();
    private LinearLayout mDownloadFailedLl;
    private LinearLayout mLoadingLl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDownloadFailedLl = (LinearLayout) findViewById(R.id.download_failed_ll);
        mLoadingLl = (LinearLayout) findViewById(R.id.loading);

        presenter = new MainActivityPresenter(this);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        presenter.loadTopArtists(savedInstanceState);

        findViewById(R.id.try_again).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.mLoadingLl.setVisibility(View.VISIBLE);
                MainActivity.this.mDownloadFailedLl.setVisibility(View.GONE);
                MainActivity.this.presenter.loadTopArtists(null);
            }
        });

    }

    @Override
    public void showArtists(List<Artist> artists) {

        this.mDownloadFailedLl.setVisibility(View.GONE);
        this.mTopArtists = artists;

        GridView topArtitsGv = (GridView) findViewById(R.id.artists_gv);
        topArtitsGv.setAdapter(new ArtistAdapter(
                this, artists, new ArtistClickListener(this)));

    }

    @Override
    public void showError() {
        this.mLoadingLl.setVisibility(View.GONE);
        this.mDownloadFailedLl.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putSerializable("topArtists", new ArrayList<>(this.mTopArtists));
    }

}