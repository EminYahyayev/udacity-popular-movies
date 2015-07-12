package com.ewintory.udacity.popularmovies.utils;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

public final class GlideSetup implements GlideModule {
    private static final String TAG = GlideSetup.class.getSimpleName();

    public static final String BASE_URL = "http://image.tmdb.org/t/p";

    @Override public void applyOptions(Context context, GlideBuilder builder) { /** ignore */}

    @Override public void registerComponents(Context context, Glide glide) {
        glide.register(String.class, InputStream.class, new MovieDBLoader.Factory());
    }

    private static class MovieDBLoader extends BaseGlideUrlLoader<String> {

        public MovieDBLoader(Context context) {
            super(context);
        }

        // TODO: support other kinds of images(logo_sizes, e.t.c)
        @Override protected String getUrl(String model, int width, int height) {
            Log.v(TAG, "getUrl: model=" + model);
            return getPosterUrl(model, width);
        }

        // NOTE: only works with "poster_size" values
        private String getPosterUrl(String model, int width) {
            String widthPath;

            if (width <= 92)
                widthPath = "/w92";
            else if (width <= 154)
                widthPath = "/w154";
            else if (width <= 185)
                widthPath = "/w185";
            else if (width <= 342)
                widthPath = "/w342";
            else if (width <= 500)
                widthPath = "/w500";
            else
                widthPath = "/w780";

            Log.v(TAG, "getPosterUrl: widthPath=" + widthPath);
            return BASE_URL + widthPath + model;
        }

        public static class Factory implements ModelLoaderFactory<String, InputStream> {
            @Override public StreamModelLoader<String> build(Context context, GenericLoaderFactory factories) {
                return new MovieDBLoader(context);
            }

            @Override public void teardown() { /** ignore */}
        }
    }
}
