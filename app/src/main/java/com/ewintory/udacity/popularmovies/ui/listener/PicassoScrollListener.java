package com.ewintory.udacity.popularmovies.ui.listener;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

/**
 * Simple {@link RecyclerView.OnScrollListener} implementation which
 * pauses/resumes Picasso's tagged requests
 *
 * @author Emin Yahyayev
 * @see {@link RequestCreator#tag(Object)}
 */
public final class PicassoScrollListener extends RecyclerView.OnScrollListener {

    private static final int DEFAULT_SENSITIVITY = 120;

    private Picasso mPicasso;
    private int mPauseSensitivity; // scroll sensitivity
    private Object mTag;

    public PicassoScrollListener(Context context, Object tag) {
        this(Picasso.with(context), tag, DEFAULT_SENSITIVITY);
    }

    public PicassoScrollListener(Picasso picasso, Object tag) {
        this(picasso, tag, DEFAULT_SENSITIVITY);
    }

    public PicassoScrollListener(Context context, Object tag, int pauseSensitivity) {
        this(Picasso.with(context), tag, pauseSensitivity);
    }

    public PicassoScrollListener(Picasso picasso, Object tag, int pauseSensitivity) {
        mPicasso = picasso;
        mPauseSensitivity = pauseSensitivity;
        mTag = tag;
    }

    @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (Math.abs(dy) > mPauseSensitivity)
            mPicasso.pauseTag(mTag);
        else
            mPicasso.resumeTag(mTag);
    }
}
