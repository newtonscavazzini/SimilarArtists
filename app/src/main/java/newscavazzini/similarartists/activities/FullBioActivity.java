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

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import newscavazzini.similarartists.R;
import newscavazzini.similarartists.ui.ToolbarExtension;

public class FullBioActivity extends AppCompatActivity {

    private TextView mFullBioTv;
    private ToolbarExtension mToolbarExtension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_bio);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null) setSupportActionBar(toolbar);

        mToolbarExtension = new ToolbarExtension(this);

        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {

            finish();

        } else {

            if(getSupportActionBar() != null) {
                getSupportActionBar().setTitle(bundle.getString("artistName") + "'s Bio");
            }

            mFullBioTv = (TextView) findViewById(R.id.full_bio_tv);
            mFullBioTv.setText(bundle.getString("bio"));

            mToolbarExtension.show(R.drawable.ic_person_white,
                    bundle.getString("artistName"),
                    bundle.getString("artistTags"));

        }

    }

}
