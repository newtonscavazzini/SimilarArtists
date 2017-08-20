/*
    Similar Artists
    Copyright (C) 2017  Newton Scavazzini <newtonscavazzini@gmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package newscavazzini.similarartists.listeners;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.net.URLEncoder;


public class YoutubePlayStoreClickListener {

    private Context context;

    public YoutubePlayStoreClickListener(Context context) {
        this.context = context;
    }

    public void openYoutube(String searchTerm) {
        try {

            String nameEncoded = URLEncoder.encode(searchTerm, "UTF-8");
            goToUrl("https://www.youtube.com/results?search_query=" + nameEncoded);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openPlayStore(String searchTerm) {
        try {

            String nameEncoded = URLEncoder.encode(searchTerm, "UTF-8");
            String url = "https://play.google.com/store/search?q=" + nameEncoded + "&c=music";
            goToUrl(url);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        context.startActivity(launchBrowser);
    }

}
