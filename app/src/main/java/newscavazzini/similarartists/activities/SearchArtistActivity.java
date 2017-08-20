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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import newscavazzini.similarartists.R;
import newscavazzini.similarartists.adapters.ArtistAdapter;
import newscavazzini.similarartists.listeners.ArtistClickListener;
import newscavazzini.similarartists.models.artist.Artist;
import newscavazzini.similarartists.retrofit.RetrofitInitializer;
import newscavazzini.similarartists.retrofit.deserializer.SearchDeserializer;
import newscavazzini.similarartists.ui.ToolbarExtension;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchArtistActivity extends AppCompatActivity implements View.OnClickListener {
    
    private String mSearchTerm;
    private String mArtistName;
    private List<Artist> mSimilarArtists = new ArrayList<>();
    private int mDownloadAttempts;

    private GridView mSimilarArtistsGv;
    private ToolbarExtension mToolbarExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

        mToolbarExtension = new ToolbarExtension(this);

        Intent intent = getIntent();

        if (!Intent.ACTION_SEARCH.equals(intent.getAction())) {
            finish();
            return;
        }

        mSearchTerm = (Normalizer.normalize(intent.getStringExtra(SearchManager.QUERY), Normalizer.Form.NFD)).trim();

        if(intent.getExtras() != null) {

            if (savedInstanceState != null) {

                loadFromInstanceState(savedInstanceState);
                scrollToTopOfScreen();

            } else {

                loadFromNetwork();
                scrollToTopOfScreen();

            }

        } else {
            finish();
        }

    }

    private void loadFromInstanceState(Bundle savedInstanceState) {

        String artistName = savedInstanceState.getString("artistName");
        List<Artist> similarArtists = ((List<Artist>) savedInstanceState.getSerializable("similarArtists"));

        if (artistName != null && !artistName.equals("")
                && similarArtists != null && similarArtists.size() > 0) {

            loadUi(artistName, similarArtists);

        } else {
            downloadFailed();
        }

    }

    private void uiLoading(boolean loading) {

        LinearLayout loadingList = (LinearLayout) findViewById(R.id.loadingList);

        if (loading) {
            mToolbarExtension.clear();
            if (getSupportActionBar() != null) getSupportActionBar().setTitle("");
            loadingList.setVisibility(View.VISIBLE);

        } else {
            loadingList.setVisibility(View.GONE);
        }

    }

    private void tryAgain() {
        mDownloadAttempts = 0;

        LinearLayout progress = (LinearLayout) findViewById(R.id.loadingList);
        progress.setVisibility(View.VISIBLE);

        View emptyBox = findViewById(R.id.download_failed_ll);
        emptyBox.setVisibility(View.GONE);

        loadFromNetwork();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.try_again:
                tryAgain();
                break;

        }
    }

    private void loadFromNetwork() {

        if (mSearchTerm.equals("")) {
            finish();
            return;
        }

        mDownloadAttempts++;

        if (mDownloadAttempts > 3) {

            downloadFailed();
            findViewById(R.id.loadingList).setVisibility(View.GONE);

            return;
        }

        final String artistName = mSearchTerm;

        uiLoading(true);

        Gson gsonSearchArtist = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Artist>>(){}.getType(), new SearchDeserializer())
                .create();

        Call<List<Artist>> searchCall = new RetrofitInitializer(gsonSearchArtist)
                .getArtistService()
                .getSearch(artistName, RetrofitInitializer.LAST_FM_KEY);

        searchCall.enqueue(new Callback<List<Artist>>() {

            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {

                List<Artist> similarArtists = response.body();
                if (similarArtists != null && similarArtists.size() > 0) {

                    loadUi(artistName, similarArtists);
                    uiLoading(false);

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

    private void downloadFailed() {

        findViewById(R.id.download_failed_ll).setVisibility(View.VISIBLE);
        findViewById(R.id.try_again).setOnClickListener(this);

    }

    private void loadUi(String artistName, List<Artist> similarArtists) {

        mArtistName = artistName;
        mSimilarArtists = similarArtists;

        findViewById(R.id.download_failed_ll).setVisibility(View.GONE);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.searching_for);

        mToolbarExtension.show(R.drawable.ic_search_white, artistName, null);

        mSimilarArtistsGv = (GridView) findViewById(R.id.artists_gv);
        mSimilarArtistsGv.setAdapter(new ArtistAdapter(this, similarArtists, new ArtistClickListener(this)));

    }

    private void scrollToTopOfScreen() {
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollview_main);
        scrollView.smoothScrollTo(0, 0);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putString("artistName", mArtistName);
        bundle.putSerializable("similarArtists", new ArrayList<>(mSimilarArtists));
    }
}