package com.ewintory.udacity.popularmovies.ui.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.ui.listener.MovieClickListener;
import com.ewintory.udacity.popularmovies.ui.widget.AspectLockedFrameLayout;
import com.ewintory.udacity.popularmovies.utils.Lists;
import com.ewintory.udacity.popularmovies.utils.MoviesHelper;
import com.ewintory.udacity.popularmovies.utils.ResourceUtils;
import com.ewintory.udacity.popularmovies.utils.StringUtils;
import com.github.florent37.glidepalette.BitmapPalette;
import com.github.florent37.glidepalette.GlidePalette;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;
import rx.functions.Action1;
import timber.log.Timber;

public final class MoviesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements Action1<List<Movie>> {

    public static final String PICASSO_TAG = "picasso_movies";

    @LayoutRes private static final int VIEW_TYPE_LOAD_MORE = R.layout.item_load_more;
    @LayoutRes private static final int VIEW_TYPE_MOVIE = R.layout.item_movie;

    /**
     * I store {@link Fragment} instead of {@link Context} in order to use Glide's Lifecycle integration
     *
     * @see <a href="http://github.com/bumptech/glide/wiki">Glide's wiki</a>
     */
    @NonNull private final Fragment fragment;
    @NonNull private final LayoutInflater inflater;
    @NonNull private final MoviesHelper moviesHelper;
    @NonNull private final Picasso picasso;

    @NonNull private MovieClickListener listener;
    @NonNull private List<Movie> movies;

    private boolean showLoadMore = false;

    public MoviesAdapter(Fragment fragment, @NonNull MovieClickListener listener, Picasso picasso) {
        this(fragment, listener, picasso, new ArrayList<Movie>());
    }

    public MoviesAdapter(Fragment fragment, @NonNull MovieClickListener listener, Picasso picasso, @NonNull List<Movie> movies) {
        inflater = LayoutInflater.from(fragment.getActivity());
        moviesHelper = new MoviesHelper(fragment.getActivity());
        this.fragment = fragment;
        this.listener = listener;
        this.picasso = picasso;
        this.movies = movies;
    }

    public void setListener(@NonNull MovieClickListener listener) {
        this.listener = listener;
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

    public boolean isLoadMore(int position) {
        return showLoadMore && (position == (getItemCount() - 1)); // At last position add one
    }

    @Override public int getItemViewType(int position) {
        return isLoadMore(position) ? VIEW_TYPE_LOAD_MORE : VIEW_TYPE_MOVIE;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_LOAD_MORE:
                return new RecyclerView.ViewHolder(inflater.inflate(VIEW_TYPE_LOAD_MORE, parent, false)) {};
            case VIEW_TYPE_MOVIE:
                return new MovieHolder(inflater.inflate(VIEW_TYPE_MOVIE, parent, false));
            default:
                throw new IllegalStateException("No such view type specified");
        }
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_MOVIE) {
            ((MovieHolder) holder).bind(movies.get(position), this);
        }
    }

    @Override public int getItemCount() {
        return movies.size() + ((showLoadMore) ? 1 : 0);
    }

    @Override public void call(List<Movie> newMovies) {
        add(newMovies);
    }

    public void add(List<Movie> newMovies) {
        if (newMovies == null)
            return;

        int currentSize = movies.size();
        int amountInserted = newMovies.size();

        movies.addAll(newMovies);
        notifyItemRangeInserted(currentSize, amountInserted);
    }

    public void clear() {
        movies.clear();
        notifyDataSetChanged();
    }

    public final class MovieHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.movie_item_container) View mContentContainer;
        @Bind(R.id.movie_item_image) ImageView mImageView;
        @Bind(R.id.movie_item_image_container) AspectLockedFrameLayout mImageContainer;
        @Bind(R.id.movie_item_title) TextView mTitle;
        @Bind(R.id.movie_item_genre) TextView mGenre;
        @Bind(R.id.movie_item_footer) View mFooter;
        @Bind(R.id.movie_item_btn_favorite) ImageButton mFavoriteButton;

        @BindColor(R.color.theme_primary) int mColorThemePrimary;
        @BindColor(R.color.body_text_1_inverse) int mColorBodyText1;
        @BindColor(R.color.body_text_2_inverse) int mColorBodyText2;

        public MovieHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(@NonNull final Movie movie, @NonNull final MoviesAdapter adapter) {
            mImageContainer.setAspectRatio(ResourceUtils.getFloatDimension(adapter.fragment.getResources(), R.dimen.movie_item_image_aspect_ratio));

            mContentContainer.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    adapter.listener.onContentClicked(movie, view);
                }
            });

            mFavoriteButton.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    adapter.listener.onFavoredClicked(movie, v);
                }
            });

            mTitle.setText(movie.getTitle());

            List<Integer> genreIds = movie.getGenreIds();
            if (!Lists.isEmpty(genreIds))
                mGenre.setText(StringUtils.join(adapter.moviesHelper.getGenreNames(genreIds), ", "));

            resetColors();
            Glide.with(adapter.fragment)
                    .load(movie.getPosterPath())
                    .load(movie.getPosterPath())
                    .placeholder(R.color.movie_image_placeholder)
                    .crossFade()
                    .listener(GlidePalette.with(movie.getPosterPath()).intoCallBack(new BitmapPalette.CallBack() {
                        @Override public void onPaletteLoaded(Palette palette) {
                            applyColors(palette.getVibrantSwatch());
                        }
                    }))
                    .into(mImageView);
        }

        private void resetColors() {
            mFooter.setBackgroundColor(mColorThemePrimary);
            mTitle.setTextColor(mColorBodyText1);
            mGenre.setTextColor(mColorBodyText2);
            mFavoriteButton.setColorFilter(mColorBodyText1, PorterDuff.Mode.MULTIPLY);
        }

        private void applyColors(Palette.Swatch swatch) {
            if (swatch != null) {
                mFooter.setBackgroundColor(swatch.getRgb());
                mTitle.setTextColor(swatch.getBodyTextColor());
                mGenre.setTextColor(swatch.getTitleTextColor());
                mFavoriteButton.setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.MULTIPLY);
            }
        }
    }
}

/**
 * //            adapter.picasso
 * //                    .load(movie.getPosterPath())
 * //                    .tag(PICASSO_TAG)
 * //                    .placeholder(R.color.movie_image_placeholder)
 * //                    .fit().centerCrop()
 * //                    .transform(PaletteTransformation.instance())
 * //                    .into(mImageView, new Callback.EmptyCallback() {
 * //                        @Override public void onSuccess() {
 * //                            Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
 * //                            Palette palette = PaletteTransformation.getPalette(bitmap);
 * //                            applyColors(palette.getVibrantSwatch());
 * //                        }
 * //                    });
 */
