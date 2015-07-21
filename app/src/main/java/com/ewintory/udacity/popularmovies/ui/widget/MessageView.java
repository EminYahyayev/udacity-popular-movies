package com.ewintory.udacity.popularmovies.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ewintory.udacity.popularmovies.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class MessageView extends LinearLayout {

    @Bind(R.id.message_view_text) TextView mTextView;
    @Bind(R.id.message_view_image) ImageView mImageView;

    private View mRoot;

    private MessageView(Context context) {
        super(context, null, 0);
        initialize(context, null, 0);
    }

    public MessageView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initialize(context, attrs, 0);
    }

    private MessageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyle) {
        mRoot = LayoutInflater.from(context).inflate(R.layout.view_message, this, true);
        ButterKnife.bind(this, mRoot);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MessageView, defStyle, 0);

        String text = a.getString(R.styleable.MessageView_messageText);
        Drawable image = a.getDrawable(R.styleable.MessageView_messageImage);

        mTextView.setText(text);
        mImageView.setImageDrawable(image);

        a.recycle();
    }
}
