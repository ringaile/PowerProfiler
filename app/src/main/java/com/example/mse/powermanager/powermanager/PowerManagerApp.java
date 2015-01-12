package com.example.mse.powermanager.powermanager;


import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.example.mse.powermanager.powermanager.measurements.MeasurementCollection;
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

    public static String getFileId() {

        return fileid;
    }

    public static void setFileId(String fileId){

        fileid = fileId;
    }

//    public static void addMeasurementIteration(MeasurementStruct measurementStruct)
//    {
//        Log.d("addMeasurementIteration", String.valueOf(measurementIterations.size()));
//        measurementIterations.add(measurementStruct);
//        float meanProcessorLoad = 0;
//        float meanMemoryFree = 0;
//        if (measurementIterations.size() >= 10)
//        {
//            for (MeasurementStruct iteratedStruct: measurementIterations)
//            {
//                Log.d(">>>>> timestamp", String.valueOf(iteratedStruct.timestamp));
//                Log.d("battery level", String.valueOf(iteratedStruct.batteryLevel));
//
//                Log.d("wifi", String.valueOf(iteratedStruct.wifiStatus));
//                Log.d("bluetooth", String.valueOf(iteratedStruct.bluetoothStatus));
//                Log.d("mobile", String.valueOf(iteratedStruct.mobileStatus));
//                Log.d("gps", String.valueOf(iteratedStruct.gpsStatus));
//                Log.d("screen on", String.valueOf(iteratedStruct.screenStatus));
//
//                Log.d("screen brightness", String.valueOf(iteratedStruct.screenBrightness));
//                Log.d("memory free", String.valueOf(iteratedStruct.memoryFree));
//                Log.d("cpu usage", String.valueOf(iteratedStruct.cpuUsage));
//                Log.d("cpu frequency", String.valueOf(iteratedStruct.cpuFrequency));
//
//                Log.d("sent network", String.valueOf(iteratedStruct.networkSent));
//                Log.d("received network", String.valueOf(iteratedStruct.networkReceived));
//
//                meanProcessorLoad += iteratedStruct.cpuUsage;
//                meanMemoryFree += iteratedStruct.memoryFree;
//            }
//            meanProcessorLoad /= 10;
//            meanMemoryFree /= 10;
//            //TODO: process gathered data
//            //Make some changes to the system
//            Log.d("Mean processor load", String.valueOf(meanProcessorLoad));
//            Log.d("Mean memory free", String.valueOf(meanMemoryFree));
//
//            //WiFi
//            WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
//            wifiManager.setWifiEnabled(true);
//            wifiManager.setWifiEnabled(false);
//
//            if (mode == 0)
//            {
//                if ( (meanProcessorLoad > 50) || (meanMemoryFree < 50) )
//                {
//                    //ACTION
//                    Log.d("ACTION", "saving action");
//                }
//            }
//            else if (mode == 1)
//            {
//                if ( (meanProcessorLoad > 75) || (meanMemoryFree < 75) )
//                {
//                    //ACTION
//                    Log.d("ACTION", "normal action");
//                }
//            }
//
//            measurementIterations.clear();
//        }
//    }

}
