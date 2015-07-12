package com.ewintory.udacity.popularmovies.dagger;

import com.ewintory.udacity.popularmovies.BuildConfig;
import com.ewintory.udacity.popularmovies.data.service.MoviesService;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

@Module(
        complete = false,
        library = true
)
public final class ApiModule {
    public static final String MOVIE_DB_API_URL = "http://api.themoviedb.org/3";

    @Provides @Singleton Endpoint provideEndpoint() {
        return Endpoints.newFixedEndpoint(MOVIE_DB_API_URL);
    }

    @Provides @Singleton RestAdapter provideRestAdapter(Endpoint endpoint, OkHttpClient client, Gson gson) {
        return new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setEndpoint(endpoint)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override public void intercept(RequestFacade request) {
                        request.addQueryParam("api_key", BuildConfig.MOVIE_DB_API_KEY);
                    }
                })
                .setConverter(new GsonConverter(gson))
                .build();
    }

    @Provides @Singleton MoviesService provideMoviesService(RestAdapter restAdapter) {
        return restAdapter.create(MoviesService.class);
    }
}