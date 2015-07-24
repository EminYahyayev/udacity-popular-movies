package com.ewintory.udacity.popularmovies.ui.listener;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.view.View;

import com.ewintory.udacity.popularmovies.data.model.Movie;

public interface MovieClickListener {

    void onContentClicked(@NonNull final Movie movie, View view, @Nullable Palette.Swatch swatch);

    boolean onFavoredClicked(@NonNull final Movie movie);

    MovieClickListener DUMMY = new MovieClickListener() {
        @Override public void onContentClicked(@NonNull Movie movie, View view, @Nullable Palette.Swatch swatch) { /** ignore */}

        @Override public boolean onFavoredClicked(@NonNull Movie movie) { return true;}
    };
}
