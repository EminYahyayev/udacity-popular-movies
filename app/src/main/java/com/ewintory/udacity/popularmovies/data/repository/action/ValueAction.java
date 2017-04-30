package com.ewintory.udacity.popularmovies.data.repository.action;

/**
 * @author Emin Yahyayev
 */
public final class ValueAction<T> extends Action {

    public final T value;

    public ValueAction(T value) {
        this.value = value;
    }

    @Override public String toString() {
        return "TypeAction{" +
                "value=" + value +
                '}';
    }
}
