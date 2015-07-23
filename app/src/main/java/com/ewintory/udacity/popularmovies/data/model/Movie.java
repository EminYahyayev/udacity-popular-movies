package com.ewintory.udacity.popularmovies.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.ewintory.udacity.popularmovies.data.db.Db;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.squareup.sqlbrite.SqlBrite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.functions.Func1;

import static com.squareup.sqlbrite.SqlBrite.Query;


public final class Movie implements Parcelable {
    public static final String TABLE = "movie";

    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String GENRE_IDS = "genre_ids";
    public static final String OVERVIEW = "overview";
    public static final String POPULARITY = "popularity";
    public static final String VOTE_COUNT = "vote_count";
    public static final String VOTE_AVERAGE = "vote_average";
    public static final String FAVORED = "favored";
    public static final String POSTER_PATH = "poster_path";
    public static final String BACKDROP_PATH = "backdrop_path";

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

    @Override public String toString() {
        return "Movie{" +
                "backdropPath='" + backdropPath + '\'' +
                ", genreIds=" + genreIds +
                ", id=" + id +
                ", overview='" + overview + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", popularity=" + popularity +
                ", title='" + title + '\'' +
                ", voteAverage=" + voteAverage +
                ", voteCount=" + voteCount +
                '}';
    }

    public static final Func1<SqlBrite.Query, List<Movie>> MAP = new Func1<Query, List<Movie>>() {
        @Override public List<Movie> call(Query query) {
            Cursor cursor = query.run();
            try {
                List<Movie> values = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    values.add(new Movie()
                            .setId(Db.getLong(cursor, ID))
                            .setTitle(Db.getString(cursor, TITLE))
                            .setPosterPath(Db.getString(cursor, POSTER_PATH))
                            .setBackdropPath(Db.getString(cursor, BACKDROP_PATH))
                            .setFavored(Db.getBoolean(cursor, FAVORED))
                            .setPopularity(Db.getDouble(cursor, POPULARITY))
                            .setVoteCount(Db.getInt(cursor, VOTE_COUNT))
                            .setVoteAverage(Db.getDouble(cursor, VOTE_AVERAGE))
                            .setOverview(Db.getString(cursor, OVERVIEW)));
                }
                return values;
            } finally {
                cursor.close();
            }
        }
    };

    public static final Func1<SqlBrite.Query, Set<Long>> ID_MAP = new Func1<Query, Set<Long>>() {
        @Override public Set<Long> call(Query query) {
            Cursor cursor = query.run();
            try {
                Set<Long> idSet = new HashSet<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    idSet.add(Db.getLong(cursor, ID));
                }
                return idSet;
            } finally {
                cursor.close();
            }
        }
    };

    public static final class Builder {
        private final ContentValues values = new ContentValues();

        public Builder id(long id) {
            values.put(ID, id);
            return this;
        }

        public Builder title(String title) {
            values.put(TITLE, title);
            return this;
        }

//        public Builder listId(List<Long> genreIds) {
//            values.put(GENRE_IDS, genreIds);
//            return this;
//        }

        public Builder overview(String overview) {
            values.put(OVERVIEW, overview);
            return this;
        }

        public Builder backdropPath(String backdropPath) {
            values.put(BACKDROP_PATH, backdropPath);
            return this;
        }

        public Builder posterPath(String posterPath) {
            values.put(POSTER_PATH, posterPath);
            return this;
        }

        public Builder voteCount(long voteCount) {
            values.put(VOTE_COUNT, voteCount);
            return this;
        }

        public Builder voteAverage(double voteCount) {
            values.put(VOTE_AVERAGE, voteCount);
            return this;
        }

        public Builder popularity(double popularity) {
            values.put(POPULARITY, popularity);
            return this;
        }

        public Builder favored(boolean favored) {
            values.put(FAVORED, favored);
            return this;
        }

        public Builder movie(Movie movie) {
            return id(movie.getId())
                    .title(movie.getTitle())
                    .overview(movie.getOverview())
                    .backdropPath(movie.getBackdropPath())
                    .posterPath(movie.getPosterPath())
                    .popularity(movie.getPopularity())
                    .voteCount(movie.getVoteCount())
                    .voteAverage(movie.getVoteAverage())
                    .favored(movie.isFavored());
        }

        public ContentValues build() {
            return values; // TODO defensive copy?
        }
    }


    // --------------------------------------------------------------------------------------


    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
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
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {return new Movie(source);}

        public Movie[] newArray(int size) {return new Movie[size];}
    };
}
