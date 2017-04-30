package com.ewintory.udacity.popularmovies.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import com.ewintory.udacity.popularmovies.BuildConfig;
import com.ewintory.udacity.popularmovies.data.api.AuthenticationInterceptor;
import com.ewintory.udacity.popularmovies.data.api.MoviesApi;
import com.ewintory.udacity.popularmovies.data.db.MoviesDatabase;
import com.ewintory.udacity.popularmovies.data.repository.GenresRepository;
import com.ewintory.udacity.popularmovies.data.repository.MoviesRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Зависимости по работе c данными
 *
 * @author Emin Yahyayev
 */
@Module
public final class DataModule {

    private static final int DISK_CACHE_SIZE = 100 * 1024 * 1024; // 100MB
    private static final int CONNECTION_TIMEOUT_SECONDS = 20;

    public DataModule() {}

    @Provides
    @Singleton
    SharedPreferences sharedPreferences(final Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    Gson gson() {
        return new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapterFactory(CustomAdapterFactory.create())
                .setLenient()
                .create();
    }

    @Provides
    @Singleton
    Cache okHttpCache(final Application application) {
        File cacheDir = new File(application.getCacheDir(), "http");
        return new Cache(cacheDir, DISK_CACHE_SIZE);
    }

    @Provides
    @Singleton
    OkHttpClient okHttpClient(final Cache cache) {

        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.HEADERS
                : HttpLoggingInterceptor.Level.NONE);

        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        builder.readTimeout(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        builder.cache(cache);

        builder.addInterceptor(logging);
        builder.addInterceptor(new AuthenticationInterceptor(BuildConfig.MOVIE_DB_API_KEY));

        return builder.build();
    }

    @Provides
    @Singleton
    MoviesApi moviesApi(Gson gson, OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(MoviesApi.BASE_URL)
                .client(client)
                .build();
        return retrofit.create(MoviesApi.class);
    }

    @Provides
    @Singleton
    SQLiteOpenHelper openHelper(final Application application) {
        return new MoviesDatabase(application);
    }

    @Provides
    @Singleton
    SqlBrite sqlBrite() {
        return new SqlBrite.Builder()
                .logger(message -> Timber.tag("Database").v(message))
                .build();
    }

    @Provides
    @Singleton
    BriteDatabase database(final SqlBrite sqlBrite, final SQLiteOpenHelper helper) {
        final BriteDatabase db = sqlBrite.wrapDatabaseHelper(helper, Schedulers.io());
        db.setLoggingEnabled(BuildConfig.DEBUG);
        return db;
    }

    @Provides
    @Singleton
    GenresRepository genresRepository(final Application app,
                                      final MoviesApi api,
                                      final BriteDatabase database) {
        return new GenresRepository(app, api, database);
    }

    @Provides
    @Singleton
    MoviesRepository moviesRepository(final Application app,
                                      final MoviesApi api,
                                      final BriteDatabase database) {
        return new MoviesRepository(app, api, database);
    }
}
