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
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import newscavazzini.similarartists.R;
import newscavazzini.similarartists.adapters.ArtistAdapter;
import newscavazzini.similarartists.listeners.ArtistClickListener;
import newscavazzini.similarartists.models.artist.Artist;
import newscavazzini.similarartists.retrofit.RetrofitInitializer;
import newscavazzini.similarartists.retrofit.deserializer.SimilarArtistsDeserializer;
import newscavazzini.similarartists.ui.ToolbarExtension;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AllSimilarArtistsActivity extends AppCompatActivity implements View.OnClickListener {

    private String mArtistName;
    private String mTags;
    private List<Artist> mSimilarArtists = new ArrayList<>();
    private int mDownloadAttempts;
    private Bundle mExtras;

    private GridView mSimilarArtistsGv;
    private ToolbarExtension mToolbarExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_similars);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) setSupportActionBar(toolbar);

        mToolbarExtension = new ToolbarExtension(this);

        Intent intent = getIntent();

        if(intent != null && intent.getExtras() != null) {

            this.mExtras = intent.getExtras();

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
        String tags = savedInstanceState.getString("tags");
        List<Artist> similarArtists =
                ((List<Artist>) savedInstanceState.getSerializable("similarArtists"));

        if (artistName != null && !artistName.equals("") && tags != null &&
                !tags.equals("") && similarArtists != null && similarArtists.size() > 0) {

            loadUi(artistName, tags, similarArtists);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.try_again:
                tryAgain();
                break;

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

    private void loadFromNetwork() {

        if (this.mExtras.getString("artistName") == null || this.mExtras.getString("artistName").equals("") ||
                this.mExtras.getString("tags") == null || this.mExtras.getString("tags").equals("")) {

            finish();
            return;
        }

        mDownloadAttempts++;

        if (mDownloadAttempts > 3) {
            uiLoading(false);
            downloadFailed();
            return;
        }

        final String artistName = this.mExtras.getString("artistName");
        final String tags = this.mExtras.getString("tags");

        uiLoading(true);

        Gson gsonSimilarArtists = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Artist>>(){}.getType(), new SimilarArtistsDeserializer())
                .create();

        Call<List<Artist>> similarCall = new RetrofitInitializer(gsonSimilarArtists)
                .getArtistService()
                .getSimilar(artistName, RetrofitInitializer.LAST_FM_KEY, 24);

        similarCall.enqueue(new Callback<List<Artist>>() {

            @Override
            public void onResponse(Call<List<Artist>> call, Response<List<Artist>> response) {

                List<Artist> similarArtists = response.body();
                if (similarArtists != null && similarArtists.size() > 0) {

                    loadUi(artistName, tags, similarArtists);
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

    private void loadUi(String artistName, String tags, List<Artist> similarArtists) {

        findViewById(R.id.download_failed_ll).setVisibility(View.GONE);

        mArtistName = artistName;
        mTags = tags;
        mSimilarArtists = similarArtists;

        if (getSupportActionBar() != null) getSupportActionBar().setTitle(R.string.similar_artists_to);

        mToolbarExtension.show(R.drawable.ic_person_white, artistName, tags);

        mSimilarArtistsGv = (GridView) findViewById(R.id.artists_gv);
        mSimilarArtistsGv.setAdapter(new ArtistAdapter(this, similarArtists, new ArtistClickListener(this)));

    }

    private void scrollToTopOfScreen() {
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollviewMain);
        scrollView.smoothScrollTo(0, 0);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putString("artistName", mArtistName);
        bundle.putString("tags", mTags);
        bundle.putSerializable("similarArtists", new ArrayList<>(mSimilarArtists));
    }

}