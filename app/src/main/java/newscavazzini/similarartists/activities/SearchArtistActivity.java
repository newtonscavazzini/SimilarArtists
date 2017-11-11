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
import android.widget.TextView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import newscavazzini.similarartists.R;
import newscavazzini.similarartists.adapters.ArtistAdapter;
import newscavazzini.similarartists.listeners.ArtistClickListener;
import newscavazzini.similarartists.models.artist.Artist;
import newscavazzini.similarartists.mvp.presenter.SearchArtistActivityPresenter;
import newscavazzini.similarartists.mvp.view.SearchArtistActivityView;
import newscavazzini.similarartists.ui.ToolbarExtension;


public class SearchArtistActivity extends AppCompatActivity
        implements View.OnClickListener, SearchArtistActivityView {

    private SearchArtistActivityPresenter presenter;
    private String mSearchTerm;
    private List<Artist> mSimilarArtists = new ArrayList<>();

    private ToolbarExtension mToolbarExtension;
    private LinearLayout mDownloadFailedLl;
    private LinearLayout mLoadingLl;
    private TextView mErrorMessageTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        this.mDownloadFailedLl = (LinearLayout) findViewById(R.id.download_failed_ll);
        this.mLoadingLl = (LinearLayout) findViewById(R.id.loadingList);
        mErrorMessageTv = (TextView) findViewById(R.id.error_message_tv);
        this.mToolbarExtension = new ToolbarExtension(this);

        if (!Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            finish();
            return;
        }

        mSearchTerm = (Normalizer.normalize(getIntent().getStringExtra(
                SearchManager.QUERY), Normalizer.Form.NFD)).trim();

        this.presenter = new SearchArtistActivityPresenter(this);
        this.presenter.searchArtist(mSearchTerm, savedInstanceState);

        findViewById(R.id.try_again).setOnClickListener(this);

    }

    @Override
    public void showResults(List<Artist> artistsFound) {

        scrollToTopOfScreen();
        this.mSimilarArtists = artistsFound;

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.searching_for);

        this.mToolbarExtension.show(R.drawable.ic_search_white,
                mSearchTerm, null);

        GridView mSimilarArtistsGv = (GridView) findViewById(R.id.artists_gv);
        mSimilarArtistsGv.setAdapter(new ArtistAdapter(this, artistsFound,
                        new ArtistClickListener(this)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.try_again:
                SearchArtistActivity.this.presenter.tryAgain(mSearchTerm);
                break;

        }
    }

    @Override
    public void displayLoadingLayout() {
        this.mLoadingLl.setVisibility(View.VISIBLE);
        this.mDownloadFailedLl.setVisibility(View.GONE);
    }

    @Override
    public void displayErrorLayout() {
        this.mLoadingLl.setVisibility(View.GONE);
        this.mDownloadFailedLl.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayErrorLayout(String message) {
        this.mErrorMessageTv.setText(message);
        displayErrorLayout();
    }

    @Override
    public void displayResultsLayout() {
        this.mLoadingLl.setVisibility(View.GONE);
        this.mDownloadFailedLl.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle){
        super.onSaveInstanceState(bundle);
        bundle.putSerializable("similarArtists", new ArrayList<>(mSimilarArtists));
    }

    private void scrollToTopOfScreen() {
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollview_main);
        scrollView.smoothScrollTo(0, 0);
    }

}