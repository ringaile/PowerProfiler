package com.example.mse.powermanager.powermanager.measurements;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class BluetoothStatus implements Measurement {

    private ConnectivityManager connectivity;
    private NetworkInfo info;

    /**
     * Constructs a new object and obtains the ConnectivityManager service which
     * can then be queried for the current Bluetooth status.
     **/
    public BluetoothStatus(Context context) {
        connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);
    }

//    public String getName() {
//        return "bluetooth_status";
//    }
//
//    /**
//     * Query the NetworkInfo object and return whether bluetooth is active.
//     *
//     * @return 1 if bluetooth is active, 0 otherwise
//     **/
//    public Double getMeasurement() {
//        if (info != null && info.isAvailable()) {
//            return 1.0;
//        }
//
//        return 0.0;
//    }

    public boolean getBluetoothStatusValue()
    {
        return (info != null && info.isAvailable());
    }

}
