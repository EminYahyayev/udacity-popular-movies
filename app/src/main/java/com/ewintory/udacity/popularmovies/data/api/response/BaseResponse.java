package com.ewintory.udacity.popularmovies.data.api.response;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * @author Emin Yahyayev
 */
public class BaseResponse {

    @Nullable
    @SerializedName("status_code")
    public Integer statusCode;

    @Nullable
    @SerializedName("status_message")
    public String statusMessage;

    @Nullable
    public Boolean success;

}
