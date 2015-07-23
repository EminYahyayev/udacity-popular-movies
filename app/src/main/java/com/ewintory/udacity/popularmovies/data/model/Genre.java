package com.ewintory.udacity.popularmovies.data.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.ewintory.udacity.popularmovies.data.db.Db;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;


public class Genre implements Parcelable {

    public static final String TABLE = "genre";

    public static final String ID = "_id";
    public static final String NAME = "name";

    @Expose
    private long id;

    @Expose
    private String name;

    public Genre() {}

    public Genre(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
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

    public static Func1<Cursor, List<Genre>> MAP = new Func1<Cursor, List<Genre>>() {
        @Override public List<Genre> call(final Cursor cursor) {
            try {
                List<Genre> values = new ArrayList<>(cursor.getCount());

                while (cursor.moveToNext()) {
                    long id = Db.getLong(cursor, ID);
                    String name = Db.getString(cursor, NAME);
                    values.add(new Genre(id, name));
                }
                return values;
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

        public Builder name(String name) {
            values.put(NAME, name);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }

    // --------------------------------------------------------------------------------------

    @Override public int describeContents() { return 0; }

    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
    }

    protected Genre(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
    }

    public static final Creator<Genre> CREATOR = new Creator<Genre>() {
        public Genre createFromParcel(Parcel source) {return new Genre(source);}

        public Genre[] newArray(int size) {return new Genre[size];}
    };
}
