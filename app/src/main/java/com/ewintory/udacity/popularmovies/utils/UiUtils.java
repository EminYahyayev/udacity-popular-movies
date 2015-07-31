package com.ewintory.udacity.popularmovies.utils;


import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.ewintory.udacity.popularmovies.data.model.Genre;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

public final class UiUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    private UiUtils() {
        throw new AssertionError("No instances.");
    }

    public static String getDisplayReleaseDate(String releaseDate) {
        if (TextUtils.isEmpty(releaseDate)) return "";
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(DATE_FORMAT.parse(releaseDate));
            return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH) + " " + calendar.get(Calendar.YEAR);
        } catch (ParseException e) {
            Timber.e(e, "Failed to parse release date.");
            return "";
        }
    }

    public static String joinStrings(List<String> strings, String delimiter, @NonNull StringBuilder builder) {
        builder.setLength(0);
        if (strings != null)
            for (String str : strings) {
                if (builder.length() > 0) builder.append(delimiter);
                builder.append(str);
            }
        return builder.toString();
    }

    public static String joinGenres(List<Genre> genres, String delimiter, @NonNull StringBuilder builder) {
        builder.setLength(0);
        if (!Lists.isEmpty(genres))
            for (Genre genre : genres) {
                if (builder.length() > 0) builder.append(delimiter);
                builder.append(genre.getName());
            }
        return builder.toString();
    }
}
