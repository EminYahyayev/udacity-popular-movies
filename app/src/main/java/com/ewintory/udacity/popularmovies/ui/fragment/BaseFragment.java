package com.ewintory.udacity.popularmovies.ui.fragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;
import android.view.View;

import com.ewintory.udacity.popularmovies.App;

import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import dagger.ObjectGraph;
import rx.Observable;
import rx.android.app.AppObservable;

/**
 * Base class for all fragments.
 * Binds views and performs dependency injections
 */
public abstract class BaseFragment extends Fragment {

    private ObjectGraph mObjectGraph;

    @CallSuper
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildObjectGraph();
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
        App.get(getActivity()).getRefWatcher().watch(this);
    }

    private void buildObjectGraph() {
        Object[] modules = getModules().toArray();
        if (modules.length > 0) {
            mObjectGraph = App.get(getActivity()).buildScopedObjectGraph(modules);
            mObjectGraph.inject(this);
        }
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
