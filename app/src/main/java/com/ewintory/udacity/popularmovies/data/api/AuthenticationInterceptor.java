package com.ewintory.udacity.popularmovies.data.api;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Token authentication {@link Interceptor interceptor}
 *
 * @author Emin Yahyayev (yahyayev@iteratia.com)
 */
public final class AuthenticationInterceptor implements Interceptor {

    private final String apiKey;

    public AuthenticationInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        if (apiKey != null && apiKey.length() == 0) {
            Timber.w("Api key is empty, proceeding original request.");
            return chain.proceed(original);
        }

        HttpUrl originalHttpUrl = original.url();

        HttpUrl url = originalHttpUrl.newBuilder()
                .addQueryParameter("api_key", apiKey)
                .build();

        // Request customization: add request headers
        Request.Builder requestBuilder = original.newBuilder()
                .url(url);

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
