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

package com.ewintory.udacity.popularmovies.utils;

import java.util.List;


public final class StringUtils {

    private StringUtils() {
        throw new AssertionError("No instances.");
    }

    public static String join(List<String> strings, String delimiter) {
        return join(strings, delimiter, new StringBuilder(strings.size() * 8));
    }

    public static String joinIntegers(List<Integer> integers, String delimiter) {
        return joinIntegers(integers, delimiter, new StringBuilder(30));
    }


    public static String join(List<String> strings, String delimiter, StringBuilder builder) {
        builder.setLength(0);
        if (strings != null)
            for (String str : strings) {
                if (builder.length() > 0) builder.append(delimiter);
                builder.append(str);
            }
        return builder.toString();
    }

    public static String joinIntegers(List<Integer> integers, String delimiter, StringBuilder builder) {
        builder.setLength(0);
        if (integers != null)
            for (Integer integer : integers) {
                if (builder.length() > 0) builder.append(delimiter);
                builder.append(String.valueOf(integer));
            }
        return builder.toString();
    }
}
