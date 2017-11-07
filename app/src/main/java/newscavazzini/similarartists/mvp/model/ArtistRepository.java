package newscavazzini.similarartists.mvp.model;

import android.os.Bundle;

import java.util.List;

import newscavazzini.similarartists.models.album.Album;
import newscavazzini.similarartists.models.artist.Artist;
import newscavazzini.similarartists.models.track.Track;
import retrofit2.Callback;

public interface ArtistRepository {
    void getTopArtists(Callback<List<Artist>> callback);
    void getArtistInfo(Bundle mExtras, Callback<Artist> callback);
    void getTopTracksOfArtist(Bundle extras, Callback<List<Track>> callback);
    void getTopAlbumsOfArtist(Bundle extras, Callback<List<Album>> callback);
}
