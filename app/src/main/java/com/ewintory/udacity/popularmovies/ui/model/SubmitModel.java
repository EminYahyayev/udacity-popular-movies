package com.ewintory.udacity.popularmovies.ui.model;

/**
 * @author Emin Yahyayev
 */
public class SubmitModel {

    public final boolean inProgress;
    public final boolean success;
    public final String errorMessage;

    private SubmitModel(boolean inProgress, boolean success, String errorMessage) {
        this.inProgress = inProgress;
        this.success = success;
        this.errorMessage = errorMessage;
    }

    public static SubmitModel idle() {
        return new SubmitModel(false, false, null);
    }

    public static SubmitModel inProgress() {
        return new SubmitModel(true, false, null);
    }

    public static SubmitModel success() {
        return new SubmitModel(false, true, null);
    }

    public static SubmitModel failure(String errorMessage) {
        return new SubmitModel(false, false, errorMessage);
    }

    @Override public String toString() {
        return "SubmitModel{" +
                "inProgress=" + inProgress +
                ", success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
