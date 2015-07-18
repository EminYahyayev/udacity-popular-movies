package com.ewintory.udacity.popularmovies.data.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import rx.functions.Func1;


public class GenreMetadata {

    public static final Func1<Response, GenreMetadata> TRANSFORMER = new Func1<Response, GenreMetadata>() {
        @Override public GenreMetadata call(GenreMetadata.Response response) {
            return new GenreMetadata(response);
        }
    };

    private HashMap<Integer, Genre> mGenresMap;
    private List<Genre> mGenres;

    public GenreMetadata(@NonNull Response response) {
        this(response.getGenres());
    }

    public GenreMetadata(@NonNull List<Genre> genres) {
        mGenres = genres;

        for (Genre genre : genres)
            mGenresMap.put(genre.getId(), genre);
    }

    public List<Genre> getGenres() {
        return Collections.unmodifiableList(mGenres);
    }

    public String getGenreName(Integer id) {
        Genre genre = mGenresMap.get(id);
        return genre != null ? genre.getName() : null;
    }

    public static class Genre {
        @Expose
        private Integer id;
        @Expose
        private String name;

        public Integer getId() {
            return id;
        }

        public Genre setId(Integer id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public Genre setName(String name) {
            this.name = name;
            return this;
        }
    }

    public static class Response {

        @Expose
        private List<Genre> genres = new ArrayList<>();

        public List<Genre> getGenres() {
            return genres;
        }

        public Response setGenres(List<Genre> genres) {
            this.genres = genres;
            return this;
        }
    }
}
