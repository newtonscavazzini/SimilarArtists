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
import newscavazzini.similarartists.listeners.TrackClickListener;
import newscavazzini.similarartists.models.track.Track;

public class TracksAdapter extends BaseAdapter {

    private Context context;
    private List<Track> tracks;
    private TrackClickListener listener;

    public TracksAdapter(Context context, List<Track> tracks, TrackClickListener listener){
        this.context = context;
        this.tracks = tracks;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Object getItem(int position) {
        return tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Track track = tracks.get(position);
        View layout;

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            layout = inflater.inflate(R.layout.row_track, parent, false);

        } else {
            layout = convertView;
        }

        TextView trackNameTv = (TextView) layout.findViewById(R.id.track_name_tv);
        trackNameTv.setText(track.getName());

        LinearLayout clickableTrackLl = (LinearLayout) layout.findViewById(R.id.track_click_ll);
        clickableTrackLl.setTag(track.getName());
        clickableTrackLl.setOnClickListener(listener);

        return layout;

    }

}