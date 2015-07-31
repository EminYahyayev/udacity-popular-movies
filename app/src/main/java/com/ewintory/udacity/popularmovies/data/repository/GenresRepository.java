package com.ewintory.udacity.popularmovies.data.repository;

import com.ewintory.udacity.popularmovies.data.model.Genre;

import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;

public interface GenresRepository {

    Observable<Map<Integer, Genre>> genres();

}
