package com.george.dynamicgridlauncher;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import java.util.ArrayList;
import java.util.List;

public class scrollManager {
    private class Menu {
        public View view;
        public View focus;

    }

    List<Menu> menuList = new ArrayList<Menu>();

    NonFocusingScrollView horizontalScrollView;
    LinearLayout mainHorizontalLayout;

    Context context;

    @SuppressLint("ClickableViewAccessibility")
    public scrollManager(NonFocusingScrollView sc, LinearLayout hl, Context c)
    {
        context = c;

        mainHorizontalLayout = hl;
        mainHorizontalLayout.setFocusable(false);
        mainHorizontalLayout.scrollTo(0, 0);

        horizontalScrollView = sc;
        horizontalScrollView.setFocusable(false);
        horizontalScrollView.setSmoothScrollingEnabled(true);
        horizontalScrollView.scrollTo(0, 0);
        horizontalScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);

        horizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });




    }

    public static void addVerticalScrollto(View div, NestedScrollView sv, View focus, int scrollTo)
    {
        div.setFocusable(View.FOCUSABLE);
        div.setAlpha(1);

        div.setNextFocusLeftId(div.getId());
        div.setNextFocusRightId(div.getId());
        div.setNextFocusUpId(div.getId());
        div.setNextFocusDownId(div.getId());


        div.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {

                Log.d("ScrollHorizontal", "scrolling");
                //sv.fling(1);

                ObjectAnimator animator=ObjectAnimator.ofInt(sv, "scrollY", scrollTo );
                animator.setStartDelay(0);
                animator.setDuration(400);
                animator.start();
                focus.requestFocus();

            }

        });

    }

    public void horizontalAddScrollTo(View d, View st ,View f)
    {
        d.setNextFocusLeftId(R.id.HorizontalDivider);
        d.setNextFocusRightId(R.id.HorizontalDivider);
        d.setNextFocusUpId(R.id.HorizontalDivider);
        d.setNextFocusDownId(R.id.HorizontalDivider);
        d.setFocusable(true);
        d.setAlpha(1);
        horizontalScrollView.dividers.add(d);

        d.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {

                horizontalScrollView.clearAnimation();
                horizontalScrollView.currentMenu = (ConstraintLayout) st;
                horizontalScrollView.forceAllowFocus = f;
                ObjectAnimator animator=ObjectAnimator.ofInt(horizontalScrollView, "scrollX", (int) st.getX());
                horizontalScrollView.animator = animator;
                animator.setStartDelay(0);
                animator.setDuration(800);

                animator.start();
                f.requestFocus();

                Log.d("ScrollHorizontal", "Scroll to "+st.getX());



            }

        });

    }



    public static View addVertDivider(LinearLayout layout, scrollManager scrollmanager)
    {
        View v;
        LayoutInflater inflater = (LayoutInflater) layout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.vertical_divider, layout, false);
        v.setFocusable(false);
        layout.addView(v);
        scrollmanager.horizontalScrollView.dividers.add(v);
        return v;
    }

    public View addHorizontalDivider()
    {
        View v;
        LayoutInflater inflater = (LayoutInflater) mainHorizontalLayout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.horizontal_divider, mainHorizontalLayout, false);
        v.setFocusable(false);
        mainHorizontalLayout.addView(v);
        horizontalScrollView.dividers.add(v);
        return v;
    }




    public View addMenu(@LayoutRes int resource, @IdRes int focusID)
    {
        LayoutInflater inflater = (LayoutInflater) mainHorizontalLayout.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View s = inflater.inflate(resource, mainHorizontalLayout, false);
        s.setFocusable(false);
        Menu m = new Menu();
        m.focus = s.findViewById(focusID);
        m.view = s;
        m.focus.setFocusable(true);
        menuList.add(m);
        if(menuList.size() > 1) {

            View div1 = addHorizontalDivider();
            View div2 = addHorizontalDivider();
            Menu prevMenu = menuList.get(menuList.size()-2);
            horizontalAddScrollTo(div1, s, m.focus);
            horizontalAddScrollTo(div2, prevMenu.view, prevMenu.focus);
        }
        mainHorizontalLayout.addView(s);
        return s;

    }



}
