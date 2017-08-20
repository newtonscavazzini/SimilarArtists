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

package newscavazzini.similarartists.retrofit.deserializer;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import newscavazzini.similarartists.models.artist.Artist;
import newscavazzini.similarartists.models.artist.Tag;

public class ArtistDeserializer implements JsonDeserializer<Artist> {

    @Override
    public Artist deserialize(JsonElement json, Type typeOfT,
                              JsonDeserializationContext context) throws JsonParseException {

        JsonElement content = json.getAsJsonObject().get("artist");

        JsonElement tags = content.getAsJsonObject()
                                  .get("tags")
                                  .getAsJsonObject()
                                  .get("tag")
                                  .getAsJsonArray();

        Artist artist = new Gson().fromJson(content, Artist.class);
        artist.setTagsList((List<Tag>) new Gson().fromJson(tags, new TypeToken<List<Tag>>(){}.getType()));

        return artist;
    }

}
