package newscavazzini.similarartists.mvp.model;

import java.util.List;

import newscavazzini.similarartists.models.artist.Artist;
import retrofit2.Callback;

public interface ArtistRepository {
    void getTopArtists(Callback<List<Artist>> callback);
}
