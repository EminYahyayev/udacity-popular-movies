package com.ewintory.udacity.popularmovies.ui.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Emin Yahyayev
 */
public final class PagingModel<T> {

    public static final int NO_PAGE = 0;

    @Nullable
    public final List<T> items;
    public final int currentPage;
    public final boolean inProgress;
    public final boolean allPagesLoaded;
    @Nullable
    public final String errorMessage;

    private PagingModel(@Nullable List<T> items,
                        int currentPage,
                        boolean inProgress,
                        boolean allPagesLoaded,
                        @Nullable String errorMessage) {
        this.items = items;
        this.currentPage = currentPage;
        this.inProgress = inProgress;
        this.allPagesLoaded = allPagesLoaded;
        this.errorMessage = errorMessage;
    }

    public static <T> PagingModel<T> init(boolean inProgress) {
        return new PagingModel<>(null, NO_PAGE, inProgress, false, null);
    }

    public PagingModel<T> inProgress() {
        return new PagingModel<>(items, currentPage, true, allPagesLoaded, null);
    }

    public PagingModel<T> nextPage(@NonNull List<T> newItems) {
        List<T> list = items != null ? new ArrayList<>(items) : new ArrayList<>();
        list.addAll(newItems);
        return new PagingModel<>(list, currentPage + 1, false, newItems.isEmpty(), null);
    }

    public PagingModel<T> failure(String errorMessage) {
        return new PagingModel<>(items, currentPage, false, allPagesLoaded, errorMessage);
    }

    @Override public String toString() {
        return "PagingModel{" +
                "itemsSize=" + (items != null ? items.size() : null) +
                ", currentPage=" + currentPage +
                ", inProgress=" + inProgress +
                ", allPagesLoaded=" + allPagesLoaded +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
