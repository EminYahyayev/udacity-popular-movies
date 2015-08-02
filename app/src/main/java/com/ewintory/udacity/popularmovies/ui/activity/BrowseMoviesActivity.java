/*
 * Copyright 2015.  Emin Yahyayev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ewintory.udacity.popularmovies.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.data.api.Sort;
import com.ewintory.udacity.popularmovies.data.model.Movie;
import com.ewintory.udacity.popularmovies.ui.fragment.FavoredMoviesFragment;
import com.ewintory.udacity.popularmovies.ui.fragment.MovieFragment;
import com.ewintory.udacity.popularmovies.ui.fragment.MoviesFragment;
import com.ewintory.udacity.popularmovies.ui.fragment.SortedMoviesFragment;
import com.ewintory.udacity.popularmovies.utils.PrefUtils;

import java.util.ArrayList;

import timber.log.Timber;

public final class BrowseMoviesActivity extends BaseActivity implements MoviesFragment.Listener {
    private static final String STATE_MODE = "state_mode";

    private static final String MOVIES_FRAGMENT_TAG = "fragment_movies";
    private static final String MOVIE_DETAILS_FRAGMENT_TAG = "fragment_movie_details";

    public static final String MODE_FAVORITES = "favorites";

    private ModeSpinnerAdapter mSpinnerAdapter = new ModeSpinnerAdapter();
    private MoviesFragment mMoviesFragment;
    private String mMode;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_movies);

        mTwoPane = findViewById(R.id.movie_details_container) != null;

        mMode = (savedInstanceState != null) ?
                savedInstanceState.getString(STATE_MODE, Sort.POPULARITY.toString())
                : PrefUtils.getBrowseMoviesMode(this);

        initModeSpinner();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mMoviesFragment = (MoviesFragment) getSupportFragmentManager().findFragmentByTag(MOVIES_FRAGMENT_TAG);
        if (mMoviesFragment == null)
            replaceMoviesFragment(mMode.equals(MODE_FAVORITES)
                    ? new FavoredMoviesFragment()
                    : SortedMoviesFragment.newInstance(Sort.fromString(mMode)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_browse_movies, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_refresh:
                mMoviesFragment.onRefresh();
                break;
            case R.id.menu_scroll_to_top:
                mMoviesFragment.scrollToTop(true);
                break;
            case R.id.menu_settings:
                startActivity(new Intent(this, PreferenceActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_MODE, mMode);
    }

    @Override
    protected void onPause() {
        PrefUtils.setBrowseMoviesMode(this, mMode);
        super.onPause();
    }

    @Override
    public void onMovieSelected(@NonNull Movie movie, View view) {
        Timber.d(String.format("Movie '%s' selected", movie.getTitle()));

        if (mTwoPane) {
            MovieFragment fragment = MovieFragment.newInstance(movie);
            replaceMovieDetailsFragment(fragment);
        } else {
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            intent.putExtra(MovieDetailsActivity.EXTRA_MOVIE, movie);
            startActivity(intent);
        }
    }

    private void replaceMovieDetailsFragment(MovieFragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movie_details_container, fragment, MOVIE_DETAILS_FRAGMENT_TAG)
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .commit();
    }

    private void replaceMoviesFragment(MoviesFragment fragment) {
        mMoviesFragment = fragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.movies_container, fragment, MOVIES_FRAGMENT_TAG)
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .commit();
    }

    private void initModeSpinner() {
        Toolbar toolbar = getToolbar();
        if (toolbar == null)
            return;

        mSpinnerAdapter.clear();
        mSpinnerAdapter.addItem(MODE_FAVORITES, getString(R.string.mode_favored), false);
        mSpinnerAdapter.addHeader(getString(R.string.mode_sort));
        mSpinnerAdapter.addItem(Sort.POPULARITY.toString(), getString(R.string.mode_sort_popularity), false);
        mSpinnerAdapter.addItem(Sort.VOTE_COUNT.toString(), getString(R.string.mode_sort_vote_count), false);
        mSpinnerAdapter.addItem(Sort.VOTE_AVERAGE.toString(), getString(R.string.mode_sort_vote_average), false);

        int itemToSelect = -1;

        if (mMode.equals(MODE_FAVORITES))
            itemToSelect = 0;
        else if (mMode.equals(Sort.POPULARITY.toString()))
            itemToSelect = 2;
        else if (mMode.equals(Sort.VOTE_COUNT.toString()))
            itemToSelect = 3;
        else if (mMode.equals(Sort.VOTE_AVERAGE.toString()))
            itemToSelect = 4;

        View spinnerContainer = LayoutInflater.from(this).inflate(R.layout.widget_toolbar_spinner, toolbar, false);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        toolbar.addView(spinnerContainer, lp);

        Spinner spinner = (Spinner) spinnerContainer.findViewById(R.id.mode_spinner);
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {
                onModeSelected(mSpinnerAdapter.getMode(position));
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        if (itemToSelect >= 0) {
            Timber.d("Restoring item selection to mode spinner: " + itemToSelect);
            spinner.setSelection(itemToSelect);
        }
    }

    private void onModeSelected(String mode) {
        if (mode.equals(mMode)) return;
        mMode = mode;

        if (mMode.equals(MODE_FAVORITES))
            replaceMoviesFragment(new FavoredMoviesFragment());
        else
            replaceMoviesFragment(SortedMoviesFragment.newInstance(Sort.fromString(mMode)));
    }

    private class ModeSpinnerItem {
        boolean isHeader;
        String mode, title;
        boolean indented;

        ModeSpinnerItem(boolean isHeader, String mode, String title, boolean indented) {
            this.isHeader = isHeader;
            this.mode = mode;
            this.title = title;
            this.indented = indented;
        }
    }

    private class ModeSpinnerAdapter extends BaseAdapter {

        private ModeSpinnerAdapter() { }

        private ArrayList<ModeSpinnerItem> mItems = new ArrayList<ModeSpinnerItem>();

        public void clear() {
            mItems.clear();
        }

        public void addItem(String tag, String title, boolean indented) {
            mItems.add(new ModeSpinnerItem(false, tag, title, indented));
        }

        public void addHeader(String title) {
            mItems.add(new ModeSpinnerItem(true, "", title, false));
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private boolean isHeader(int position) {
            return position >= 0 && position < mItems.size()
                    && mItems.get(position).isHeader;
        }

        @Override
        public View getDropDownView(int position, View view, ViewGroup parent) {
            if (view == null || !view.getTag().toString().equals("DROPDOWN")) {
                view = getLayoutInflater().inflate(R.layout.item_toolbar_spinner_dropdown,
                        parent, false);
                view.setTag("DROPDOWN");
            }

            TextView headerTextView = (TextView) view.findViewById(R.id.header_text);
            View dividerView = view.findViewById(R.id.divider_view);
            TextView normalTextView = (TextView) view.findViewById(android.R.id.text1);

            if (isHeader(position)) {
                headerTextView.setText(getTitle(position));
                headerTextView.setVisibility(View.VISIBLE);
                normalTextView.setVisibility(View.GONE);
                dividerView.setVisibility(View.VISIBLE);
            } else {
                headerTextView.setVisibility(View.GONE);
                normalTextView.setVisibility(View.VISIBLE);
                dividerView.setVisibility(View.GONE);

                setUpNormalDropdownView(position, normalTextView);
            }

            return view;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null || !view.getTag().toString().equals("NON_DROPDOWN")) {
                view = getLayoutInflater().inflate(R.layout.item_toolbar_spinner, parent, false);
                view.setTag("NON_DROPDOWN");
            }
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            textView.setText(getTitle(position));
            return view;
        }

        private String getTitle(int position) {
            return position >= 0 && position < mItems.size() ? mItems.get(position).title : "";
        }

        private String getMode(int position) {
            return position >= 0 && position < mItems.size() ? mItems.get(position).mode : "";
        }

        private void setUpNormalDropdownView(int position, TextView textView) {
            textView.setText(getTitle(position));
        }

        @Override
        public boolean isEnabled(int position) {
            return !isHeader(position);
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }
    }
}
