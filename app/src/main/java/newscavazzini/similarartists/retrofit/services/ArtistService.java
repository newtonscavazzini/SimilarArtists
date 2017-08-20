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

package newscavazzini.similarartists.retrofit.services;

import java.util.List;

import newscavazzini.similarartists.models.artist.Artist;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ArtistService {

    @GET("?method=artist.getinfo&format=json")
    Call<Artist> getInfo(@Query("artist") String artistName,
                         @Query("api_key") String apiKey,
                         @Query("lang") String lang);

    @GET("?method=artist.getsimilar&format=json")
    Call<List<Artist>> getSimilar(@Query("artist") String artistName,
                                  @Query("api_key") String apiKey,
                                  @Query("limit") int limit);

    @GET("?method=artist.search&format=json")
    Call<List<Artist>> getSearch(@Query("artist") String artistName,
                              @Query("api_key") String apiKey);

    @GET("?method=chart.gettopartists&format=json")
    Call<List<Artist>> getTopArtists(@Query("api_key") String apiKey,
                                     @Query("limit") int limit);

}
