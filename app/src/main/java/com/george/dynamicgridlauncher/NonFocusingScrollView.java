package com.george.dynamicgridlauncher;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.Scroller;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import java.util.ArrayList;
import java.util.List;

public class NonFocusingScrollView extends HorizontalScrollView {


    List<View> dividers = new ArrayList<View>();
    Animator animator = null;

    ConstraintLayout currentMenu = null;


    View forceAllowFocus = null;

    public NonFocusingScrollView(Context context) {


        super(context);

    }

    public NonFocusingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonFocusingScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
        //return super.computeScrollDeltaToGetChildRectOnScreen(rect);
    }

    @Override
    public void requestChildFocus(View child, View focused) {

        super.requestChildFocus(child, focused);
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {

        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }
}
