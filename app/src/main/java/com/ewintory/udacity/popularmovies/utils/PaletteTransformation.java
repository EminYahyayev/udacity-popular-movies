package com.ewintory.udacity.popularmovies.utils;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.squareup.picasso.Transformation;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * @see <a href="http://jakewharton.com/coercing-picasso-to-play-with-palette/">Jake's example</a>
 */
public final class PaletteTransformation implements Transformation {

    private static final PaletteTransformation INSTANCE = new PaletteTransformation();
    private static final Map<Bitmap, Palette> CACHE = new WeakHashMap<>();

    public static PaletteTransformation instance() {
        return INSTANCE;
    }

    public static Palette getPalette(Bitmap bitmap) {
        return CACHE.get(bitmap);
    }

    private PaletteTransformation() {}

    @Override public Bitmap transform(Bitmap source) {
        Palette palette = new Palette.Builder(source)
                .maximumColorCount(24)
                .generate();

        CACHE.put(source, palette);
        return source;
    }

    @Override public String key() {
        return ""; // Stable key for all requests. An unfortunate requirement.
    }

}
