/*
 * Copyright 2015.  Emin Yahyayev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ewintory.udacity.popularmovies.data.api;


import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.data.api.response.PageResponse;
import com.ewintory.udacity.popularmovies.data.api.response.GenresResponse;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface MoviesApi {

    String BASE_URL = "http://api.themoviedb.org/3/";

    @GET("genre/movie/list") Observable<Response<GenresResponse>> genres();

    @GET("discover/movie") Observable<Response<PageResponse<Movie>>> discoverMovies(
            @Query("sort_by") Sort sort,
            @Query("page") int page);

//    @GET("/discover/movie") Observable<Movie.Response> discoverMovies(
//            @Query("sort_by") Sort sort,
//            @Query("page") int page,
//            @Query("include_adult") boolean includeAdult);
//
//    @GET("/movie/{id}/videos") Observable<Video.Response> videos(
//            @Path("id") long movieId);
//
//    @GET("/movie/{id}/reviews") Observable<Review.Response> reviews(
//            @Path("id") long movieId,
//            @Query("page") int page);

}
