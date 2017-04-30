package com.ewintory.udacity.popularmovies.ui.model;

/**
 * @author Emin Yahyayev
 */
public class PageUiEvent {

    public final int page;

    public PageUiEvent(int page) {
        this.page = page;
    }

    @Override public String toString() {
        return "PageUiEvent{" +
                "page=" + page +
                '}';
    }
}
