package com.ewintory.udacity.popularmovies.ui.listener;

import android.view.View;

import com.ewintory.udacity.popularmovies.data.model.Movie;

public interface MovieClickListener {

    void onContentClicked(Movie movie, View view);

    class SimpleListener implements MovieClickListener {

        @Override public void onContentClicked(Movie movie, View view) {}
    }
}
