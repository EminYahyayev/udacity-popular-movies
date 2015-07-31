package com.ewintory.udacity.popularmovies.data.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.ewintory.udacity.popularmovies.data.provider.MoviesContract;
import com.ewintory.udacity.popularmovies.data.provider.meta.GenreMeta;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;


public class Genre implements Parcelable, GenreMeta {

    @Expose
    private int id;

    @Expose
    private String name;

    public Genre() {}

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public Genre setId(int id) {
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

    // --------------------------------------------------------------------------------------

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
    }

    protected Genre(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
    }

    public static final Creator<Genre> CREATOR = new Creator<Genre>() {
        public Genre createFromParcel(Parcel source) {return new Genre(source);}

        public Genre[] newArray(int size) {return new Genre[size];}
    };

    public static class Response {

        @Expose
        public List<Genre> genres = new ArrayList<>();

    }
}
