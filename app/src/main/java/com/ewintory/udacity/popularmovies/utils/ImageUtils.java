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

        //Timber.v("buildPosterUrl: widthPath=" + widthPath);
        return BASE_URL + widthPath + imagePath;
    }

}
