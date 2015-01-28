package com.example.mse.powermanager.powermanager.measurements;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class BluetoothStatus implements Measurement {

    private ConnectivityManager connectivity;
    private NetworkInfo info;

    public BluetoothStatus(Context context) {
        connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);
    }

    public boolean getBluetoothStatusValue()
    {
        return (info != null && info.isAvailable());
    }

}
