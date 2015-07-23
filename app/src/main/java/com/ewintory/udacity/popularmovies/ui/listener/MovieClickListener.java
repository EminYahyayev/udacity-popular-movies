package com.ewintory.udacity.popularmovies.ui.listener;

import android.support.annotation.NonNull;
import android.view.View;

import com.ewintory.udacity.popularmovies.data.model.Movie;

public interface MovieClickListener {

    void onContentClicked(@NonNull final Movie movie, View view);

    boolean onFavoredClicked(@NonNull final Movie movie);

    MovieClickListener DUMMY = new MovieClickListener() {
        @Override public void onContentClicked(@NonNull Movie movie, View view) { /** ignore */}

        @Override public boolean onFavoredClicked(@NonNull Movie movie) { return true;}
    };
}
