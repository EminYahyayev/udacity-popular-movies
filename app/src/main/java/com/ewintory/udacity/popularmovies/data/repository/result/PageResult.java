package com.ewintory.udacity.popularmovies.data.repository.result;

import android.support.annotation.Nullable;

import java.util.List;

/**
 * @author Emin Yahyayev
 */
public final class PageResult<T> {

    public final boolean inFlight;
    public final int page;
    @Nullable
    public final List<T> items;
    @Nullable
    public final String errorMessage;

    private PageResult(boolean inFlight, int page, @Nullable List<T> items, @Nullable String errorMessage) {
        this.inFlight = inFlight;
        this.items = items;
        this.page = page;
        this.errorMessage = errorMessage;
    }

    @SuppressWarnings("unchecked")
    public static <T> PageResult<T> inFlight(int page) {
        return new PageResult<>(true, page, null, null);
    }

    public static <T> PageResult<T> success(int page, List<T> items) {
        return new PageResult<>(false, page, items, null);
    }

    public static <T> PageResult<T> failure(int page, String message) {
        return new PageResult<>(false, page, null, message);
    }

    @Override public String toString() {
        return "PageResult{" +
                "inFlight=" + inFlight +
                ", page=" + page +
                ", items=" + (items != null ? items.size() : null) +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
