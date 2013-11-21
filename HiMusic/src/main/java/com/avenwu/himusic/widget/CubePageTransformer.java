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
public class CubePageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View page, float position) {
        int pageWidth = page.getWidth();
        if (position < -1) {//[-Infinity,-1)
            // This page is way off-screen to the left.
            ViewHelper.setAlpha(page, 0);
        } else if (position < 0) {//[-1,0]
            float mRot = 90.0f * (1 - position);
            ViewHelper.setPivotX(page, page.getMeasuredWidth() * position);
            ViewHelper.setPivotY(page, page.getMeasuredHeight() * 0.5f);
            ViewHelper.setRotationY(page, mRot);
//            ViewHelper.setScaleX(page, -position);
//            ViewHelper.setScaleY(page, -position);
            ViewHelper.setAlpha(page, 1);
        } else if (position <= 1) {//(0,1]
            float mRot = 90.0f * position;
            ViewHelper.setPivotX(page, (1 - position) * pageWidth);
            ViewHelper.setPivotY(page, page.getMeasuredHeight() * 0.5f);
            ViewHelper.setRotationY(page, mRot);
//            ViewHelper.setScaleX(page, position);
//            ViewHelper.setScaleY(page, position);
            ViewHelper.setAlpha(page, 1);
        } else {//(1, +Infinity]
            ViewHelper.setAlpha(page, 0);
        }
    }
}
