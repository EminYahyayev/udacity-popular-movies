package com.ewintory.udacity.popularmovies.ui.helper;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ShareCompat;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.data.model.Video;
import com.ewintory.udacity.popularmovies.data.repository.MoviesRepository;
import com.ewintory.udacity.popularmovies.utils.PrefUtils;

import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class MoviesHelper {

    private static final PublishSubject<FavoredEvent> FAVORED_SUBJECT = PublishSubject.create();

    private final Activity mActivity;
    private final MoviesRepository mRepository;

    public MoviesHelper(Activity activity, MoviesRepository moviesRepository) {
        mActivity = activity;
        mRepository = moviesRepository;
    }

    public Observable<FavoredEvent> getFavoredObservable() {
        return FAVORED_SUBJECT.asObservable();
    }

    public void setMovieFavored(Movie movie, boolean favored) {
        movie.setFavored(favored);
        if (favored) {
            mRepository.saveMovie(movie);
            PrefUtils.addToFavorites(mActivity, movie.getId());
        } else {
            mRepository.deleteMovie(movie);
            PrefUtils.removeFromFavorites(mActivity, movie.getId());
        }
        FAVORED_SUBJECT.onNext(new FavoredEvent(movie.getId(), favored));
    }

    public void playVideo(Video video) {
        if (video.getSite().equals(Video.SITE_YOUTUBE))
            mActivity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video.getKey())));
        else
            Timber.w("Unsupported video format");
    }

    public void shareTrailer(int messageTemplateResId, Video video) {
        mActivity.startActivity(Intent.createChooser(
                createShareIntent(messageTemplateResId, video.getName(), video.getKey()),
                mActivity.getString(R.string.title_share_trailer)));
    }

    public Intent createShareIntent(int messageTemplateResId, String title, String key) {
        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(mActivity)
                .setType("text/plain")
                .setText(mActivity.getString(messageTemplateResId, title, " http://www.youtube.com/watch?v=" + key));
        return builder.getIntent();
    }

    public static class FavoredEvent {
        public long movieId;
        public boolean favored;

        private FavoredEvent(long movieId, boolean favored) {
            this.movieId = movieId;
            this.favored = favored;
        }
    }
}
