package com.ewintory.udacity.popularmovies.ui.model;

/**
 * @author Emin Yahyayev
 */
public class FilteredPageUiEvent<F> {

    public final int page;
    public final F filter;

    public FilteredPageUiEvent(F filter, int page) {
        this.filter = filter;
        this.page = page;
    }

    @Override public String toString() {
        return "FilteredPageEvent{" +
                "page=" + page +
                ", filter=" + filter +
                '}';
    }
}
