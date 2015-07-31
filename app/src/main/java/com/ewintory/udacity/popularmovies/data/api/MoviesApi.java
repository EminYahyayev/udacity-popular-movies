package com.ewintory.udacity.popularmovies.data.api;


import com.ewintory.udacity.popularmovies.data.model.Genre;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.data.model.Review;
import com.ewintory.udacity.popularmovies.data.model.Video;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface MoviesApi {

    @GET("/genre/movie/list") Observable<Genre.Response> genres();

    @GET("/discover/movie") Observable<Movie.Response> discoverMovies(
            @Query("sort_by") Sort sort,
            @Query("page") int page);

    @GET("/discover/movie") Observable<Movie.Response> discoverMovies(
            @Query("sort_by") Sort sort,
            @Query("page") int page,
            @Query("include_adult") boolean includeAdult);

    @GET("/movie/{id}/videos") Observable<Video.Response> videos(
            @Path("id") long movieId);

    @GET("/movie/{id}/reviews") Observable<Review.Response> reviews(
            @Path("id") long movieId,
            @Query("page") int page);

}
