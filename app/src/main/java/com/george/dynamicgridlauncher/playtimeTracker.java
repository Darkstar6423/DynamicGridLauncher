package com.george.dynamicgridlauncher;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class playtimeTracker {
    FileManager fileManager;
    Context context;
    String lastPlayedGame = "none";
    HashMap<String, Long> appPlayTimeMap = new HashMap<>();

    Long lastGameLaunchTime = 0L;

    public playtimeTracker(Context c)
    {
        fileManager = new FileManager(c, "playtime");
        context = c;

    }

    public String getLastPlayedGame()
    {
        return  lastPlayedGame;
    }

    public void storeLastGamePlayed()
    {
        if(lastPlayedGame == null)
        {
            return;
        }

        fileManager.setCachedValue("lastGamePlayed", lastPlayedGame, context);
    }
    public void retrieveLastGamePlayed()
    {
        String name = fileManager.getCachedValue("lastGamePlayed", context);

        if(name == null || name.equals("none"))
        {
            return;
        }

        lastPlayedGame = name;
    }

    public void setLastPlayedGame(String name)
    {
        if(name == null)
        {
            name = "none";
        }

        this.lastPlayedGame = name;
    }



    public void clearLastGamePlayed()
    {
        lastPlayedGame = "none";
    }


    public void setLastGameLaunchTime()
    {
        Calendar calendar = Calendar.getInstance();
        lastGameLaunchTime = calendar.getTimeInMillis();
    }

    public void setGameTime() {

        if (lastGameLaunchTime == null) {
            Log.d("storeLastGamePlayedTime", "threw null at lastGameStartTime check");
            return;
        }


        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        long gameTime = now - lastGameLaunchTime;


        if (lastPlayedGame == null) {
            Log.d("storeLastGamePlayedTime", "threw null at lastGamePlayed check");
            return;
        }
        if (lastPlayedGame.equals("none")) {
            return;
        }


        Long oldTime = appPlayTimeMap.get(lastPlayedGame);
        Log.d("oldTime", String.valueOf(oldTime));
        if(oldTime == null)
        {
            oldTime = 0L;
        }
        gameTime = oldTime + gameTime;

        appPlayTimeMap.put(lastPlayedGame, gameTime);

    }
    // in the event that the app is destroyed, run this
    public void storeGamePlayTime()
    {
        for(String key : appPlayTimeMap.keySet()) {
            String pt = String.valueOf(appPlayTimeMap.get(key));
            fileManager.setCachedValue(key, pt, context);
        }


    }

    public void retrieveGamePlaytimeCache(List<AppObject> appList)
    {
        for(AppObject app : appList)
        {
            String temp = fileManager.getCachedValue(app.getName(), context);
            if(temp == null)
            {
                continue;
            }
            Long playtime = Long.valueOf(temp);

            appPlayTimeMap.put(app.getName(), playtime);

        }


    }

//for games that really should be "Games" but aren't
private boolean isOnWhiteList(String friendlyName)
{
    boolean result = false;

    switch(friendlyName)
    {
        case "com.retroarch.aarch64":
        case "xyz.aethersx2.android":
        case "org.dolphinemu.dolphinemu":
        result = true;
        break;
    }


    return result;
}


    public List<AppObject> getAppsByPLayTime(List<AppObject> appList)
    {
        List<AppObject> newList = new ArrayList<>();
        for(int i = 0; i<appList.size(); i++)
        {

            String appName = appList.get(i).getName();
            Long pt = appPlayTimeMap.get(appName);

            if(appList.get(i).catagory != ApplicationInfo.CATEGORY_GAME && isOnWhiteList(appList.get(i).getPackageName()) == false)
            {
                continue;
            }

            if(pt == null){
                continue;}

            appList.get(i).playTime = pt;
            newList.add(appList.get(i));
            Log.d("getAppsByPlaytime", appList.get(i).getName());
        }


        if(newList.isEmpty())
        {
            return null;
        }


        for(int i =0 ;i<newList.size();i++) {

            for (int j = newList.size() - 1; j > i; j--)
            {
                if(newList.get(i).playTime < newList.get(j).playTime)
                {
                    AppObject temp = newList.get(i);
                    newList.set(i, newList.get(j));
                    newList.set(j, temp);
                }
            }

        }


        return newList;
    }






}
