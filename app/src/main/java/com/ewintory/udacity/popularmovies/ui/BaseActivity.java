package com.ewintory.udacity.popularmovies.ui;

import android.app.ProgressDialog;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.ewintory.udacity.popularmovies.R;
import com.squareup.leakcanary.RefWatcher;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;
import timber.log.Timber;


/**
 * Base class for all activities. Binds views and watches memory leaks
 *
 * @author Emin Yahyayev
 * @see ButterKnife
 * @see RefWatcher
 */
public abstract class BaseActivity extends RxAppCompatActivity {

    private Toast toast;
    private Snackbar snackbar;
    private ProgressDialog progressDialog;

    @CallSuper
    @Override public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Nullable
    protected View getSnackbarView() {
        return null;
    }

    protected final void showSnackbar(String message, @BaseTransientBottomBar.Duration int duration) {
        if (snackbar != null)
            snackbar.dismiss();

        if (getSnackbarView() != null) {
            snackbar = Snackbar.make(getSnackbarView(), message, duration);
            snackbar.show();
        }
    }

    @SuppressWarnings("unused")
    protected final void showToast(String message) {
        showToast(message, Toast.LENGTH_SHORT);
    }

    protected final void showToast(String message, int duration) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, message, duration);
        toast.show();
    }

    protected final void showProgressDialog(boolean show) {
        if (show)
            showProgressDialog();
        else
            hideProgressDialog();
    }

    protected final void showProgressDialog() {
        Timber.w("showProgressDialog");
        showProgressDialog(getString(R.string.msg_processing));
    }

    protected final void showProgressDialog(CharSequence message) {
        if (isDestroyed())
            return;

        if (progressDialog != null)
            progressDialog.dismiss();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    protected final void hideProgressDialog() {
        if (isDestroyed())
            return;

        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public final void logError(Throwable throwable) {
        Timber.e(throwable, throwable.getMessage());
    }
}
