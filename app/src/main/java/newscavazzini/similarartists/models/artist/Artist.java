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

package newscavazzini.similarartists.models.artist;


import java.io.Serializable;
import java.util.List;

public class Artist implements Serializable {

    private String name;
    private String mbid;
    private boolean ontour;
    private Bio bio;
    private Similar similar;
    private Stats stats;

    private List<Tag> tagsList;

    public String getName() {
        return name;
    }

    public String getMbid() {
        return mbid;
    }

    public boolean isOntour() {
        return ontour;
    }

    public Bio getBio() {
        return bio;
    }

    public Similar getSimilar() {
        return similar;
    }

    public Stats getStats() {
        return stats;
    }

    public List<Tag> getTagsList() {
        return tagsList;
    }

    public void setTagsList(List<Tag> tagsList) {
        this.tagsList = tagsList;
    }

    public String getTagsAsString() {

        if (this.tagsList.size() == 0)
            return "";

        StringBuilder tagsString = new StringBuilder();

        for (Tag tag : this.tagsList) {
            tagsString.append(tag.getName()).append(", ");
        }

        return tagsString.delete(tagsString.length() - 2, tagsString.length() - 1).toString();
    }
}
