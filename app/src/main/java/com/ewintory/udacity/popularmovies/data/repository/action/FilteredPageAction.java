package com.ewintory.udacity.popularmovies.data.repository.action;

/**
 * @author Emin Yahyayev
 */
public final class FilteredPageAction<F> extends Action{

    public final int page;
    public final F filter;

    public FilteredPageAction(F filter, int page) {
        this.filter = filter;
        this.page = page;
    }

    @Override public String toString() {
        return "FilteredPageAction{" +
                "page=" + page +
                ", filter=" + filter +
                '}';
    }
}
