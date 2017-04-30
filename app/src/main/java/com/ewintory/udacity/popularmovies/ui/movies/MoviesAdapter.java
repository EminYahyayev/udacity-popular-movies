package com.ewintory.udacity.popularmovies.ui.movies;

import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.ui.EndlessStateAdapter;
import com.jakewharton.rxrelay.PublishRelay;

import java.lang.ref.WeakReference;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import timber.log.Timber;

import static android.text.TextUtils.isEmpty;

/**
 * @author Emin Yahyayev
 */
final class MoviesAdapter extends EndlessStateAdapter<Movie, MoviesAdapter.MovieHolder> {

    private final PublishRelay<Movie> itemClickRelay = PublishRelay.create();
    private final PublishRelay<Movie> favoriteClickRelay = PublishRelay.create();

    private final WeakReference<Fragment> fragmentRef;

    MoviesAdapter(@NonNull Fragment fragment) {
        super(fragment.getContext());
        fragmentRef = new WeakReference<>(fragment);
        setHasStableIds(true);
    }

    Observable<Movie> getItemClickObservable() {
        return itemClickRelay.asObservable();
    }

    Observable<Movie> getFavoriteClickObservable() {
        return favoriteClickRelay.asObservable();
    }

    @Override
    public long getItemId(int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            Movie movie = getItem(position);
            return movie != null ? movie.id() : RecyclerView.NO_ID;
        } else {
            return super.getItemId(position);
        }
    }

    @Override
    protected void onBindItemHolder(MovieHolder holder, Movie movie) {
        holder.favoriteButton.setSelected(movie.favored());
        holder.titleView.setText(movie.title());
        holder.genresView.setText(isEmpty(movie.genres())
                ? holder.emptyGenres : movie.genres());

        // prevents unnecessary color blinking
        // if (holder.movieId != movie.id()) {
        //    holder.resetColors();
        //    holder.movieId = movie.id();
        // }

        if (!fragmentRef.isEnqueued()) {
            Glide.with(fragmentRef.get())
                    .load(movie.posterPath())
                    .crossFade()
                    .placeholder(R.color.default_image_placeholder)
                    .error(R.color.default_image_error)
                    // .listener(GlidePalette.with(movie.posterPath())
                    //        .intoCallBack(palette -> holder.applyColors(palette.getVibrantSwatch())))
                    .into(holder.imageView);
        } else {
            Timber.w("Fragment reference is enqueued! Skip image loading...");
        }
    }

    @Override
    protected MovieHolder onCreateItemHolder(ViewGroup parent, int viewType) {
        return new MovieHolder(inflater.inflate(R.layout.item_movie, parent, false));
    }

    final class MovieHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_item_image) ImageView imageView;
        @BindView(R.id.movie_item_title) TextView titleView;
        @BindView(R.id.movie_item_genres) TextView genresView;
        @BindView(R.id.movie_item_footer) View footerView;
        @BindView(R.id.movie_item_btn_favorite) ImageButton favoriteButton;

        @BindColor(R.color.primary) int colorBackground;
        @BindColor(R.color.body_text_white) int colorTitle;
        @BindColor(R.color.body_text_1_inverse) int colorSubtitle;

        @BindString(R.string.movie_item_empty_genres) String emptyGenres;

        long movieId;

        MovieHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    itemClickRelay.call(getItem(position));
                }
            });

            favoriteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    favoriteClickRelay.call(getItem(position));
                }
            });
        }

        void resetColors() {
            footerView.setBackgroundColor(colorBackground);
            titleView.setTextColor(colorTitle);
            genresView.setTextColor(colorSubtitle);
            favoriteButton.setColorFilter(colorTitle, PorterDuff.Mode.MULTIPLY);
        }

        void applyColors(Palette.Swatch swatch) {
            if (swatch != null) {
                footerView.setBackgroundColor(swatch.getRgb());
                titleView.setTextColor(swatch.getBodyTextColor());
                genresView.setTextColor(swatch.getTitleTextColor());
                favoriteButton.setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.MULTIPLY);
            }
        }
    }

}
