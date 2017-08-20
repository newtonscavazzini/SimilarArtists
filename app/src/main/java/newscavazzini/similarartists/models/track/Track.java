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

package newscavazzini.similarartists.models.track;

import java.io.Serializable;

import newscavazzini.similarartists.models.album.Album;
import newscavazzini.similarartists.models.artist.Artist;

public class Track implements Serializable {

    private Artist artist;
    private String name;
    private String playcount;
    private String listeners;
    private String mbid;
    private String duration;
    private Album album;
    private Wiki wiki;

    public Artist getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }

    public String getPlaycount() {
        return playcount;
    }

    public String getListeners() {
        return listeners;
    }

    public String getMbid() {
        return mbid;
    }

    public String getDuration() {
        return duration;
    }

    public Album getAlbum() {
        return album;
    }

    public Wiki getWiki() {
        return wiki;
    }
}
