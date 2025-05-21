package com.george.dynamicgridlauncher;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class CollapsingLinearLayout extends LinearLayout {


    public int mOriginalHeight = 0;

    private CollapsingLinearLayout ll = this;
    private ViewGroup.LayoutParams parameters;
    private boolean isShrink = false;
    private boolean initialSizeObtained = false;
    Animation _hideAnimation = new Animation() {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
            params.topMargin = -(int) (ll.mOriginalHeight * interpolatedTime);
            ll.setLayoutParams(params);
        }

    };
    Animation _showAnimation = new Animation() {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
            params.topMargin = (int) (ll.mOriginalHeight * (interpolatedTime - 1));
            ll.setLayoutParams(params);
        }
    };


    @Override
    public void addView(View child) {
        super.addView(child);


    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if(gainFocus)
        {
            Button b = ll.getChildAt(0).findViewById(R.id.funcButton);
            if(b != null)
            {
                b.requestFocus();
            }

        }
    }

    public void setLayoutParams(ViewGroup.LayoutParams params) {
        parameters = params;
        super.setLayoutParams(params);
    }
    private void addGlobalListener()
    {
        ll = this;
        _hideAnimation.setDuration(1000);
        _showAnimation.setDuration(1000);
        ll.setFocusable(false);
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (initialSizeObtained)
                    return;
                initialSizeObtained = true;
                mOriginalHeight = ll.getMeasuredHeight();
            }
        });



    }

    public CollapsingLinearLayout(Context context) {

        super(context);
        addGlobalListener();
    }

    public CollapsingLinearLayout(Context context, AttributeSet attribute)
    {
        super(context, attribute);
        addGlobalListener();
    }
    public CollapsingLinearLayout(Context context,
                                  AttributeSet attrs,
                                  int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        addGlobalListener();
    }
    public void toggleCollapse()
    {
        isShrink = !isShrink;
        Log.d("animation", "collapsing");
        this.clearAnimation();
        this.startAnimation(isShrink? _hideAnimation : _showAnimation);

    }


    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        Animation anim = _showAnimation;
        if(isShrink)
        {
            anim = _hideAnimation;
        }


    }

    public void collapse()
    {
        if(!isShrink)
        {
            isShrink = true;
            this.startAnimation(_hideAnimation);
        }

    }

    public boolean isCollapsed()
    {
        return isShrink;
    }


}
