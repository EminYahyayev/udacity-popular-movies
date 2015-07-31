package com.ewintory.udacity.popularmovies.data.provider;

import android.app.Application;
import android.content.ContentResolver;
import android.database.sqlite.SQLiteOpenHelper;

import com.squareup.sqlbrite.BriteContentResolver;
import com.squareup.sqlbrite.SqlBrite;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

@Module(
        injects = {
                MoviesProvider.class
        },
        complete = false,
        library = true
)
public final class ProviderModule {

    @Provides
    @Singleton SqlBrite provideSqlBrite() {
        return SqlBrite.create(message -> Timber.tag("Database").v(message));
    }

    @Provides
    @Singleton ContentResolver provideContentResolver(Application application) {
        return application.getContentResolver();
    }

    @Provides
    @Singleton BriteContentResolver provideBrideContentResolver(SqlBrite sqlBrite, ContentResolver contentResolver) {
        return sqlBrite.wrapContentProvider(contentResolver);
    }

}
