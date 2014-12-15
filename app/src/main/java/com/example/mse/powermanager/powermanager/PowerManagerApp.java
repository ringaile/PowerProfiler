package com.example.mse.powermanager.powermanager;


import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.mse.powermanager.powermanager.measurements.MeasurementCollection;
import com.example.mse.powermanager.powermanager.structs.MeasurementStruct;

import java.util.ArrayList;
import java.util.List;

public class PowerManagerApp extends Application{
    private static Context context;
    private static String fileid;
    //private static List<MeasurementCollection> measureContainer;
    public static List<MeasurementStruct> measurementIterations;
    public static int mode;

    public void onCreate() {
        super.onCreate();
        PowerManagerApp.context = getApplicationContext();
        //measureContainer = new ArrayList<MeasurementCollection>();
        measurementIterations = new ArrayList<MeasurementStruct>();
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

    public static void addMeasurementIteration(MeasurementStruct measurementStruct)
    {
        Log.d("addMeasurementIteration", String.valueOf(measurementIterations.size()));
        measurementIterations.add(measurementStruct);
        if (measurementIterations.size() >= 10)
        {
            for (MeasurementStruct iteratedStruct: measurementIterations)
            {
                Log.d(">>>>> timestamp", String.valueOf(iteratedStruct.timestamp));
                Log.d("battery level", String.valueOf(iteratedStruct.batteryLevel));

                Log.d("wifi", String.valueOf(iteratedStruct.wifiStatus));
                Log.d("bluetooth", String.valueOf(iteratedStruct.bluetoothStatus));
                Log.d("mobile", String.valueOf(iteratedStruct.mobileStatus));
                Log.d("gps", String.valueOf(iteratedStruct.gpsStatus));
                Log.d("screen on", String.valueOf(iteratedStruct.screenStatus));

                Log.d("screen brightness", String.valueOf(iteratedStruct.screenBrightness));
                Log.d("memory free", String.valueOf(iteratedStruct.memoryFree));
                Log.d("cpu usage", String.valueOf(iteratedStruct.cpuUsage));
                Log.d("cpu frequency", String.valueOf(iteratedStruct.cpuFrequency));

                Log.d("sent network", String.valueOf(iteratedStruct.networkSent));
                Log.d("received network", String.valueOf(iteratedStruct.networkReceived));

            }
            //TODO: process gathered data
            //Make some changes to the system
            measurementIterations.clear();
        }
    }


//    public static void addMeasurementCollection(MeasurementCollection collection){
//        measureContainer.add(collection);
//    }
//
//    public static void writeToLog(){
//        LogWriter lw = new LogWriter();
//        for(MeasurementCollection m : measureContainer){
//            lw.write(m.getMeasurements());
//        }
//        measureContainer.clear();
//    }
}
