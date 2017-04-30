package com.ewintory.udacity.popularmovies.data.repository.action;

/**
 * @author Emin Yahyayev
 */
public final class PageAction extends Action {

    public final int page;

    public PageAction(int page) {
        this.page = page;
    }

    @Override public String toString() {
        return "PageAction{" +
                "page=" + page +
                '}';
    }
}
