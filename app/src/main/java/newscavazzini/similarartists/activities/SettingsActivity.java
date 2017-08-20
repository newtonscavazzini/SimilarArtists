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
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import newscavazzini.similarartists.R;


public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) setSupportActionBar(toolbar);

        findViewById(R.id.github_btn).setOnClickListener(this);
        findViewById(R.id.lastfm_btn).setOnClickListener(this);

        try {

            TextView versionNameTv = (TextView) findViewById(R.id.version_tv);
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionNameTv.setText(version);

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    private void openWebsite(String url){
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(launchBrowser);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.lastfm_btn) openWebsite("http://www.lastfm.com/");
        if (v.getId() == R.id.github_btn) openWebsite("https://github.com/newtonscavazzini/SimilarArtists/");
    }

}