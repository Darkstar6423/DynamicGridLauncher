package com.george.dynamicgridlauncher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.List;

public class appAdapter extends BaseAdapter {
    Context context;
    List<AppObject> appList;
    public appAdapter(Context context, List<AppObject> appList)
    {
    this.context = context;
    this.appList = appList;


    }


    @Override
    public int getCount() {
        return appList.size();
    }

    @Override
    public Object getItem(int position) {
        return appList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.item_app, parent, false);
        }
        else
        {
            v = convertView;
        }
        ConstraintLayout layout = v.findViewById(R.id.applayout);
        parent.setFocusable(true);
        layout.setFocusable(false);

        ImageView myImage = v.findViewById(R.id.appImage);
        TextView myLabel = v.findViewById(R.id.appLabel);
        myImage.setImageDrawable(appList.get(position).getImage());
        myLabel.setText(appList.get(position).getName());


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ((MainActivity) context).fileManager.setCachedValue("0", appList.get(position).getPackageName(), context);
                    ((MainActivity) context).ItemPress(appList.get(position));

            }
        });

            parent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(keyCode == KeyEvent.KEYCODE_BUTTON_A)
                {
                    GridView gv = (GridView)parent;
                    Log.d("appAdapter", appList.get(gv.getSelectedItemPosition()).getPackageName());
                    ((MainActivity) context).fileManager.setCachedValue("0", appList.get(gv.getSelectedItemPosition()).getPackageName(), context);
                    ((MainActivity) context).ItemPress(appList.get(gv.getSelectedItemPosition()));

                }


                return false;
            }
        });



        return v;
    }
}
