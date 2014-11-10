package com.example.mse.powermanager.powermanager.measurements;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class WifiStatus implements Measurement {

    private ConnectivityManager connectivity;
    private NetworkInfo info;

    public WifiStatus(Context context) {
        connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    public String getName() {
        return "wifi_status";
    }

    public Double getMeasurement() {
        if (info.isAvailable()) {
            return 1.0;
        }

        return 0.0;
    }
}
