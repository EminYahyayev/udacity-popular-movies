/*
 * Copyright 2015.  Emin Yahyayev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ewintory.udacity.popularmovies.data.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.ewintory.udacity.popularmovies.data.provider.meta.MovieMeta;
import com.ewintory.udacity.popularmovies.utils.Lists;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import static com.ewintory.udacity.popularmovies.data.provider.MoviesContract.MoviesColumns;


public final class Movie implements Parcelable, MovieMeta {

    @Expose
    long id;

    @Expose @SerializedName("genre_ids")
    List<Integer> genreIds = new ArrayList<>();

    @Expose
    String overview;

    @Expose @SerializedName("release_date")
    String releaseDate;

    @Expose @SerializedName("poster_path")
    String posterPath;

    @Expose @SerializedName("backdrop_path")
    String backdropPath;

    @Expose
    double popularity;

    @Expose
    String title;

    @Expose @SerializedName("vote_average")
    double voteAverage;

    @Expose @SerializedName("vote_count")
    long voteCount;

    boolean favored = false;

    List<Genre> genres;

    public Movie() {}

    public long getId() {
        return id;
    }

    public Movie setId(long id) {
        this.id = id;
        return this;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Movie setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
        return this;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public Movie setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
        return this;
    }

    public String getOverview() {
        return overview;
    }

    public Movie setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public Movie setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public Movie setPosterPath(String posterPath) {
        this.posterPath = posterPath;
        return this;
    }

    public double getPopularity() {
        return popularity;
    }

    public Movie setPopularity(double popularity) {
        this.popularity = popularity;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Movie setTitle(String title) {
        this.title = title;
        return this;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public Movie setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
        return this;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public Movie setVoteCount(long voteCount) {
        this.voteCount = voteCount;
        return this;
    }

    public boolean isFavored() {
        return favored;
    }

    public Movie setFavored(boolean favored) {
        this.favored = favored;
        return this;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public Movie setGenres(List<Genre> genres) {
        this.genres = genres;
        return this;
    }

    public String makeGenreIdsList() {
        if (Lists.isEmpty(genreIds)) return "";

        StringBuilder sb = new StringBuilder();
        sb.append(genreIds.get(0));
        for (int i = 1; i < genreIds.size(); i++) {
            sb.append(",").append(genreIds.get(i));
        }
        return sb.toString();
    }

    // TODO: Think about possible problems here
    public Movie putGenreIdsList(String ids) {
        if (!TextUtils.isEmpty(ids)) {
            genreIds = new ArrayList<>();
            String[] strs = ids.split(",");
            for (String s : strs)
                genreIds.add(Integer.parseInt(s));
        }
        return this;
    }

    @Override
    public String toString() {
        return "Movie{" + " title='" + title + '}';
    }

    public static final class Response {

        @Expose
        public int page;

        @Expose @SerializedName("total_pages")
        public int totalPages;

        @Expose @SerializedName("total_results")
        public int totalMovies;

        @Expose @SerializedName("results")
        public List<Movie> movies = new ArrayList<>();
    }

    // --------------------------------------------------------------------------------------

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeList(this.genreIds);
        dest.writeString(this.overview);
        dest.writeString(this.releaseDate);
        dest.writeString(this.posterPath);
        dest.writeString(this.backdropPath);
        dest.writeDouble(this.popularity);
        dest.writeString(this.title);
        dest.writeDouble(this.voteAverage);
        dest.writeLong(this.voteCount);
        dest.writeByte(favored ? (byte) 1 : (byte) 0);
        dest.writeTypedList(genres);
    }

    protected Movie(Parcel in) {
        this.id = in.readLong();
        this.genreIds = new ArrayList<Integer>();
        in.readList(this.genreIds, List.class.getClassLoader());
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.posterPath = in.readString();
        this.backdropPath = in.readString();
        this.popularity = in.readDouble();
        this.title = in.readString();
        this.voteAverage = in.readDouble();
        this.voteCount = in.readLong();
        this.favored = in.readByte() != 0;
        this.genres = in.createTypedArrayList(Genre.CREATOR);
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {return new Movie(source);}

        public Movie[] newArray(int size) {return new Movie[size];}
    };
}
