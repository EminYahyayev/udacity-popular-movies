package com.ewintory.udacity.popularmovies.data.model;

import java.io.Serializable;

public enum Sort implements Serializable {

    POPULARITY_ASC("popularity.asc"),
    POPULARITY_DESC("popularity.desc"),
    VOTE_AVERAGE_ASC("vote_average.asc"),
    VOTE_AVERAGE_DESC("vote_average.desc");

    private final String value;

    Sort(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return value;
    }
}
