package com.ewintory.udacity.popularmovies.utils;

import android.content.res.Resources;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.util.TypedValue;

public final class ResourceUtils {

    private ResourceUtils() {
        throw new AssertionError("No instances.");
    }

    public static float getFloatDimension(@NonNull Resources resources, @DimenRes int dimenRes) {
        TypedValue outValue = new TypedValue();
        resources.getValue(dimenRes, outValue, true);
        return outValue.getFloat();
    }
}
