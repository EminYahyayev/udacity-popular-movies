package com.ewintory.udacity.popularmovies.ui.widget;

/**
 * Copyright (c) 2013 CommonsWare, LLC
 * Portions Copyright (C) 2009 The Android Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.ewintory.udacity.popularmovies.R;

import timber.log.Timber;

public class AspectLockedFrameLayout extends FrameLayout {
    private float aspectRatio = 0;
    private AspectRatioSource aspectRatioSource = null;

    public AspectLockedFrameLayout(Context context) {
        super(context, null);
    }

    public AspectLockedFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public AspectLockedFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AspectLockedFrameLayout, defStyle, 0);
            aspectRatio = a.getDimension(R.styleable.AspectLockedFrameLayout_aspectRatio, 0);
            a.recycle();
        }

        Timber.w("aspectRatio:" + aspectRatio);
    }

    // from com.android.camera.PreviewFrameLayout, with slight
    // modifications

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        float localRatio = aspectRatio;

        if (localRatio == 0.0 && aspectRatioSource != null
                && aspectRatioSource.getHeight() > 0) {
            localRatio =
                    (float) aspectRatioSource.getWidth()
                            / (float) aspectRatioSource.getHeight();
        }

        if (localRatio == 0.0) {
            super.onMeasure(widthSpec, heightSpec);
        } else {
            int lockedWidth = MeasureSpec.getSize(widthSpec);
            int lockedHeight = MeasureSpec.getSize(heightSpec);

            if (lockedWidth == 0 && lockedHeight == 0) {
                throw new IllegalArgumentException(
                        "Both width and height cannot be zero -- watch out for scrollable containers");
            }

            // Get the padding of the border background.
            int hPadding = getPaddingLeft() + getPaddingRight();
            int vPadding = getPaddingTop() + getPaddingBottom();

            // Resize the preview frame with correct aspect ratio.
            lockedWidth -= hPadding;
            lockedHeight -= vPadding;

            if (lockedHeight > 0 && (lockedWidth > lockedHeight * localRatio)) {
                lockedWidth = (int) (lockedHeight * localRatio + .5);
            } else {
                lockedHeight = (int) (lockedWidth / localRatio + .5);
            }

            // Add the padding of the border.
            lockedWidth += hPadding;
            lockedHeight += vPadding;

            // Ask children to follow the new preview dimension.
            super.onMeasure(MeasureSpec.makeMeasureSpec(lockedWidth,
                            MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(lockedHeight,
                            MeasureSpec.EXACTLY));
        }
    }

    public void setAspectRatioSource(View v) {
        this.aspectRatioSource = new ViewAspectRatioSource(v);
    }

    public void setAspectRatioSource(AspectRatioSource aspectRatioSource) {
        this.aspectRatioSource = aspectRatioSource;
    }

    // from com.android.camera.PreviewFrameLayout, with slight
    // modifications

    public void setAspectRatio(float aspectRatio) {
        if (aspectRatio <= 0.0) {
            throw new IllegalArgumentException(
                    "aspect ratio must be positive");
        }

        if (this.aspectRatio != aspectRatio) {
            this.aspectRatio = aspectRatio;
            requestLayout();
        }
    }

    public interface AspectRatioSource {
        int getWidth();

        int getHeight();
    }

    private static class ViewAspectRatioSource implements
            AspectRatioSource {
        private View v = null;

        ViewAspectRatioSource(View v) {
            this.v = v;
        }

        @Override
        public int getWidth() {
            return (v.getWidth());
        }

        @Override
        public int getHeight() {
            return (v.getHeight());
        }
    }
}
