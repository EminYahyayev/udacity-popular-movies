package com.ewintory.udacity.popularmovies.data.api;

import android.app.Application;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.model.GenreMetadata;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.data.model.Review;
import com.ewintory.udacity.popularmovies.data.model.Video;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;


public final class MockMoviesService implements MoviesService {

    private static final Random sRandom = new Random();
    private static final int PAGE_SIZE = 20;

    private final Application mApplication;
    private final Integer mTotalMovies;
    private final Integer mTotalMoviePages;
    private final int[] mGenreIds;

    public MockMoviesService(Application application) {
        mApplication = application;
        mGenreIds = application.getResources().getIntArray(R.array.genre_ids);

        mTotalMovies = sRandom.nextInt(100 + sRandom.nextInt(50));
        mTotalMoviePages = mTotalMovies % PAGE_SIZE;
    }

    @Override public Observable<GenreMetadata.Response> genres() {
        return Observable.empty();
    }

    @Override public Observable<Movie.Response> discoverMovies(@Query("sort_by") Sort sort, @Query("page") Integer page) {
        return Observable.just(mockMovieResponse(page)).timeout(1 + sRandom.nextInt(3), TimeUnit.SECONDS);
    }

    @Override public Observable<Video.Response> videos(@Path("id") Integer id) {
        return Observable.empty();
    }

    @Override public Observable<Review.Response> reviews(@Path("id") Integer id, @Query("page") Integer page) {
        return Observable.empty();
    }

    private Movie.Response mockMovieResponse(Integer page) {
        List<Movie> movies = new ArrayList<>(PAGE_SIZE);

        for (int id = page * PAGE_SIZE; id < (page + 1) * PAGE_SIZE; id++) {
            movies.add(mockMovie(id));
        }

        return new Movie.Response()
                .setMovies(movies)
                .setPage(page)
                .setTotalPages(mTotalMoviePages)
                .setTotalMovies(mTotalMovies);
    }

    private Movie mockMovie(int id) {
        return new Movie()
                .setTitle("Movie #" + id)
                .setGenreIds(Arrays.asList(randomGenreId(), randomGenreId()));
    }

    private Integer randomGenreId() {
        return mGenreIds[sRandom.nextInt(mGenreIds.length)];
    }

}
