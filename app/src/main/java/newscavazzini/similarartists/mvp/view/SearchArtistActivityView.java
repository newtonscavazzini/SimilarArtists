package newscavazzini.similarartists.mvp.view;

import java.util.List;

import newscavazzini.similarartists.models.artist.Artist;


public interface SearchArtistActivityView {
    void showResults(List<Artist> artistsFound);

    void displayLoadingLayout();
    void displayErrorLayout();
    void displayResultsLayout();
}
