package com.avenwu.himusic.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.CompoundButton;

import com.avenwu.himusic.R;

/**
 * @author chaobin
 * @date 11/27/13.
 */
public class PlayPauseButton extends CompoundButton {
    private Drawable mDrawableOn;//unchecked state
    private Drawable mDrawableOff;
    private OnPlayPauseListener mPlayPauseListener;

    public interface OnPlayPauseListener {
        public void onPlay();

        public void onPause();
    }

    public PlayPauseButton(Context context) {
        this(context, null);
    }

    public PlayPauseButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayPauseButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PlayPauseButton);
        mDrawableOn = a.getDrawable(R.styleable.PlayPauseButton_backgroundOn);
        mDrawableOff = a.getDrawable(R.styleable.PlayPauseButton_backgroundOff);
        if (mDrawableOff != null) setButtonDrawable(mDrawableOff);
        a.recycle();
    }

    @Override
    public void toggle() {
        super.toggle();
        if (isChecked()) {
            if (mPlayPauseListener != null) mPlayPauseListener.onPlay();
            setButtonDrawable(mDrawableOff);
        } else {
            if (mPlayPauseListener != null) mPlayPauseListener.onPause();
            setButtonDrawable(mDrawableOn);
        }
    }

    public void setOnPlayPauseListener(OnPlayPauseListener listener) {
        mPlayPauseListener = listener;
    }
}
