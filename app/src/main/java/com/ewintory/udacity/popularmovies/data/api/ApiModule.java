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

import com.ewintory.udacity.popularmovies.BuildConfig;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module(complete = false, library = true)
public final class ApiModule {
    public static final String MOVIE_DB_API_URL = "http://api.themoviedb.org/3";

    @Provides @Singleton Endpoint provideEndpoint() {
        return Endpoints.newFixedEndpoint(MOVIE_DB_API_URL);
    }

    @Provides @Singleton @Named("Api") OkHttpClient provideApiClient(OkHttpClient client) {
        return client.clone();
    }

    @Provides @Singleton RestAdapter provideRestAdapter(Endpoint endpoint, @Named("Api") OkHttpClient client, Gson gson) {
        return new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setEndpoint(endpoint)
                .setLogLevel(RestAdapter.LogLevel.NONE)
                .setRequestInterceptor(request -> request.addQueryParam("api_key", BuildConfig.MOVIE_DB_API_KEY))
                .setConverter(new GsonConverter(gson))
                .build();
    }

    @Provides @Singleton MoviesApi provideMoviesApi(RestAdapter restAdapter) {
        return restAdapter.create(MoviesApi.class);
    }
}