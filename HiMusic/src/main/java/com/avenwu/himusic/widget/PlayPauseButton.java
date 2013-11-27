package com.avenwu.himusic.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

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

    public void setOnPlayPauseListener(OnPlayPauseListener listener) {
        mPlayPauseListener = listener;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked()) {
                    play();
                } else {
                    pause();
                }
            }
        });
    }

    public void play() {
        mPlayPauseListener.onPlay();
        setButtonDrawable(mDrawableOn);
    }

    public void pause() {
        mPlayPauseListener.onPause();
        setButtonDrawable(mDrawableOff);
    }
}
