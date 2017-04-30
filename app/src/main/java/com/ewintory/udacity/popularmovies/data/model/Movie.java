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

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class Movie implements Parcelable {

    public abstract long id();

    public abstract String title();

    public abstract String overview();

    @SerializedName("release_date")
    public abstract String releaseDate();

    @Nullable
    @SerializedName("genre_ids")
    @SuppressWarnings("mutable")
    public abstract long[] genreIds();

    @Nullable
    public abstract String genres();

    @Nullable
    @SerializedName("poster_path")
    public abstract String posterPath();

    @Nullable
    @SerializedName("backdrop_path")
    public abstract String backdropPath();

    @SerializedName("vote_average")
    public abstract float voteAverage();

    @SerializedName("vote_count")
    public abstract long voteCount();

    public abstract float popularity();

    public abstract boolean favored();

    public static TypeAdapter<Movie> typeAdapter(Gson gson) {
        return new AutoValue_Movie.GsonTypeAdapter(gson)
                .setDefaultVoteCount(0)
                .setDefaultVoteAverage(0)
                .setDefaultPopularity(1)
                .setDefaultFavored(false);
    }


//    public String makeGenreIdsList() {
//        if (Lists.isEmpty(genreIds)) return "";
//
//        StringBuilder sb = new StringBuilder();
//        sb.append(genreIds.get(0));
//        for (int i = 1; i < genreIds.size(); i++) {
//            sb.append(",").append(genreIds.get(i));
//        }
//        return sb.toString();
//    }
//
//    // TODO: Think about possible problems here
//    public Movie putGenreIdsList(String ids) {
//        if (!TextUtils.isEmpty(ids)) {
//            genreIds = new ArrayList<>();
//            String[] strs = ids.split(",");
//            for (String s : strs)
//                genreIds.add(Integer.parseInt(s));
//        }
//        return this;
//    }
}
