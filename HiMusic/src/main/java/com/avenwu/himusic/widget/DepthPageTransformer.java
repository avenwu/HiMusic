package com.avenwu.himusic.widget;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

/**
 * http://developer.android.com/training/animation/screen-slide.html
 *
 * @author chaobin
 * @date 11/18/13.
 */
public class DepthPageTransformer implements ViewPager.PageTransformer {
    private final float MIN_SCALE = 0.75f;

    @Override
    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();
        if (position < -1) {//[-Infinity,-1)
            // This page is way off-screen to the left.
            ViewHelper.setAlpha(page, 0);
        } else if (position < 0) {//[-1,0]
            // Use the default slide transition when moving to the left page
            ViewHelper.setAlpha(page, 1);
            ViewHelper.setTranslationX(page, 0);
            ViewHelper.setScaleX(page, 1);
            ViewHelper.setScaleY(page, 1);
        } else if (position <= 1) {//(0,1]
            // Fade the page out.
            ViewHelper.setAlpha(page, 1 - position);
            // Counteract the default slide transition
            ViewHelper.setTranslationX(page, -pageWidth * position);
            // Scale the page down (between MIN_SCALE and 1)
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            ViewHelper.setScaleX(page, scaleFactor);
            ViewHelper.setScaleY(page, scaleFactor);
        } else {//(1, +Infinity]
            // This page is way off-screen to the right.
            ViewHelper.setAlpha(page, 0);
        }
    }
}
