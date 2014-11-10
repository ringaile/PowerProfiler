package com.example.mse.powermanager.powermanager;


import android.app.Application;
import android.content.Context;

import com.example.mse.powermanager.powermanager.measurements.MeasurementCollection;

import java.util.ArrayList;
import java.util.List;

public class PowerManagerApp extends Application{
    private static Context context;
    private static String fileid;
    private static List<MeasurementCollection> measureContainer;

    public void onCreate() {
        super.onCreate();
        PowerManagerApp.context = getApplicationContext();
        measureContainer = new ArrayList<MeasurementCollection>();
    }

    public static Context getContext() {

        return context;
    }

    public static String getFileId() {

        return fileid;
    }

    public static void setFileId(String fileId){

        fileid = fileId;
    }

    public static void addMeasurementCollection(MeasurementCollection collection){
        measureContainer.add(collection);
    }

    public static void writeToLog(){
        LogWriter lw = new LogWriter();
        for(MeasurementCollection m : measureContainer){
            lw.write(m.getMeasurements());
        }
        measureContainer.clear();
    }
}
