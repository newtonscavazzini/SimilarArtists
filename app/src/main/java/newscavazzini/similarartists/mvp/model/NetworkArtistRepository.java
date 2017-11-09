package newscavazzini.similarartists.mvp.model;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Locale;

import newscavazzini.similarartists.models.album.Album;
import newscavazzini.similarartists.models.artist.Artist;
import newscavazzini.similarartists.models.track.Track;
import newscavazzini.similarartists.retrofit.RetrofitInitializer;
import newscavazzini.similarartists.retrofit.deserializer.AlbumDeserializer;
import newscavazzini.similarartists.retrofit.deserializer.ArtistDeserializer;
import newscavazzini.similarartists.retrofit.deserializer.SearchDeserializer;
import newscavazzini.similarartists.retrofit.deserializer.TopArtistsDeserializer;
import newscavazzini.similarartists.retrofit.deserializer.TrackListDeserializer;
import retrofit2.Call;
import retrofit2.Callback;

public class NetworkArtistRepository implements ArtistRepository {

    @Override
    public void getTopArtists(Callback<List<Artist>> callback) {

        Gson gsonTopArtists = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Artist>>(){}.getType(),
                        new TopArtistsDeserializer())
                .create();

        Call<List<Artist>> topArtistsCall = new RetrofitInitializer(gsonTopArtists)
                .getArtistService()
                .getTopArtists(RetrofitInitializer.LAST_FM_KEY, 20);

        topArtistsCall.enqueue(callback);

    }

    @Override
    public void getArtistInfo(Bundle mExtras, Callback<Artist> callback) {

        Gson gsonArtist = new GsonBuilder()
                .registerTypeAdapter(Artist.class, new ArtistDeserializer())
                .create();

        Call<Artist> artistCall = new RetrofitInitializer(gsonArtist)
                .getArtistService()
                .getInfo(mExtras.getString("artistName"),
                        RetrofitInitializer.LAST_FM_KEY,
                        Locale.getDefault().getLanguage());
                        //getString(R.string.lang));

        artistCall.enqueue(callback);

    }

    @Override
    public void getTopTracksOfArtist(Bundle extras, Callback<List<Track>> callback) {

        Gson gsonTopTracks = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Track>>(){}.getType(),
                        new TrackListDeserializer())
                .create();

        Call<List<Track>> topTracksCall = new RetrofitInitializer(gsonTopTracks)
                .getTracksService()
                .getTopTracks(extras.getString("artistName"),
                        RetrofitInitializer.LAST_FM_KEY, 14);

        topTracksCall.enqueue(callback);
    }

    @Override
    public void getTopAlbumsOfArtist(Bundle extras, Callback<List<Album>> callback) {
        Gson gsonTopAlbums = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Album>>(){}.getType(),
                        new AlbumDeserializer())
                .create();

        Call<List<Album>> topAlbumsCall = new RetrofitInitializer(gsonTopAlbums)
                .getAlbumsService()
                .getTopAlbums(extras.getString("artistName"),
                        RetrofitInitializer.LAST_FM_KEY, 4);

        topAlbumsCall.enqueue(callback);
    }

    @Override
    public void searchArtist(String searchTerm, Callback<List<Artist>> callback) {

        Gson gsonSearchArtist = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Artist>>(){}.getType(),
                        new SearchDeserializer())
                .create();

        Call<List<Artist>> searchCall = new RetrofitInitializer(gsonSearchArtist)
                .getArtistService()
                .getSearch(searchTerm, RetrofitInitializer.LAST_FM_KEY);

        searchCall.enqueue(callback);
    }

}
