package com.ewintory.udacity.popularmovies.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ewintory.udacity.popularmovies.data.api.Sort;


/**
 * Utilities and constants related to app preferences.
 */
public final class PrefUtils {

    /**
     * Boolean indicating whether we performed the (one-time) welcome flow.
     */
    public static final String PREF_WELCOME_DONE = "pref_welcome_done";

    public static final String PREF_MOVIES_SORT = "pref_movies_sort";

    public static final String PREF_BROWSE_MOVIES_MODE = "pref_browse_movies_mode";

    public static final String PREF_INCLUDE_ADULT = "pref_include_adult";

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

    public static String getBrowseMoviesMode(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_BROWSE_MOVIES_MODE, Sort.POPULARITY.toString());
    }

    public static void setBrowseMoviesMode(final Context context, String mode) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_BROWSE_MOVIES_MODE, mode).apply();
    }

    public static boolean isIncludeAdult(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(PREF_INCLUDE_ADULT, false);
    }

    public static void setIncludeAdult(final Context context, boolean include) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(PREF_INCLUDE_ADULT, include).apply();
    }

    public static void registerOnSharedPreferenceChangeListener(final Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void unregisterOnSharedPreferenceChangeListener(final Context context, SharedPreferences.OnSharedPreferenceChangeListener listener) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public static void clear(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().clear().apply();
    }
}
