package com.ewintory.udacity.popularmovies.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v4.content.IntentCompat;
import android.support.v7.widget.Toolbar;

import com.ewintory.udacity.popularmovies.R;
import com.ewintory.udacity.popularmovies.utils.PrefUtils;

public final class PreferenceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = getToolbar();
        if (toolbar != null) {
            toolbar.setTitle(R.string.title_settings);
            toolbar.setNavigationIcon(R.drawable.ic_up);
            toolbar.setNavigationOnClickListener(view -> navigateUpToFromChild(PreferenceActivity.this,
                    IntentCompat.makeMainActivity(new ComponentName(PreferenceActivity.this, BrowseMoviesActivity.class))));
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new SettingsFragment())
                    .commit();
        }
    }

    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        public SettingsFragment() { }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            PrefUtils.registerOnSharedPreferenceChangeListener(getActivity(), this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            PrefUtils.unregisterOnSharedPreferenceChangeListener(getActivity(), this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {}
    }

}
