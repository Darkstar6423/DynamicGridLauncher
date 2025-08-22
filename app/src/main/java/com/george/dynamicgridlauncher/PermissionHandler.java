package com.george.dynamicgridlauncher;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.function.Consumer;

public class PermissionHandler {
    public MainActivity mainActivity;
    public boolean musicAndAudioPerm = false;
    Consumer<Boolean> audioPermissionCallback = bool -> {};

    public boolean permissionAsked = false;




    public PermissionHandler(MainActivity activity, Context c)
    {
        mainActivity = activity;

        musicAndAudioPerm = ContextCompat.checkSelfPermission(
                c, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED;

        Log.d("Permission", "Audio:"+musicAndAudioPerm);

        requestMissingPermissions();
    }


    public void requestMissingPermissions()
    {
        ActivityResultLauncher<String> audioPermissionLauncher = mainActivity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                musicAndAudioPerm = true;
            } else {
                musicAndAudioPerm = false;
                Log.d("permission", "denied");
            }
        });

        if(!musicAndAudioPerm){
            audioPermissionLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO);
        }



    }

    public void runPermissionCallbacks()
    {
        audioPermissionCallback.accept(musicAndAudioPerm);
    }

}
