package com.ewintory.udacity.popularmovies.utils;

import java.util.List;

/**
 * String formatting utils for our app
 */
public final class StringUtils {

    public static String join(List<String> strings, String delimiter) {
        return join(strings, delimiter, new StringBuilder(strings.size() * 8));
    }

    public static String join(List<String> strings, String delimiter, StringBuilder builder) {
        if (strings != null)
            for (String str : strings) {
                if (builder.length() > 0) builder.append(delimiter);
                builder.append(str);
            }
        return builder.toString();
    }
}
