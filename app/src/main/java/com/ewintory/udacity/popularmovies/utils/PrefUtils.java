package com.ewintory.udacity.popularmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ewintory.udacity.popularmovies.data.api.Sort;

import java.util.HashSet;
import java.util.Set;


/**
 * Utilities and constants related to app preferences.
 */
public final class PrefUtils {

    private PrefUtils() {
        throw new AssertionError("No instances.");
    }

    public static final String PREF_WELCOME_DONE = "pref_welcome_done";

    public static final String PREF_MOVIES_SORT = "pref_movies_sort";

    public static final String PREF_FAVORITE_MOVIES = "pref_favorite_movies";

    public static boolean isWelcomeDone(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_WELCOME_DONE, false);
    }

    public static void markWelcomeDone(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_WELCOME_DONE, true).apply();
    }

    public static Sort getMoviesSort(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String sort = sp.getString(PREF_MOVIES_SORT, Sort.POPULARITY.toString());
        return Sort.fromString(sort);
    }

    public static void setMoviesSort(final Context context, Sort sort) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_MOVIES_SORT, sort.toString()).apply();
    }

    public static Set<String> getFavoriteMovies(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getStringSet(PREF_FAVORITE_MOVIES, new HashSet<String>());
    }

    public static void setFavoriteMovies(final Context context, final Set<String> set) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putStringSet(PREF_FAVORITE_MOVIES, set).apply();
    }

    public static void registerOnSharedPreferenceChangeListener(final Context context,
                                                                SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnSharedPreferenceChangeListener(final Context context,
                                                                  SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static void clear(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().clear().apply();
    }
}
