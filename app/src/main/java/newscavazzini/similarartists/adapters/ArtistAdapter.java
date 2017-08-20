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

package newscavazzini.similarartists.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import newscavazzini.similarartists.R;
import newscavazzini.similarartists.listeners.ArtistClickListener;
import newscavazzini.similarartists.models.artist.Artist;

public class ArtistAdapter extends BaseAdapter {

    private Context context;
    private List<Artist> artists;
    private ArtistClickListener listener;

    public ArtistAdapter(Context context, List<Artist> artists, ArtistClickListener listener){
        this.context = context;
        this.artists = artists;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return artists.size();
    }

    @Override
    public Object getItem(int position) {
        return artists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Artist artist = artists.get(position);
        View layout;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            layout = inflater.inflate(R.layout.row_artist, parent, false);

        } else {
            layout = convertView;
        }

        TextView artistNameTv = (TextView) layout.findViewById(R.id.artist_name_tv);
        artistNameTv.setText(artist.getName());

        LinearLayout clickableArtistLl = (LinearLayout) layout.findViewById(R.id.artist_click_ll);
        clickableArtistLl.setTag(artist.getName());
        clickableArtistLl.setOnClickListener(this.listener);

        return layout;

    }

}