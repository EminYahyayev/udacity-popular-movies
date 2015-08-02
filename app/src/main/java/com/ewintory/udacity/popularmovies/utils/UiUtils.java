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
