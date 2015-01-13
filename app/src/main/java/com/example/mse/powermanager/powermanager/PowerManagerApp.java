package com.example.mse.powermanager.powermanager;


import android.app.Application;
import android.content.Context;
import com.example.mse.powermanager.powermanager.structs.MeasurementStruct;

import java.util.ArrayList;
import java.util.List;

public class PowerManagerApp extends Application{
    private static Context context;
    private static String fileid;
    public static List<MeasurementStruct> measurementIterations;
    public static List<String> warningsList;
    public static PowerManagerActivity mainActivity;
    public static int mode;

    public void onCreate() {
        super.onCreate();
        PowerManagerApp.context = getApplicationContext();
        measurementIterations = new ArrayList<MeasurementStruct>();
        warningsList = new ArrayList<String>();
    }

    public static Context getContext() {

        return context;
    }

    public static void setFileId(String fileId){

        fileid = fileId;
    }


}
