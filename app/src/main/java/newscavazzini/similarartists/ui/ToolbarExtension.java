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

package newscavazzini.similarartists.ui;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import newscavazzini.similarartists.R;

public class ToolbarExtension {

    private Activity context;

    public ToolbarExtension(Activity context) {
        this.context = context;
        this.clear();
    }

    public void show(int image, String lineOne, String lineTwo) {

        ImageView toolbarExtImg = (ImageView) this.context.findViewById(R.id.toolbar_ext_img);
        TextView toolbarExtLineOne = (TextView) this.context.findViewById(R.id.toolbar_ext_tv_line_one);
        TextView toolbarExtLineTwo = (TextView) this.context.findViewById(R.id.toolbar_ext_tv_line_two);


        if (image == 0) {
            toolbarExtImg.setVisibility(View.INVISIBLE);
            toolbarExtLineOne.setVisibility(View.INVISIBLE);
            toolbarExtLineTwo.setVisibility(View.INVISIBLE);
            return;
        }

        if (lineOne != null && !lineOne.equals("")) {
            toolbarExtLineOne.setText(lineOne);
            toolbarExtLineOne.setVisibility(View.VISIBLE);
        } else {
            toolbarExtLineOne.setVisibility(View.GONE);
        }

        if (lineTwo != null && !lineTwo.equals("")) {
            toolbarExtLineTwo.setText(lineTwo);
            toolbarExtLineTwo.setVisibility(View.VISIBLE);
        } else {
            toolbarExtLineTwo.setVisibility(View.GONE);
        }

        toolbarExtImg.setImageDrawable(ContextCompat.getDrawable(this.context, image));
        toolbarExtImg.setVisibility(View.VISIBLE);
        toolbarExtImg.setPadding(0, 0, 0, 10);

    }

    public void clear() {
        show(0, null, null);
    }

}
