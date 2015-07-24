package com.ewintory.udacity.popularmovies.data.api;


import com.ewintory.udacity.popularmovies.data.model.GenreMetadata;
import com.ewintory.udacity.popularmovies.data.model.MoviesResponse;
import com.ewintory.udacity.popularmovies.data.model.Review;
import com.ewintory.udacity.popularmovies.data.model.Video;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface MoviesApi {

    @GET("/genre/movie/list") Observable<GenreMetadata.Response> genres();

    @GET("/discover/movie") Observable<MoviesResponse> discoverMovies(
            @Query("sort_by") Sort sort,
            @Query("page") Integer page);

    @GET("/discover/movie") Observable<MoviesResponse> discoverMovies(
            @Query("sort_by") Sort sort,
            @Query("page") Integer page,
            @Query("include_adult") boolean includeAdult);

    @GET("/movie/{id}/videos") Observable<Video.Response> videos(
            @Path("id") Integer id);

    @GET("/movie/{id}/reviews") Observable<Review.Response> reviews(
            @Path("id") Integer id,
            @Query("page") Integer page);

}
