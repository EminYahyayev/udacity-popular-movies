package com.ewintory.udacity.popularmovies.data.service;

import com.ewintory.udacity.popularmovies.data.model.MoviesResponse;
import com.ewintory.udacity.popularmovies.data.model.Sort;

import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface MoviesService {

    @GET("/discover/movie") Observable<MoviesResponse> discoverMovies(
            @Query("sort_by") Sort sort,
            @Query("page") Integer page);

}
