package com.ewintory.udacity.popularmovies.data.api;

import java.io.Serializable;

public enum Sort implements Serializable {

    POPULARITY("popularity.desc"),
    VOTE_AVERAGE("vote_average.desc"),
    VOTE_COUNT("vote_count.desc");

    private final String value;

    Sort(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return value;
    }

    public static Sort fromString(String value) {
        if (value != null) {
            for (Sort sort : Sort.values()) {
                if (value.equalsIgnoreCase(sort.value)) {
                    return sort;
                }
            }
        }
        throw new IllegalArgumentException("No constant with text " + value + " found");
    }
}
