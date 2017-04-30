package com.ewintory.udacity.popularmovies.utils;

import android.support.annotation.Nullable;

import java.util.Collection;


/**
 * Утилиты для работы с коллекциями
 *
 * @author Emin Yahyayev (yahyayev@iteratia.com)
 */
public final class CollectionUtils {

    public static int safeSize(@Nullable Collection collection) {
        return (collection != null) ? collection.size() : 0;
    }

    public static boolean isEmpty(@Nullable Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean isEmpty(@Nullable T[] array) {
        return array == null || array.length == 0;
    }

    private CollectionUtils() {
        throw new AssertionError("No instances.");
    }
}
