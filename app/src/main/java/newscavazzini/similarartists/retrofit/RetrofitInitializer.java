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

package newscavazzini.similarartists.retrofit;

import com.google.gson.Gson;

import newscavazzini.similarartists.retrofit.services.AlbumService;
import newscavazzini.similarartists.retrofit.services.ArtistService;
import newscavazzini.similarartists.retrofit.services.TrackService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInitializer {

    public static final String LAST_FM_KEY = "LAST-FM-KEY-HERE";
    private final Retrofit retrofit;

    public RetrofitInitializer(Gson gson) {

        this.retrofit = new Retrofit.Builder()
                            .baseUrl("http://ws.audioscrobbler.com/2.0/")
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

    }

    public ArtistService getArtistService() {
        return retrofit.create(ArtistService.class);
    }

    public TrackService getTracksService() {
        return retrofit.create(TrackService.class);
    }

    public AlbumService getAlbumsService() {
        return retrofit.create(AlbumService.class);
    }

}
