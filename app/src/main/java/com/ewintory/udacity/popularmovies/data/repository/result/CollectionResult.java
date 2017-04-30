package com.ewintory.udacity.popularmovies.data.repository.result;

import java.util.List;

/**
 * @author Emin Yahyayev
 */
public final class CollectionResult<T> {

    public final boolean inFlight;
    public final List<T> items;
    public final String errorMessage;

    private CollectionResult(boolean inFlight, List<T> items, String errorMessage) {
        this.inFlight = inFlight;
        this.items = items;
        this.errorMessage = errorMessage;
    }

    public static <T> CollectionResult<T> inFlight() {
        return new CollectionResult<>(true, null, null);
    }

    public static <T> CollectionResult<T> success(List<T> items) {
        return new CollectionResult<>(false, items, null);
    }

    public static <T> CollectionResult<T> failure(String errorMessage) {
        return new CollectionResult<>(false, null, errorMessage);
    }

}
