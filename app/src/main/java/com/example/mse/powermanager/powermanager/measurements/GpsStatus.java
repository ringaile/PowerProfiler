package com.example.mse.powermanager.powermanager.measurements;


import android.content.Context;
import android.location.LocationManager;

public class GpsStatus implements Measurement{

    private LocationManager location;

    public GpsStatus(Context context) {
        location = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }

    public boolean getGpsStatusValue()
    {
        return location.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
