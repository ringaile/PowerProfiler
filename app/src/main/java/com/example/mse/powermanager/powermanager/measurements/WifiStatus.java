package com.example.mse.powermanager.powermanager.measurements;


import android.content.Context;
import android.net.wifi.WifiManager;

public class WifiStatus implements Measurement {
    private Context context;

    public WifiStatus(Context context) {
        this.context = context;
    }


    public boolean getWifiStatusValue()
    {

        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        boolean wifiEnabled = wifiManager.isWifiEnabled();
        return wifiEnabled;
    }

}
