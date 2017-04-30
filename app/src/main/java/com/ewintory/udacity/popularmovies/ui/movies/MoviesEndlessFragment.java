package com.ewintory.udacity.popularmovies.ui.movies;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ewintory.udacity.popularmovies.MoviesApp;
import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.data.repository.MoviesRepository;
import com.ewintory.udacity.popularmovies.data.repository.action.PageAction;
import com.ewintory.udacity.popularmovies.data.repository.result.PageResult;
import com.ewintory.udacity.popularmovies.ui.BaseFragment;
import com.ewintory.udacity.popularmovies.ui.listener.EndlessScrollListener;
import com.ewintory.udacity.popularmovies.ui.listener.EndlessScrollListener.OnLoadMoreCallback;
import com.ewintory.udacity.popularmovies.ui.model.PageUiEvent;
import com.ewintory.udacity.popularmovies.ui.model.PagingModel;
import com.ewintory.udacity.popularmovies.ui.widget.BetterViewAnimator;
import com.ewintory.udacity.popularmovies.utils.RxUtils;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxrelay.PublishRelay;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observable;
import rx.exceptions.OnErrorNotImplementedException;
import timber.log.Timber;

import static com.ewintory.udacity.popularmovies.ui.EndlessStateAdapter.LOAD_MORE_STATE_ACTION;
import static com.ewintory.udacity.popularmovies.ui.EndlessStateAdapter.LOAD_MORE_STATE_NONE;
import static com.ewintory.udacity.popularmovies.ui.EndlessStateAdapter.LOAD_MORE_STATE_PROGRESS;
import static timber.log.Timber.d;

/**
 * @author Emin Yahyayev
 */
public final class MoviesEndlessFragment extends BaseFragment implements OnLoadMoreCallback {

    private static final int VISIBLE_PAGE_THRESHOLD = 4;

    @IdRes private static final int ANIMATOR_VIEW_CONTENT = R.id.recycler_view;
    @IdRes private static final int ANIMATOR_VIEW_EMPTY = R.id.empty_view;

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.view_animator) BetterViewAnimator viewAnimator;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    @Inject
    protected MoviesRepository moviesRepository;

    // private LinearLayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    private EndlessScrollListener endlessScrollListener;

    private PublishRelay<Void> nextPageRelay = PublishRelay.create();

    private MoviesAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        MoviesApp.get(getContext())
                .getDataComponent().inject(this);

        adapter = new MoviesAdapter(this);

        adapter.getItemClickObservable()
                .compose(bindToLifecycle())
                .doOnError(this::logError)
                .retry()
                .subscribe(movie -> showToast(movie.title()));

        adapter.getFavoriteClickObservable()
                .compose(bindToLifecycle())
                .doOnError(this::logError)
                .retry()
                .subscribe(movie -> showToast(movie.title()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedState) {
        return inflater.inflate(R.layout.fragment_movies, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedState) {
        super.onViewCreated(view, savedState);

        swipeRefreshLayout.setColorSchemeColors(getResources()
                .getIntArray(R.array.swipe_progress_colors));

        Observable<Void> refreshObservable = RxSwipeRefreshLayout.refreshes(swipeRefreshLayout)
                .doOnNext(ignore -> d("SwipeRefresh received."));

        // layoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), getResources().getInteger(R.integer.movies_columns));
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override public int getSpanSize(int position) {
                int spanCount = gridLayoutManager.getSpanCount();
                return (adapter.isLoadMorePosition(position) /* && (position % spanCount == 0) */) ? spanCount : 1;
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gridLayoutManager);

        Observable<Void> refreshes = refreshObservable.startWith((Void) null);

        Observable<PageUiEvent> events = refreshes
                .flatMap(refresh -> nextPageRelay.scan(1, (page, ignore) -> page + 1)
                        .map(PageUiEvent::new))
                .doOnNext(event -> d("Event: %s", event));

        Observable<PageResult<Movie>> results = events
                .map(event -> new PageAction(event.page))
                .doOnNext(action -> d("Action: %s", action))
                .compose(moviesRepository.discover());

        Observable<PagingModel<Movie>> models = results
                .compose(RxUtils.pageResultToModel())
                .doOnNext(model -> d("Model: %s", model));

        models.compose(bindToLifecycle())
                .subscribe(model -> {
                    if (model.inProgress) {
                        if (model.currentPage == PagingModel.NO_PAGE) {
                            adapter.clear();

                            if (endlessScrollListener != null) {
                                recyclerView.removeOnScrollListener(endlessScrollListener);
                                endlessScrollListener.setCallback(OnLoadMoreCallback.DUMMY);
                            }

                            endlessScrollListener = EndlessScrollListener
                                    .fromGridLayoutManager(gridLayoutManager, VISIBLE_PAGE_THRESHOLD, 0)
                                    .setCallback(this);
                            recyclerView.addOnScrollListener(endlessScrollListener);
                        }

                        adapter.setLoadMoreState(LOAD_MORE_STATE_PROGRESS);
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        adapter.setItems(model.items);
                        setAnimatorView(adapter.hasItems() ? ANIMATOR_VIEW_CONTENT : ANIMATOR_VIEW_EMPTY);

                        if (model.errorMessage != null) {
                            showToast(model.errorMessage);
                            adapter.setLoadMoreState(model.allPagesLoaded
                                    ? LOAD_MORE_STATE_NONE
                                    : LOAD_MORE_STATE_ACTION);
                        } else {
                            adapter.setLoadMoreState(model.allPagesLoaded
                                    ? LOAD_MORE_STATE_NONE
                                    : LOAD_MORE_STATE_PROGRESS);
                        }
                    }
                }, t -> { throw new OnErrorNotImplementedException(t); });
    }

    @Override
    public void onDestroyView() {
        if (recyclerView != null)
            recyclerView.clearOnScrollListeners();
        super.onDestroyView();
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (adapter.getLoadMoreState() == LOAD_MORE_STATE_PROGRESS)
            nextPageRelay.call(null);
    }

    protected final void setAnimatorView(@IdRes int resId) {
        if (viewAnimator != null)
            viewAnimator.setDisplayedChildId(resId);
        else
            Timber.w("viewAnimator == null");
    }

}
