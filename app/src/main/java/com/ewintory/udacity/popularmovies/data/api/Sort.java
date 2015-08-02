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

package com.ewintory.udacity.popularmovies.data.api;

import java.io.Serializable;

public enum Sort implements Serializable {

    POPULARITY("popularity.desc"),
    VOTE_AVERAGE("vote_average.desc"),
    VOTE_COUNT("vote_count.desc");

    private final String value;

    Sort(String value) {
        this.value = value;
    }

    @Override public String toString() {
        return value;
    }

    public static Sort fromString(String value) {
        if (value != null) {
            for (Sort sort : Sort.values()) {
                if (value.equalsIgnoreCase(sort.value)) {
                    return sort;
                }
            }
        }
        throw new IllegalArgumentException("No constant with text " + value + " found");
    }
}
