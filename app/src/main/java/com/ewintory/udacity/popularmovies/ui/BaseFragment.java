package com.ewintory.udacity.popularmovies.ui;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.view.View;
import android.widget.Toast;

import com.ewintory.udacity.popularmovies.MoviesApp;
import com.squareup.leakcanary.RefWatcher;
import com.trello.rxlifecycle.components.support.RxFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.exceptions.OnErrorNotImplementedException;
import timber.log.Timber;

/**
 * Base class for all fragments. Binds views and watches memory leaks
 *
 * @author Emin Yahyayev (yahyayev@iteratia.com)
 * @see ButterKnife
 * @see RefWatcher
 */
public abstract class BaseFragment extends RxFragment {

    private Toast toast;
    private Unbinder unBinder;

    @CallSuper
    @Override public void onViewCreated(View view, Bundle savedState) {
        super.onViewCreated(view, savedState);
        unBinder = ButterKnife.bind(this, view);
    }

    @CallSuper
    @Override public void onDestroyView() {
        unBinder.unbind();
        super.onDestroyView();
    }

    @CallSuper
    @Override public void onDestroy() {
        super.onDestroy();
        MoviesApp.get(getActivity()).getRefWatcher().watch(this);
    }

    @SuppressWarnings("unused")
    protected final void showToast(String message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    protected final void showToast(String message, int duration) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getActivity(), message, duration);
        toast.show();
    }

    protected final void logError(Throwable throwable) {
        Timber.e(throwable, throwable.getMessage());
    }

    protected final void logError(String logTag, Throwable throwable) {
        Timber.tag(logTag).e(throwable, throwable.getMessage());
    }

    protected final void handleModelError(Throwable throwable) {
        throw new OnErrorNotImplementedException(throwable);
    }
}
