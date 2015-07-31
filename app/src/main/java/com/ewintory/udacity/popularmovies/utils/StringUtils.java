package com.ewintory.udacity.popularmovies.utils;

import java.util.List;

/**
 * String formatting utils for our app
 */
public final class StringUtils {

    private StringUtils() {
        throw new AssertionError("No instances.");
    }

    public static String join(List<String> strings, String delimiter) {
        return join(strings, delimiter, new StringBuilder(strings.size() * 8));
    }

    public static String joinIntegers(List<Integer> integers, String delimiter) {
        return joinIntegers(integers, delimiter, new StringBuilder(30));
    }


    public static String join(List<String> strings, String delimiter, StringBuilder builder) {
        builder.setLength(0);
        if (strings != null)
            for (String str : strings) {
                if (builder.length() > 0) builder.append(delimiter);
                builder.append(str);
            }
        return builder.toString();
    }

    public static String joinIntegers(List<Integer> integers, String delimiter, StringBuilder builder) {
        builder.setLength(0);
        if (integers != null)
            for (Integer integer : integers) {
                if (builder.length() > 0) builder.append(delimiter);
                builder.append(String.valueOf(integer));
            }
        return builder.toString();
    }
}
