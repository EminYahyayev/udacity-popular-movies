package com.ewintory.udacity.popularmovies.ui.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.ui.listener.MovieClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

public final class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Action1<List<Movie>> {

    @LayoutRes
    private static final int VIEW_TYPE_LOAD_MORE = R.layout.item_load_more;
    @LayoutRes
    private static final int VIEW_TYPE_MOVIE = R.layout.item_movie;

    /**
     * I store {@link Fragment} instead of {@link Context} in order to use Glide's Lifecycle integration
     *
     * @see <a href="http://github.com/bumptech/glide/wiki">Glide's wiki</a>
     */
    private final Fragment mFragment;
    private final LayoutInflater mInflater;

    @NonNull private List<Movie> mMovies;
    @NonNull private MovieClickListener mListener;

    private boolean showLoadMore = false;

    public MoviesAdapter(Fragment fragment, @NonNull MovieClickListener listener) {
        this(fragment, listener, new ArrayList<Movie>());
    }

    public MoviesAdapter(Fragment fragment, @NonNull MovieClickListener listener, @NonNull List<Movie> movies) {
        mInflater = LayoutInflater.from(fragment.getActivity());
        mFragment = fragment;
        mListener = listener;
        mMovies = movies;
    }

    public void setListener(@NonNull MovieClickListener listener) {
        mListener = listener;
    }

    public void setLoadMore(boolean enabled) {
        if (showLoadMore != enabled) {
            if (showLoadMore) {
                notifyItemRemoved(getItemCount()); // Remove last position
                showLoadMore = false;
            } else {
                notifyItemInserted(getItemCount());
                showLoadMore = true;
            }
        }
    }

    @Override public int getItemViewType(int position) {
        return (showLoadMore && position == (getItemCount() - 1)) // At last position add one
                ? VIEW_TYPE_LOAD_MORE
                : VIEW_TYPE_MOVIE;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_LOAD_MORE:
                return new RecyclerView.ViewHolder(mInflater.inflate(VIEW_TYPE_LOAD_MORE, parent, false)) {};
            case VIEW_TYPE_MOVIE:
                return new MovieHolder(mInflater.inflate(VIEW_TYPE_MOVIE, parent, false));
            default:
                throw new IllegalStateException("No such view type specified");
        }
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_MOVIE) {
            ((MovieHolder) holder).bind(mMovies.get(position), mListener, mFragment);
        }
    }

    @Override public int getItemCount() {
        return mMovies.size() + ((showLoadMore) ? 1 : 0);
    }

    @Override public void call(List<Movie> newMovies) {
        add(newMovies);
    }

    public void add(List<Movie> newMovies) {
        if (newMovies == null)
            return;

        int currentSize = mMovies.size();
        int amountInserted = newMovies.size();

        mMovies.addAll(newMovies);
        notifyItemRangeInserted(currentSize, amountInserted);
    }

    public void clear() {
        mMovies.clear();
        notifyDataSetChanged();
    }

    public static final class MovieHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.movie_container) View mContainer;
        @Bind(R.id.movie_poster) ImageView mImageView;

        public MovieHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(@NonNull final Movie movie, @NonNull final MovieClickListener listener, final Fragment fragment) {

            mContainer.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    listener.onContentClicked(movie, view);
                }
            });

            Glide.with(fragment)
                    .load(movie.getPosterPath())
                    .error(R.drawable.egg_error)
                    .into(mImageView);
        }
    }
}
