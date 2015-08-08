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

package com.ewintory.udacity.popularmovies.data;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GenericLoaderFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.bumptech.glide.load.model.stream.StreamModelLoader;
import com.bumptech.glide.module.GlideModule;
import com.ewintory.udacity.popularmovies.PopularMoviesApplication;
import com.ewintory.udacity.popularmovies.utils.ImageUtils;
import com.squareup.okhttp.OkHttpClient;

import java.io.InputStream;

import javax.inject.Inject;

public final class GlideSetup implements GlideModule {

    @Inject OkHttpClient mOkHttpClient;

    @Override public void applyOptions(Context context, GlideBuilder builder) { /** ignore */}

    @Override public void registerComponents(Context context, Glide glide) {
        PopularMoviesApplication.get(context).inject(this);

        glide.register(String.class, InputStream.class, new ImageLoader.Factory());
        glide.register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(mOkHttpClient));
    }

    private static class ImageLoader extends BaseGlideUrlLoader<String> {

        public ImageLoader(Context context) {
            super(context);
        }

        // TODO: support other kinds of images(logo_sizes, e.t.c)
        @Override protected String getUrl(String imagePath, int width, int height) {
            String url = ImageUtils.buildPosterUrl(imagePath, width);
            //Timber.tag("Glide").v("Loading image: " + url);
            return url;
        }

        public static class Factory implements ModelLoaderFactory<String, InputStream> {
            @Override public StreamModelLoader<String> build(Context context, GenericLoaderFactory factories) {
                return new ImageLoader(context);
            }

            @Override public void teardown() { /** ignore */}
        }
    }
}
