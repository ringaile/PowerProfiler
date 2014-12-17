package com.example.mse.powermanager.powermanager.measurements;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class WifiStatus implements Measurement {

    private ConnectivityManager connectivity;
    private NetworkInfo info;
    private Context context;

    public WifiStatus(Context context) {
//        connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        this.context = context;
    }

//    public String getName() {
//        return "wifi_status";
//    }
//
//    public Double getMeasurement() {
//        if (info.isAvailable()) {
//            return 1.0;
//        }
//
//        return 0.0;
//    }

    public boolean getWifiStatusValue()
    {
        //This doesnt work properly
        //return info.isAvailable();

        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        boolean wifiEnabled = wifiManager.isWifiEnabled();
        return wifiEnabled;
    }

}
