package newscavazzini.similarartists.mvp.presenter;

import newscavazzini.similarartists.BuildConfig;
import newscavazzini.similarartists.mvp.view.SettingsActivityView;

public class SettingsActivityPresenter {

    private SettingsActivityView view;

    public SettingsActivityPresenter(SettingsActivityView view) {
        this.view = view;
    }

    public void openLink(String buttonTitle) {
        switch(buttonTitle) {

            case "Github":
                view.openWebsite("https://github.com/newtonscavazzini/SimilarArtists/");
                break;

            case "Lastfm":
                view.openWebsite("http://www.lastfm.com/");
                break;
        }
    }

    public void showAppVersion() {
        view.showAppVersion(BuildConfig.VERSION_NAME);
    }

}
