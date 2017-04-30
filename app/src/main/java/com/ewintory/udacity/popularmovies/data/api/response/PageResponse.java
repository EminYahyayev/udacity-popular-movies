package com.ewintory.udacity.popularmovies.data.api.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Emin Yahyayev
 */
public final class PageResponse<T> extends BaseResponse {

    public int page;

    public List<T> results;

    @SerializedName("total_results")
    public int totalResults;

    @SerializedName("total_pages")
    public int totalPages;

}
