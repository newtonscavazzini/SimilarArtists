package newscavazzini.similarartists.mvp.presenter;

import android.os.Bundle;

import java.util.List;

import newscavazzini.similarartists.models.album.Album;
import newscavazzini.similarartists.models.artist.Artist;
import newscavazzini.similarartists.models.track.Track;
import newscavazzini.similarartists.mvp.model.ArtistRepository;
import newscavazzini.similarartists.mvp.model.NetworkArtistRepository;
import newscavazzini.similarartists.mvp.view.ArtistActivityView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArtistActivityPresenter {

    private ArtistActivityView view;
    private ArtistRepository artistRepository;

    public ArtistActivityPresenter(ArtistActivityView view) {
        this.view = view;
        artistRepository = new NetworkArtistRepository();
    }

    public void loadArtistInfo(Bundle extras, Bundle savedInstanceState) {

        if(extras != null) {

            if(savedInstanceState != null) {

                Artist artist = (Artist) savedInstanceState
                        .getSerializable("artist");

                if (artist != null) {
                    view.showArtistInfo(artist);
                    view.showSimilarArtists(artist.getSimilar().getArtists());
                }
                else {
                    view.artistNotFound();
                    view.similarArtistsNotFound();
                }

                List<Track> topTracks = (List<Track>)
                        savedInstanceState.getSerializable("topTracks");

                if (topTracks != null) {
                    view.showTracks(topTracks);
                } else {
                    view.tracksNotFound();
                }

                List<Album> topAlbums = (List<Album>)
                        savedInstanceState.getSerializable("topAlbums");

                if (topAlbums != null) {
                    view.showAlbums(topAlbums);
                } else {
                    view.albumsNotFound();
                }


            } else {

                artistRepository.getArtistInfo(extras, new Callback<Artist>() {
                    @Override
                    public void onResponse(Call<Artist> call, Response<Artist> response) {
                        if (response.isSuccessful()) {
                            view.showArtistInfo(response.body());
                            view.showSimilarArtists(response.body().getSimilar().getArtists());
                        }
                    }

                    @Override
                    public void onFailure(Call<Artist> call, Throwable t) {
                        view.artistNotFound();
                    }
                });

                artistRepository.getTopTracksOfArtist(extras, new Callback<List<Track>>() {
                    @Override
                    public void onResponse(Call<List<Track>> call, Response<List<Track>> response) {
                        if (response.isSuccessful()) {
                            view.showTracks(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Track>> call, Throwable t) {
                        view.tracksNotFound();
                    }
                });

                artistRepository.getTopAlbumsOfArtist(extras, new Callback<List<Album>>() {
                    @Override
                    public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                        if (response.isSuccessful()) {
                            view.showAlbums(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Album>> call, Throwable t) {
                        view.albumsNotFound();
                    }
                });

            }

        } else {
            view.artistNotFound();
        }

    }

    public void openFullBio(Artist artist) {
        Bundle bundle = new Bundle();
        bundle.putString("bio", artist.getBio().getContent());
        bundle.putString("artistName", artist.getName());
        bundle.putString("artistTags", artist.getTagsAsString());

        view.openFullBio(bundle);
    }

    public void showMoreSimilars(Artist artist) {
        Bundle bundle = new Bundle();
        bundle.putString("artistName", artist.getName());
        bundle.putString("tags", artist.getTagsAsString());

        view.showMoreSimilars(bundle);
    }

    public boolean openMenuItem(String itemTitle, Artist artist) {

        if (artist != null) {

            switch (itemTitle) {

                case "Google Play":
                    view.openPlayStore(artist.getName());
                    return true;

                case "Youtube":
                    view.openYoutube(artist.getName());
                    return true;

            }
        }
        return false;
    }

    public void tryAgain(Bundle extras) {
        loadArtistInfo(extras, null);
    }
}
