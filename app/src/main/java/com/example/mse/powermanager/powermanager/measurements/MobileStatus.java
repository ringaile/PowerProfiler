package com.example.mse.powermanager.powermanager.measurements;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MobileStatus implements Measurement {

    private ConnectivityManager connectivity;
    private NetworkInfo info;

    public MobileStatus(Context context) {
        connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    }

    public boolean getMobileStatusValue()
    {
        return info.isAvailable();
    }
}
