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

package newscavazzini.similarartists.listeners;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import newscavazzini.similarartists.activities.AlbumActivity;


public class AlbumClickListener implements View.OnClickListener {

    private Context context;
    private String artistName;

    public AlbumClickListener(Context context, String artistName) {
        this.context = context;
        this.artistName = artistName;
    }

    @Override
    public void onClick(View v) {

        if (v.getTag() != null) {
            Bundle params = new Bundle();
            params.putString("artistName", artistName);
            params.putString("albumName", v.getTag().toString());
            Intent intent = new Intent(context, AlbumActivity.class);
            intent.putExtras(params);
            context.startActivity(intent);
        }

    }

}