package com.ewintory.udacity.popularmovies.data.repository.result;

/**
 * @author Emin Yahyayev
 */
public class ValueResult<T> {

    public final T value;
    public final int code;
    public final String errorMessage;

    private ValueResult(T value) {
        this.errorMessage = null;
        this.code = 200;
        this.value = value;
    }

    private ValueResult(String errorMessage) {
        this(errorMessage, 0);
    }

    private ValueResult(String errorMessage, int code) {
        this.errorMessage = errorMessage;
        this.code = code;
        this.value = null;
    }

    public boolean isSuccessful() {
        return value != null;
    }

    public static final ValueResult IN_FLIGHT = new ValueResult<>(null);

    @SuppressWarnings("unchecked")
    public static <T> ValueResult<T> inFlight() {
        return (ValueResult<T>) IN_FLIGHT;
    }

    public static <T> ValueResult<T> success(T value) {return new ValueResult<>(value);}

    public static <T> ValueResult<T> failure(String errorMessage) {
        return new ValueResult<>(errorMessage, 0);
    }

    public static <T> ValueResult<T> failure(String errorMessage, int code) {
        return new ValueResult<>(errorMessage, code);
    }

    @Override public String toString() {
        return "ValueResult{" +
                "value=" + value +
                ", code=" + code +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
