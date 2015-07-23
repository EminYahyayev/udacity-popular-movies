package com.ewintory.udacity.popularmovies.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public final class MoviesResponse {

    @Expose
    private Integer page;

    @Expose
    @SerializedName("total_pages")
    private Integer totalPages;

    @Expose
    @SerializedName("total_results")
    private Integer totalMovies;

    @SerializedName("results")
    private List<Movie> movies = new ArrayList<>();

    public Integer getPage() {
        return page;
    }

    public MoviesResponse setPage(Integer page) {
        this.page = page;
        return this;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public MoviesResponse setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
        return this;
    }

    public Integer getTotalMovies() {
        return totalMovies;
    }

    public MoviesResponse setTotalMovies(Integer totalMovies) {
        this.totalMovies = totalMovies;
        return this;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public MoviesResponse setMovies(List<Movie> movies) {
        this.movies = movies;
        return this;
    }
}
