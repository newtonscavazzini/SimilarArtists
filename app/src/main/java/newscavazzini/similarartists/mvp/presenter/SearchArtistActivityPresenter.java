package newscavazzini.similarartists.mvp.presenter;

import android.os.Bundle;

import java.util.List;

import newscavazzini.similarartists.models.artist.Artist;
import newscavazzini.similarartists.mvp.model.ArtistRepository;
import newscavazzini.similarartists.mvp.model.NetworkArtistRepository;
import newscavazzini.similarartists.mvp.view.SearchArtistActivityView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SearchArtistActivityPresenter {

    private SearchArtistActivityView view;
    private ArtistRepository artistRepository;

    public SearchArtistActivityPresenter(SearchArtistActivityView view) {
        this.view = view;
        this.artistRepository = new NetworkArtistRepository();
    }

    public void searchArtist(String searchTerm, Bundle savedInstanceState) {

        view.displayLoadingLayout();

        if (savedInstanceState == null) {

            artistRepository.searchArtist(searchTerm, new Callback<List<Artist>>() {
                @Override
                public void onResponse(Call<List<Artist>> call,
                                       Response<List<Artist>> response) {

                    if (response.isSuccessful()) {
                        view.showResults(response.body());
                        view.displayResultsLayout();
                    }
                    else {
                        view.displayErrorLayout();
                    }
                }

                @Override
                public void onFailure(Call<List<Artist>> call, Throwable t) {
                    view.displayErrorLayout();
                }
            });

        }
        else {

            List<Artist> artistList = ((List<Artist>) savedInstanceState
                    .getSerializable("similarArtists"));

            if (artistList != null && artistList.size() > 0) {
                view.showResults(artistList);
                view.displayResultsLayout();
            }
            else {
                view.displayErrorLayout();
            }

        }

    }

    public void tryAgain(String searchTerm) {
        view.displayLoadingLayout();
        searchArtist(searchTerm, null);
    }
}
