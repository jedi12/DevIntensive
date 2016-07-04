package com.softdesign.devintensive.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.softdesign.devintensive.R;

public class HeaderBehavior extends CoordinatorLayout.Behavior<ViewGroup> {

    private float mActionBarSize;
    private float mTopPaddingMultiply = -1f;
    private float mBottomPaddingMultiply = -1f;

    public HeaderBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActionBarSize = getActionBarSize(context);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ViewGroup child, View dependency) {
        return dependency instanceof NestedScrollView;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ViewGroup child, View dependency) {

        if (mTopPaddingMultiply < 0) {
            mTopPaddingMultiply = child.getPaddingTop() / dependency.getY();
        }

        if (mBottomPaddingMultiply < 0) {
            mBottomPaddingMultiply = child.getPaddingBottom() / dependency.getY();
        }

        float appBarCurrentHeight = dependency.getY() - mActionBarSize;

        int newTopPadding = (int) (appBarCurrentHeight * mTopPaddingMultiply);
        int newBottomPadding = (int) (appBarCurrentHeight * mBottomPaddingMultiply);

        child.setY(dependency.getY());
        child.setPadding(child.getPaddingLeft(), newTopPadding, child.getPaddingRight(), newBottomPadding);
        dependency.setPadding(dependency.getPaddingLeft(), child.getHeight(), dependency.getPaddingRight(), dependency.getPaddingBottom());

        return false;
    }

    private float getActionBarSize(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[]{R.attr.actionBarSize});
        float actionBarSize = styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return actionBarSize;
    }
}

