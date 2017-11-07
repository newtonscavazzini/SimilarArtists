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
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import newscavazzini.similarartists.R;
import newscavazzini.similarartists.mvp.presenter.SettingsActivityPresenter;
import newscavazzini.similarartists.mvp.view.SettingsActivityView;


public class SettingsActivity extends AppCompatActivity
        implements View.OnClickListener, SettingsActivityView {

    private SettingsActivityPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        presenter = new SettingsActivityPresenter(this);

        this.presenter.showAppVersion();

        findViewById(R.id.github_btn).setOnClickListener(this);
        findViewById(R.id.lastfm_btn).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        SettingsActivity.this.presenter.openLink(v.getTag().toString());
    }

    @Override
    public void showAppVersion(String version) {
        TextView versionTv = (TextView) findViewById(R.id.version_tv);
        versionTv.setText(version);
    }

    @Override
    public void openWebsite(String url){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

}