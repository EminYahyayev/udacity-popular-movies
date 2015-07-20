package com.ewintory.udacity.popularmovies.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.ewintory.udacity.popularmovies.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MoviesHelper {

    public static final String BASE_IMAGE_URL = "http://image.tmdb.org/t/p";

    private final Context mContext;

    private HashMap<Integer, String> mGenresMap;

    public MoviesHelper(Context ctx) {
        mContext = ctx;
    }

    public String getGenreName(Integer id) {
        return getGenresMap().get(id);
    }

    public List<String> getGenreNames(@NonNull List<Integer> genreIds) {
        List<String> names = new ArrayList<>(genreIds.size());
        for (Integer id : genreIds)
            names.add(getGenreName(id));
        return names;
    }

    private HashMap<Integer, String> getGenresMap() {
        if (mGenresMap == null) {
            final int[] ids = mContext.getResources().getIntArray(R.array.genre_ids);
            final String[] names = mContext.getResources().getStringArray(R.array.genre_names);

            mGenresMap = new HashMap<>(ids.length);
            for (int i = 0; i < ids.length; i++)
                mGenresMap.put(ids[i], names[i]);
        }
        return mGenresMap;
    }

    /**
     * Helper method which build corresponding url for poster based on the target's size
     * NOTE: only works with "poster_size" values
     *
     * @param imagePath which is returned with movies
     * @param width     of the target (ex. ImageView)
     * @return poster's full url
     */
    public static String buildPosterImageUrl(String imagePath, int width) {
        String widthPath;

        if (width >= 780)
            widthPath = "/w780";
        else if (width >= 500)
            widthPath = "/w500";
        else if (width >= 342)
            widthPath = "/w342";
        else if (width >= 154)
            widthPath = "/w154";
        else
            widthPath = "w92";

        /**
         if (width <= 154)
         widthPath = "/w92";
         else if (width <= 185)
         widthPath = "/w154";
         else if (width <= 342)
         widthPath = "/w185";
         else if (width <= 500)
         widthPath = "/w342";
         else if (width <= 780)
         widthPath = "/w500";
         else
         widthPath = "/w780";
         */

        return BASE_IMAGE_URL + widthPath + imagePath;
    }
}
