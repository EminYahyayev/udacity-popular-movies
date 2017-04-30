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
import com.ewintory.udacity.popularmovies.ui.model.PageUiEvent;
import com.ewintory.udacity.popularmovies.ui.model.PagingModel;
import com.ewintory.udacity.popularmovies.ui.widget.BetterViewAnimator;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxrelay.BehaviorRelay;
import com.jakewharton.rxrelay.PublishRelay;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observable;
import rx.exceptions.OnErrorNotImplementedException;
import timber.log.Timber;

import static com.ewintory.udacity.popularmovies.data.repository.MoviesRepository.FIRST_PAGE;
import static com.ewintory.udacity.popularmovies.ui.EndlessStateAdapter.LOAD_MORE_STATE_ACTION;
import static com.ewintory.udacity.popularmovies.ui.EndlessStateAdapter.LOAD_MORE_STATE_NONE;
import static com.ewintory.udacity.popularmovies.ui.EndlessStateAdapter.LOAD_MORE_STATE_PROGRESS;
import static timber.log.Timber.d;
import static timber.log.Timber.i;

/**
 * @author Emin Yahyayev
 */
public final class MoviesFragment extends BaseFragment {

    @IdRes private static final int ANIMATOR_VIEW_CONTENT = R.id.recycler_view;
    @IdRes private static final int ANIMATOR_VIEW_EMPTY = R.id.empty_view;

    @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.view_animator) BetterViewAnimator viewAnimator;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;

    @Inject
    protected MoviesRepository moviesRepository;

    private MoviesAdapter adapter;
    private GridLayoutManager gridLayoutManager;

    private BehaviorRelay<PagingModel<Movie>> modelRelay;
    private PublishRelay<Integer> pagingRelay;

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        MoviesApp.get(getContext())
                .getDataComponent().inject(this);

        modelRelay = BehaviorRelay.create();
        pagingRelay = PublishRelay.create();

        adapter = new MoviesAdapter(this);
        adapter.setLoadMoreState(LOAD_MORE_STATE_PROGRESS);

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

        adapter.getLoadMoreClickObservable()
                .compose(bindToLifecycle())
                .doOnError(this::logError)
                .retry()
                .map(ignore -> modelRelay.getValue())
                .filter(model -> model != null)
                .map(model -> model.currentPage + 1)
                .doOnNext(page -> i("Request page: %d", page))
                .subscribe(pagingRelay);
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
                .switchMap(refresh -> pagingRelay.startWith(FIRST_PAGE)
                        .map(PageUiEvent::new))
                .doOnNext(event -> d("Event: %s", event));

        Observable<PageResult<Movie>> results = events
                .map(event -> new PageAction(event.page))
                .doOnNext(action -> d("Action: %s", action))
                .compose(moviesRepository.discover());

        results.scan(PagingModel.<Movie>init(true),
                (model, result) -> {
                    // Началась загрузка новой старницы, переводим в загрузку
                    if (result.inFlight)
                        return (result.page == FIRST_PAGE) ? PagingModel.init(true) : model.inProgress();
                    // Загрузились новая страница, добавляем данные
                    if (result.items != null)
                        return model.nextPage(result.items);
                    // Во время загрузки произошла ошибка
                    if (result.errorMessage != null)
                        return model.failure(result.errorMessage);
                    // Неизвестное состояние
                    throw new IllegalArgumentException("Unknown result: " + result);
                })
                .doOnNext(model -> d("Model: %s", model))
                .subscribe(modelRelay);

        modelRelay.compose(bindToLifecycle())
                .subscribe(this::handlePagingModel, t -> { throw new OnErrorNotImplementedException(t); });
    }

    @Override
    public void onDestroyView() {
        if (recyclerView != null)
            recyclerView.clearOnScrollListeners();
        super.onDestroyView();
    }

    private void handlePagingModel(PagingModel<Movie> model) {
        if (model.inProgress) {
            if (model.currentPage == PagingModel.NO_PAGE && !swipeRefreshLayout.isRefreshing()) {
                adapter.clear();
            }

            adapter.setLoadMoreState(LOAD_MORE_STATE_PROGRESS);
        } else {
            swipeRefreshLayout.setRefreshing(false);

            adapter.setItems(model.items);
            adapter.setLoadMoreState(model.allPagesLoaded
                    ? LOAD_MORE_STATE_NONE
                    : LOAD_MORE_STATE_ACTION);

            setAnimatorView(adapter.hasItems() ? ANIMATOR_VIEW_CONTENT : ANIMATOR_VIEW_EMPTY);

            if (model.errorMessage != null) {
                showToast(model.errorMessage);
            }
        }
    }

    protected final void setAnimatorView(@IdRes int resId) {
        if (viewAnimator != null)
            viewAnimator.setDisplayedChildId(resId);
        else
            Timber.w("viewAnimator == null");
    }

}
