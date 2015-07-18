package com.ewintory.udacity.popularmovies.ui.listener;

import android.view.View;

import com.ewintory.udacity.popularmovies.data.model.Movie;

public interface MovieClickListener {

    void onContentClicked(Movie movie, View view);

    void onFavoredClicked(Movie movie, View view);

    MovieClickListener DUMMY = new MovieClickListener() {
        @Override public void onContentClicked(Movie movie, View view) { /** ignore */}

        @Override public void onFavoredClicked(Movie movie, View view) { /** ignore */}
    };
}
