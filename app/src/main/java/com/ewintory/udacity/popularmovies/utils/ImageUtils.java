package com.ewintory.udacity.popularmovies.utils;

import timber.log.Timber;

public final class ImageUtils {

    public static final String BASE_URL = "http://image.tmdb.org/t/p";

    private ImageUtils() {
        throw new AssertionError("No instances.");
    }

    /**
     * Helper method which build corresponding url for poster based on the target's size
     * NOTE: only works with "poster_size" values
     *
     * @param imagePath which is returned with movies
     * @param width     of the target (ex. ImageView)
     * @return poster's full url
     */
    public static String buildPosterUrl(String imagePath, int width) {
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

        Timber.v("buildPosterUrl: widthPath=" + widthPath);
        return BASE_URL + widthPath + imagePath;
    }

}
