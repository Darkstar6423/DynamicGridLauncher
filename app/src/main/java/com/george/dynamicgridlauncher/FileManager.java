package com.george.dynamicgridlauncher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class FileManager {
    private File file;
    private String fileName;
    private boolean isCache = true;



    FileManager(Context context, String fileName) {

            this.fileName = fileName;
            file = new File(context.getCacheDir(), fileName+".txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void setCachedValue(String id, String value, Context context) {
        ArrayList<ArrayList<String>> data = new ArrayList<>(5);



        FileInputStream fis = null;
        try {

            Log.d("setCache "+fileName+".txt", file.getAbsolutePath());

            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
            }


        if (fis == null) {
            return;
        }
        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = null;
        boolean dupeCheck = false;
        try {
            line = reader.readLine();


        while(line != null)
        {
            String[] split = line.split(":");
            if(split.length < 2)
            {
                return;
            }

            String identifier = split[0];
            String Val = split[1];

            ArrayList<String> temp = new ArrayList<String>();
            if(id.equals(identifier))
            {
                Val=value;
                dupeCheck = true;
            }

            temp.add(identifier);
            temp.add(":");
            temp.add(Val);
            temp.add("\n");
            data.add(temp);
            Log.d("setCache "+fileName+".txt", line);
            line = reader.readLine();


        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(dupeCheck == false) {

            ArrayList<String> temp = new ArrayList<String>();
            temp.add(id);
            temp.add(":");
            temp.add(value);
            temp.add("\n");
            data.add(temp);
            Log.d("setCache "+fileName+".txt", "New value:" + value);
        }
        FileOutputStream fos = null;
        try {
            File file = new File(context.getCacheDir(), fileName+".txt");
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        int i = 0,j = 0;
        while(data.size() > i) {

            try {
                if(data.get(i).get(j) == null)
                {
                    j++;
                    continue;
                }

                fos.write(data.get(i).get(j).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            j++;
            if(j> 3)
            {
                j=0;
                i++;
            }

        }

        try {
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCachedValue(String identifier, Context context) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        Log.d("getCache", stackTraceElements[3].getMethodName());
        FileInputStream fis = null;


        Log.d("getCache", file.getAbsolutePath());
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            Log.d("getCache", fileName+".txt not found");
            return null;
        }
        if (fis == null) {
            return null;
        }
        InputStreamReader inputStreamReader = new InputStreamReader(fis, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String value = null;
        try {
            String line = reader.readLine();

            while (line != null && value == null) {

                Log.d("getCache "+fileName+".txt", line);
                String[] split = line.split(":");

                String id = split[0];
                if (id.equals(identifier)) {
                    if(split.length > 1) {
                        value = split[1];
                    }
                }

                line = reader.readLine();
            }
            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (value == null)
        {
            Log.d("getCache", "cannot find value for "+identifier);

        }

        return value;



    }


}
