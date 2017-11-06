package newscavazzini.similarartists.mvp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import newscavazzini.similarartists.models.artist.Artist;
import newscavazzini.similarartists.retrofit.RetrofitInitializer;
import newscavazzini.similarartists.retrofit.deserializer.TopArtistsDeserializer;
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

}
