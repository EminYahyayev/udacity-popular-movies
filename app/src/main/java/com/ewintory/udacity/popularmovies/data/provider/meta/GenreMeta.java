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

package com.ewintory.udacity.popularmovies.data.provider.meta;

import android.content.ContentValues;
import android.database.Cursor;

import com.ewintory.udacity.popularmovies.data.model.Genre;
import com.ewintory.udacity.popularmovies.data.provider.MoviesContract;
import com.ewintory.udacity.popularmovies.utils.DbUtils;
import com.squareup.sqlbrite.SqlBrite;

import java.util.HashMap;
import java.util.Map;

import rx.functions.Func1;


public interface GenreMeta {

    String[] PROJECTION = {
            MoviesContract.Genres._ID,
            MoviesContract.Genres.GENRE_ID,
            MoviesContract.Genres.GENRE_NAME
    };

    Func1<SqlBrite.Query, Map<Integer, Genre>> PROJECTION_MAP = query -> {
        Cursor cursor = query.run();
        try {
            Map<Integer, Genre> values = new HashMap<>(cursor.getCount());

            while (cursor.moveToNext()) {
                int id = DbUtils.getInt(cursor, MoviesContract.GenresColumns.GENRE_ID);
                String name = DbUtils.getString(cursor, MoviesContract.GenresColumns.GENRE_NAME);
                values.put(id, new Genre(id, name));
            }
            return values;
        } finally {
            cursor.close();
        }
    };

    final class Builder {
        private final ContentValues values = new ContentValues();

        public Builder id(int id) {
            values.put(MoviesContract.GenresColumns.GENRE_ID, id);
            return this;
        }

        public Builder name(String name) {
            values.put(MoviesContract.GenresColumns.GENRE_NAME, name);
            return this;
        }

        public ContentValues build() {
            return values;
        }
    }
}
