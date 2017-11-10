package newscavazzini.similarartists.mvp.view;

import java.util.List;

import newscavazzini.similarartists.models.artist.Artist;

public interface MainActivityView {
    void showArtists(List<Artist> artists);
    void showError();
    void showError(String errorMessage);
}
