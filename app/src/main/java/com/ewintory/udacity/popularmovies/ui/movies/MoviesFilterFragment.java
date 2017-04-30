package com.ewintory.udacity.popularmovies.ui.movies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ewintory.udacity.popularmovies.MoviesApp;
import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.repository.GenresRepository;
import com.ewintory.udacity.popularmovies.ui.BaseFragment;

import javax.inject.Inject;

/**
 * @author Emin Yahyayev
 */
public final class MoviesFilterFragment extends BaseFragment {

    @Inject GenresRepository genresRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MoviesApp.get(getContext())
                .getDataComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState) {
        return inflater.inflate(R.layout.fragment_movies_filter, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedState) {
        super.onViewCreated(view, savedState);
    }

}
