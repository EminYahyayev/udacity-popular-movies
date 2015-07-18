package com.ewintory.udacity.popularmovies.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


// This annotation will trigger annotation processor
// Which will generate type mapping code in compile time,
// You just need to link it in your code.
@StorIOSQLiteType(table = "movies")
public final class Movie implements Serializable {

    @Expose
    @StorIOSQLiteColumn(name = "_id", key = true)
    Integer id;

    @Expose
    @SerializedName("backdrop_path")
    @StorIOSQLiteColumn(name = "backdrop_path")
    String backdropPath;

    @Expose
    @SerializedName("genre_ids")
    List<Integer> genreIds = new ArrayList<>();

    @Expose
    @SerializedName("original_language")
    @StorIOSQLiteColumn(name = "original_language")
    String originalLanguage;

    @Expose
    @SerializedName("original_title")
    @StorIOSQLiteColumn(name = "original_title")
    String originalTitle;

    @Expose
    @StorIOSQLiteColumn(name = "overview")
    String overview;

    @Expose
    @SerializedName("release_date")
    @StorIOSQLiteColumn(name = "release_date")
    String releaseDate;

    @Expose
    @SerializedName("poster_path")
    @StorIOSQLiteColumn(name = "poster_path")
    String posterPath;

    @Expose
    Double popularity;

    @Expose
    @StorIOSQLiteColumn(name = "title")
    String title;

    @Expose
    Boolean video;

    @Expose
    @SerializedName("vote_average")
    @StorIOSQLiteColumn(name = "voteAverage")
    Double voteAverage;

    @Expose
    @SerializedName("vote_count")
    @StorIOSQLiteColumn(name = "voteCount")
    Integer voteCount;

    public Movie() {}

    public Integer getId() {
        return id;
    }

    public Movie setId(Integer id) {
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

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public Movie setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
        return this;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public Movie setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
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

    public Double getPopularity() {
        return popularity;
    }

    public Movie setPopularity(Double popularity) {
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

    public Boolean getVideo() {
        return video;
    }

    public Movie setVideo(Boolean video) {
        this.video = video;
        return this;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public Movie setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
        return this;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public Movie setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
        return this;
    }

    @Override public String toString() {
        return "Movie{" +
                "backdropPath='" + backdropPath + '\'' +
                ", genreIds=" + genreIds +
                ", id=" + id +
                ", originalLanguage='" + originalLanguage + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", popularity=" + popularity +
                ", title='" + title + '\'' +
                ", video=" + video +
                ", voteAverage=" + voteAverage +
                ", voteCount=" + voteCount +
                '}';
    }

    public static final class Response {

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

        public Response setPage(Integer page) {
            this.page = page;
            return this;
        }

        public Integer getTotalPages() {
            return totalPages;
        }

        public Response setTotalPages(Integer totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public Integer getTotalMovies() {
            return totalMovies;
        }

        public Response setTotalMovies(Integer totalMovies) {
            this.totalMovies = totalMovies;
            return this;
        }

        public List<Movie> getMovies() {
            return movies;
        }

        public Response setMovies(List<Movie> movies) {
            this.movies = movies;
            return this;
        }
    }
}
