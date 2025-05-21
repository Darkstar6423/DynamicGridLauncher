package com.george.dynamicgridlauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.List;

public class recentGameButton {
        private Context context;
        private int lastLaunchedAppIndex = -1;

        NestedScrollView nestedscrollview;

        LinearLayout mainLinearLayout;

        List<AppObject> appList;
        private ConstraintLayout parent;
        public Button button;
        private AppObject recentApp;
        private ImageView appIcon;
        private TextView gameTitle;
        private playtimeTracker playtimetracker;
        public String lastPlayedGame = "none";

        public List<LinearLayout> appDrawerRows = new ArrayList<LinearLayout>();

        public recentGameButton(ConstraintLayout v, List<AppObject> al, Context c)
        {
            parent = v;
            nestedscrollview = (NestedScrollView) v.getChildAt(0);
            mainLinearLayout = (LinearLayout) nestedscrollview.getChildAt(0);
            mainLinearLayout.setFocusable(false);
            nestedscrollview.setFocusable(false);
            context = c;
            button = v.findViewById(R.id.RecentGameButton);
            appIcon = v.findViewById(R.id.RecentGameImageIcon);
            gameTitle = v.findViewById(R.id.RecentGameTextView);
            appList = al;
            playtimetracker = ((MainActivity) context).playtimetracker;

            button.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    if(lastLaunchedAppIndex == -1)
                    {
                        return;
                    }
                    ((MainActivity) context).ItemPress(appList.get(lastLaunchedAppIndex));
                }
            });

            View div1 = scrollManager.addVertDivider(mainLinearLayout, ((MainActivity) context).scrollViewMain);
            View div2 = scrollManager.addVertDivider(mainLinearLayout, ((MainActivity) context).scrollViewMain);
            ((MainActivity) context).scrollViewMain.horizontalScrollView.dividers.add(div1);
            ((MainActivity) context).scrollViewMain.horizontalScrollView.dividers.add(div2);
            populateApplications();
            scrollManager.addVerticalScrollto(div1, nestedscrollview, appDrawerRows.get(0).getChildAt(0), 1150);
            scrollManager.addVerticalScrollto(div2, nestedscrollview, button, 0);
            button.setNextFocusLeftId(R.id.RecentGameButton);
            button.setOnFocusChangeListener(RecentButtonFocusListener);
        }

        View.OnFocusChangeListener RecentButtonFocusListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    nestedscrollview.setScrollY(0);
                    //disableAppDrawerFocus();
                }
                else
                {

                    //enableAppDrawerFocus();
                    nestedscrollview.scrollTo(0, 0);

                }

            }
        };

        private void disableAppDrawerFocus()
        {
            int length = appDrawerRows.size();
            for(int i=0;i<length;i++)
            {
                LinearLayout currentRow = appDrawerRows.get(i);
                for(int j=0;j<currentRow.getChildCount();j++)
                {
                    View child = currentRow.getChildAt(j);
                    if(child.getAlpha() > 0)
                    {
                        child.findViewById(R.id.funcButton).setFocusable(false);
                    }
                }
            }
        }

    private void enableAppDrawerFocus()
    {
        int length = appDrawerRows.size();
        for(int i=0;i<length;i++)
        {
            LinearLayout currentRow = appDrawerRows.get(i);
            for(int j=0;j<currentRow.getChildCount();j++)
            {
                View child = currentRow.getChildAt(j);
                if(child.getAlpha() > 0)
                {
                    child.findViewById(R.id.funcButton).setFocusable(true);
                }
            }
        }
    }


        public void updateRecentGameView(int appindex, AppObject app)
        {
            if(parent == null | app == null)
            {
                return;
            }

            lastLaunchedAppIndex = appindex;

            appIcon.setImageDrawable(app.getImage());
            gameTitle.setText(app.getName());
        }


    public void updateMostPlayedGames()
    {
        LinearLayout top = parent.findViewById(R.id.topFavAppRow);
        LinearLayout bottom = parent.findViewById(R.id.bottomFavAppRow);

        List<AppObject> sortedList = playtimetracker.getAppsByPLayTime(appList);

        int tSlot = 0;
        for(int i = 0;i<4;i++)
        {
            View view = top.getChildAt(i);

            if(sortedList == null || (tSlot > sortedList.size()-1))
            {
                view.setAlpha(0);
                view.setFocusable(false);
                view.findViewById(R.id.funcButton).setFocusable(false);

                continue;
            }
            view.setAlpha(1);
            view.setFocusable(false);
            view.findViewById(R.id.funcButton).setFocusable(true);

            TextView topTV = view.findViewById(R.id.appLabel);
            ImageView imageView = view.findViewById(R.id.appImage);
            Button button = view.findViewById(R.id.funcButton);
            int appIndex = tSlot;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) context).fileManager.setCachedValue("0", sortedList.get(appIndex).getPackageName(), context);
                    ((MainActivity) context).ItemPress(sortedList.get(appIndex));

                }
            });

            topTV.setText(sortedList.get(tSlot).getName());
            imageView.setImageDrawable(sortedList.get(tSlot).getImage());



            tSlot = tSlot+2;
        }

        int bSlot = 1;
        for(int i = 0;i<4;i++)
        {
            View view = bottom.getChildAt(i);
            if(sortedList == null || (bSlot > sortedList.size()-1))
            {
                view.setAlpha(0);
                view.setFocusable(false);
                view.findViewById(R.id.funcButton).setFocusable(false);

                continue;
            }
            view.setAlpha(1);
            view.setFocusable(false);
            view.findViewById(R.id.funcButton).setFocusable(true);

            TextView tv = view.findViewById(R.id.appLabel);
            ImageView imageView = view.findViewById(R.id.appImage);
            Button button = view.findViewById(R.id.funcButton);
            final int appIndex = bSlot;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) context).fileManager.setCachedValue("0", sortedList.get(appIndex).getPackageName(), context);
                    ((MainActivity) context).ItemPress(sortedList.get(appIndex));
                }
            });

            tv.setText(sortedList.get(bSlot).getName());
            imageView.setImageDrawable(sortedList.get(bSlot).getImage());
            bSlot = bSlot+2;



        }



    }

    public void populateApplications()
    {
        int len = appList.size();
        int i = 0;

        while(i<len) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout temp = (LinearLayout) inflater.inflate(R.layout.app_row, mainLinearLayout, false);
            mainLinearLayout.addView(temp);
            appDrawerRows.add(temp);
            for (int j = 0; j < temp.getChildCount(); j++) {
                View v = temp.getChildAt(j);
                if(i>=len) {
                    v.setAlpha(0);
                    v.setFocusable(false);
                    v.findViewById(R.id.funcButton).setFocusable(false);
                    continue;
                }

                TextView topTV = v.findViewById(R.id.appLabel);
                ImageView imageView = v.findViewById(R.id.appImage);
                Button button = v.findViewById(R.id.funcButton);
                button.setFocusable(true);
                topTV.setText(appList.get(i).getName());
                imageView.setImageDrawable(appList.get(i).getImage());
                int appIndex = i;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity) context).fileManager.setCachedValue("0", appList.get(appIndex).getPackageName(), context);
                        ((MainActivity) context).ItemPress(appList.get(appIndex));

                    }
                });


                if(len-i <=6)
                {
                    button.setNextFocusDownId(R.id.funcButton);
                }


                i++;
            }
        }




    }



}
