package com.george.dynamicgridlauncher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AudioPlayerBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        assert action != null;
        if(action.equalsIgnoreCase("com.george.dynamicgridlauncher.ACTION_PLAY")){

        }
    }
}
