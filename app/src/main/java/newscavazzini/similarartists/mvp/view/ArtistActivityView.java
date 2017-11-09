package newscavazzini.similarartists.mvp.view;

import android.os.Bundle;

import java.util.List;

import newscavazzini.similarartists.models.album.Album;
import newscavazzini.similarartists.models.artist.Artist;
import newscavazzini.similarartists.models.track.Track;

public interface ArtistActivityView {

    void showArtistInfo(Artist artist);
    void artistNotFound();

    void showSimilarArtists(List<Artist> similarArtists);
    void similarArtistsNotFound();

    void showAlbums(List<Album> topAlbums);
    void albumsNotFound();

    void showTracks(List<Track> topTracks);
    void tracksNotFound();

    void openFullBio(Bundle bundle);
    void showMoreSimilars(Bundle bundle);
    void openPlayStore(String artistName);
    void openYoutube(String artistName);

    void showLoadingProgress();
    void hideLoadingProgress();

}