package com.george.dynamicgridlauncher;



import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;



public class MainActivity extends AppCompatActivity {


    public FileManager fileManager;
    public recentGameButton recentGameButton;
    public MusicMenu musicmenu;
    List<AppObject> installedAppList = new ArrayList<>();
    public playtimeTracker playtimetracker;

    PermissionHandler permissions;
    Context context;

    scrollManager scrollViewMain;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        context = getApplicationContext();
        //initialize Cache Manager
        fileManager = new FileManager(context, "cache");

        installedAppList = getInstalledAppList();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        initializeScroll();



        permissions = new PermissionHandler(this, context);

        //play time tracker
        playtimetracker = new playtimeTracker(this);
        playtimetracker.retrieveGamePlaytimeCache(installedAppList);
        playtimetracker.retrieveLastGamePlayed();

        //recentGameButton
        View game = scrollViewMain.addMenu(R.layout.recent_game_menu, R.id.RecentGameButton);
        recentGameButton = new recentGameButton((ConstraintLayout) game, installedAppList, this);
        scrollViewMain.horizontalScrollView.currentMenu = (ConstraintLayout) game;

        recentGameButton.lastPlayedGame = fileManager.getCachedValue("0", getApplicationContext());

        int appIndex = findAppIndexinAppList(recentGameButton.lastPlayedGame);
        recentGameButton.updateRecentGameView(appIndex, installedAppList.get(appIndex));

        permissions.audioPermissionCallback = granted -> {
            if(granted) {
                View song = scrollViewMain.addMenu(R.layout.music_menu, R.id.playButton);
                musicmenu = new MusicMenu((ConstraintLayout) song, getApplicationContext(), this);
            }
        };



        permissions.runPermissionCallbacks();

    }



    @Override
    protected void onResume() {

        String lastGamePlayed = recentGameButton.lastPlayedGame;

        if(lastGamePlayed != null){
            recentGameButton.updateRecentGameView(findAppIndexinAppList(lastGamePlayed), getInstalledApp(lastGamePlayed));
        }
        recentGameButton.nestedscrollview.scrollTo(0, 0);
        scrollViewMain.horizontalScrollView.scrollTo(0, 0);
        findViewById(R.id.RecentGameButton).requestFocus();
        playtimetracker.setGameTime();

        recentGameButton.updateMostPlayedGames();
        installedAppList = getInstalledAppList();

        if(!new HashSet<>(recentGameButton.appList).containsAll(installedAppList)) {

            recentGameButton.appList = installedAppList;
            recentGameButton.repopulateAppList();
        }
        super.onResume();


    }

    @Override
    protected void onStop() {

        fileManager.setCachedValue("0", recentGameButton.lastPlayedGame, context);
        playtimetracker.storeGamePlayTime();
        playtimetracker.storeLastGamePlayed();

        super.onStop();
    }


    static int drawerColumns = 6;
    private void initializeScroll() {

        final LinearLayout mainHorizontalLayout = findViewById(R.id.mainHorizontalLayout);
        final NonFocusingScrollView scrollview = findViewById(R.id.mainHorizontalView);
        scrollViewMain = new scrollManager(scrollview, mainHorizontalLayout, this);


    }

    private List<AppObject> getInstalledAppList() {
        List<AppObject> list = new ArrayList<>();

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> untreatedAppList = getApplicationContext().getPackageManager().queryIntentActivities(intent, 0);

        for(ResolveInfo untreatedApp : untreatedAppList){
            String appName = untreatedApp.activityInfo.loadLabel(getPackageManager()).toString();
            String appPackageName = untreatedApp.activityInfo.packageName;
            Drawable appImage = untreatedApp.activityInfo.loadIcon(getPackageManager());


            AppObject app = new AppObject(appPackageName, appName, appImage);
            app.catagory = untreatedApp.activityInfo.applicationInfo.category;
            if(untreatedApp.filter != null && untreatedApp.filter.hasCategory("android.intent.category.HOME"))
            {
                continue;
            }

            Log.d("gameCatagory", app.getPackageName()+":"+app.catagory);
            if (!list.contains(app) && !untreatedApp.activityInfo.loadLabel(getPackageManager()).toString().equals("Dynamic Grid Launcher"))
                list.add(app);

        }
        return list;
    }

    private int findAppIndexinAppList(String packageName) {
        for (int i = 0; i < installedAppList.size(); i++)
        {
            if(installedAppList.get(i).getPackageName().equals(packageName))
            {
                return i;
            }
        }

        return 0;
    }
    private AppObject getInstalledApp(String packageName) {

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> untreatedAppList = getApplicationContext().getPackageManager().queryIntentActivities(intent, 0);
        AppObject result = null;
        for(ResolveInfo untreatedApp : untreatedAppList){
            String appName = untreatedApp.activityInfo.loadLabel(getPackageManager()).toString();
            String appPackageName = untreatedApp.activityInfo.packageName;
            Drawable appImage = untreatedApp.activityInfo.loadIcon(getPackageManager());

            if(appPackageName.equals(packageName))
            {
                result = new AppObject(appPackageName, appName, appImage);
            }

        }

        if(result == null)
        {
            return result;
        }
        Log.d("getInstalledApp", result.getPackageName());
        return result;
    }

    public void ItemPress(AppObject app) {
        Intent launchAppIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(app.getPackageName());

        if (launchAppIntent != null)
        {
            playtimetracker.setLastGameLaunchTime();
            playtimetracker.setLastPlayedGame(app.getName());
            recentGameButton.lastPlayedGame = app.getPackageName();
            Log.d("lastPlayedGameLog", app.getPackageName());


            getApplicationContext().startActivity(launchAppIntent);
        }


    }








}