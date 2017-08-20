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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import newscavazzini.similarartists.*;
import newscavazzini.similarartists.adapters.ArtistAdapter;
import newscavazzini.similarartists.listeners.ArtistClickListener;
import newscavazzini.similarartists.models.artist.Artist;
import newscavazzini.similarartists.retrofit.RetrofitInitializer;
import newscavazzini.similarartists.retrofit.deserializer.TopArtistsDeserializer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private List<Artist> mTopArtists = new ArrayList<>();
    private int mDownloadAttempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) setSupportActionBar(toolbar);

        if (savedInstanceState != null)
            loadFromInstanceState(savedInstanceState);

        else
            loadFromNetwork();

    }

    private void tryAgain() {
        this.mDownloadAttempts = 0;

        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        findViewById(R.id.download_failed_ll).setVisibility(View.GONE);

        loadFromNetwork();
    }

    private void downloadFailed() {

        findViewById(R.id.loading).setVisibility(View.GONE);
        findViewById(R.id.download_failed_ll).setVisibility(View.VISIBLE);

        findViewById(R.id.try_again).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.try_again:
                tryAgain();
                break;

        }
    }

    private void setUi(List<Artist> topArtists) {

        findViewById(R.id.download_failed_ll).setVisibility(View.GONE);

        this.mTopArtists = topArtists;

        GridView topArtitsGv = (GridView) findViewById(R.id.artists_gv);
        topArtitsGv.setAdapter(new ArtistAdapter(this, topArtists, new ArtistClickListener(this)));

    }

    private void loadFromNetwork() {

        mDownloadAttempts++;

        if (mDownloadAttempts > 3) {
            downloadFailed();
            return;
        }

        Gson gsonTopArtists = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Artist>>(){}.getType(), new TopArtistsDeserializer())
                .create();

        Call<List<Artist>> topArtistsCall = new RetrofitInitializer(gsonTopArtists)
                .getArtistService()
                .getTopArtists(RetrofitInitializer.LAST_FM_KEY, 20);

        topArtistsCall.enqueue(new Callback<List<Artist>>() {

            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {

                List<Artist> topArtists = response.body();
                if (topArtists != null && topArtists.size() > 0) {

                    setUi(topArtists);
                    findViewById(R.id.loading).setVisibility(View.GONE);

                } else {

                    loadFromNetwork();

                }

            }

            @Override
            public void onFailure(Call<List<Artist>> call, Throwable t) {
                loadFromNetwork();
            }
        });
    }

    private void loadFromInstanceState(Bundle savedInstanceState) {

        List<Artist> topArtists = ((List<Artist>) savedInstanceState.getSerializable("topArtists"));

        if (topArtists != null && topArtists.size() > 0)
            setUi(topArtists);

        else
            downloadFailed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putSerializable("topArtists", new ArrayList<>(mTopArtists));
    }

}