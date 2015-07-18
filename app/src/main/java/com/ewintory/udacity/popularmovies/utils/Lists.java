package com.ewintory.udacity.popularmovies.utils;

import java.util.Collection;


public final class Lists {

    public static <E> boolean isEmpty(Collection<E> list) {
        return (list == null || list.size() == 0);
    }

}
