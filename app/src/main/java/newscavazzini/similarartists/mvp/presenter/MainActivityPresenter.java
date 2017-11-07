package newscavazzini.similarartists.mvp.presenter;

import android.os.Bundle;

import java.util.List;

import newscavazzini.similarartists.models.artist.Artist;
import newscavazzini.similarartists.mvp.model.ArtistRepository;
import newscavazzini.similarartists.mvp.model.NetworkArtistRepository;
import newscavazzini.similarartists.mvp.view.MainActivityView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivityPresenter {

    private MainActivityView view;
    private ArtistRepository artistRepository;

    public MainActivityPresenter(MainActivityView view) {
        this.view = view;
        artistRepository = new NetworkArtistRepository();
    }

    public void loadTopArtists(Bundle savedInstanceState) {

        if (savedInstanceState == null) {

            artistRepository.getTopArtists(new Callback<List<Artist>>() {
                @Override
                public void onResponse(Call<List<Artist>> call,
                                       Response<List<Artist>> response) {

                    if (response.body().size() > 0) {
                        view.showArtists(response.body());
                    }
                    else {
                        view.showError();
                    }
                }

                @Override
                public void onFailure(Call<List<Artist>> call, Throwable t) {
                    view.showError();
                }
            });

        }
        else {

            List<Artist> artistList = ((List<Artist>) savedInstanceState
                    .getSerializable("topArtists"));

            if (artistList != null && artistList.size() > 0) {
                view.showArtists(artistList);
            }
            else {
                view.showError();
            }

        }

    }

    public void tryAgain() {
        loadTopArtists(null);
    }
}
