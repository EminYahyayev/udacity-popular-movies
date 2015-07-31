package com.ewintory.udacity.popularmovies.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.ewintory.udacity.popularmovies.MoviesApp;
import com.squareup.leakcanary.RefWatcher;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import rx.Observable;
import rx.android.app.AppObservable;

/**
 * Base class for all fragments.
 * Binds views, watches memory leaks and performs dependency injections
 *
 * @see ButterKnife
 * @see RefWatcher
 * @see ObjectGraph
 */
public abstract class BaseFragment extends Fragment {

    private ObjectGraph mObjectGraph;
    private Toast mToast;

    @CallSuper
    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        buildObjectGraph(activity);
    }

    @CallSuper
    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @CallSuper
    @Override public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @CallSuper
    @Override public void onDestroy() {
        mObjectGraph = null;
        super.onDestroy();
        MoviesApp.get(getActivity()).getRefWatcher().watch(this);
    }

    private void buildObjectGraph(Activity activity) {
        Object[] modules = getModules().toArray();
        if (modules.length > 0) {
            mObjectGraph = MoviesApp.get(activity).buildScopedObjectGraph(modules);
            mObjectGraph.inject(this);
        }
    }

    protected void showToast(String message) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    protected void showToast(@StringRes int resId) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(getActivity(), resId, Toast.LENGTH_SHORT);
        mToast.show();
    }

    protected List<Object> getModules() {
        return Collections.emptyList();
    }

    /**
     * Binds the given source sequence to a support-v4 fragment.
     *
     * @see AppObservable#bindSupportFragment(Fragment, Observable)
     */
    protected final <T> Observable<T> bind(Observable<T> source) {
        return AppObservable.bindSupportFragment(this, source);
    }
}
