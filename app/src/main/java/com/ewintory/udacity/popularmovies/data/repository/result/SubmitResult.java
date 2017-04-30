package com.ewintory.udacity.popularmovies.data.repository.result;

/**
 * @author Emin Yahyayev
 */
public class SubmitResult {

    public final int code;
    public final String errorMessage;

    private SubmitResult(String errorMessage) {
        this(errorMessage, 0);
    }

    public SubmitResult(String errorMessage, int code) {
        this.errorMessage = errorMessage;
        this.code = code;
    }

    public static final SubmitResult IN_FLIGHT = new SubmitResult(null);
    public static final SubmitResult SUCCESS = new SubmitResult(null, 200);

    public static SubmitResult failure(String errorMessage) {
        return failure(errorMessage, 0);
    }

    public static SubmitResult failure(String errorMessage, int code) {
        return new SubmitResult(errorMessage, code);
    }

    @Override public String toString() {
        return "SubmitResult{" +
                "code=" + code +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
