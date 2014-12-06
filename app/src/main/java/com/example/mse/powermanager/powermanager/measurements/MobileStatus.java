package com.example.mse.powermanager.powermanager.measurements;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Ringaile on 10/11/2014.
 */
public class MobileStatus implements Measurement {

    private ConnectivityManager connectivity;
    private NetworkInfo info;

    public MobileStatus(Context context) {
        connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    }

//    public String getName() {
//        return "mobile_status";
//    }
//
//    public Double getMeasurement() {
//        if (info.isAvailable()) {
//            return 1.0;
//        }
//
//        return 0.0;
//    }

    public boolean getMobileStatusValue()
    {
        return info.isAvailable();
    }
}
