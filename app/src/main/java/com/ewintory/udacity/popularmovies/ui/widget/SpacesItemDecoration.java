/*
 * Copyright 2015.  Emin Yahyayev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ewintory.udacity.popularmovies.ui.widget;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int spanCount;
    private int lastItemInFirstLane = -1;
    public SpacesItemDecoration(int space) {
        this(space, 1);
    }

    /**
     * @param space
     * @param spanCount spans count of one lane
     */
    public SpacesItemDecoration(int space, int spanCount) {
        this.space = space;
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        final int position = params.getViewPosition();
        final int spanSize;
        final int index;
        if (params instanceof GridLayoutManager.LayoutParams) {
            GridLayoutManager.LayoutParams gridParams = (GridLayoutManager.LayoutParams) params;
            spanSize = gridParams.getSpanSize();
            index = gridParams.getSpanIndex();
        }else{
            spanSize = 1;
            index = position % spanCount;
        }
        // invalid value
        if (spanSize < 1 || index < 0) return;

        if (spanSize == spanCount) { // full span
            outRect.left = space;
            outRect.right = space;
        } else {
            if (index == 0) {  // left one
                outRect.left = space;
            }
            // spanCount >= 1
            if (index == spanCount - 1) { // right one
                outRect.right = space;
            }
            if (outRect.left == 0) {
                outRect.left = space / 2;
            }
            if (outRect.right == 0) {
                outRect.right = space / 2;
            }
        }
        // set top to all in first lane
        if (position < spanCount && spanSize <= spanCount) {
            if (lastItemInFirstLane < 0) { // lay out at first time
                lastItemInFirstLane = position + spanSize == spanCount ? position : lastItemInFirstLane;
                outRect.top = space;
            } else if (position <= lastItemInFirstLane) { // scroll to first lane again
                outRect.top = space;
            }
        }
        outRect.bottom = space;

    }
}
