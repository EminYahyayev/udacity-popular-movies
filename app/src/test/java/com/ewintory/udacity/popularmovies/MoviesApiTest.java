package com.ewintory.udacity.popularmovies;

import android.support.annotation.Nullable;

import com.ewintory.udacity.popularmovies.data.CustomAdapterFactory;
import com.ewintory.udacity.popularmovies.data.api.AuthenticationInterceptor;
import com.ewintory.udacity.popularmovies.data.api.MoviesApi;
import com.ewintory.udacity.popularmovies.data.api.response.GenresResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

/**
 * @author Emin Yahyayev
 */
public final class MoviesApiTest {

    private static final int CONNECTION_TIMEOUT_SECONDS = 20;
    private static final String MOVIE_DB_API_KEY = "efc1a109f3955149f56aa7c5471bc287";

    private static Gson gson;
    private static MoviesApi api;

    @BeforeClass
    public static void setUp() {
        gson = new GsonBuilder()
                .serializeNulls()
                .registerTypeAdapterFactory(CustomAdapterFactory.create())
                .create();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(CONNECTION_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(new AuthenticationInterceptor(MOVIE_DB_API_KEY))
                .addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .baseUrl(HttpUrl.parse(MoviesApi.BASE_URL))
                .client(clientBuilder.build())
                .build();

        api = retrofit.create(MoviesApi.class);
    }

    private static void print(@Nullable Object object) {
        System.out.println(object != null ? object.toString() : "null");
    }

    @Before
    public void beforeTest() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override public Scheduler getMainThreadScheduler() {
                return Schedulers.immediate();
            }
        });
    }

    @After
    public void afterTest() {
        RxAndroidPlugins.getInstance().reset();
    }

    private void printJSON(Object object) {
        System.out.println(gson.toJson(object));
    }

    private static <T> Response<T> assertResponse(Observable<Response<T>> observable) {
        TestSubscriber<Response<T>> testSubscriber = new TestSubscriber<>();
        observable.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        List<Response<T>> responses = testSubscriber.getOnNextEvents();
        Assert.assertNotNull(responses);
        Assert.assertEquals(1, responses.size());
        return responses.get(0);
    }

    @Test
    public void testGenres() {
        Response<GenresResponse> response = assertResponse(api.genres());
        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.code());

        GenresResponse resp = response.body();
        Assert.assertNotNull(resp);
        printJSON(resp);
    }
}
