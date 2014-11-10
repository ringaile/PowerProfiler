package com.example.mse.powermanager.powermanager.measurements;


import android.content.Context;
import android.location.LocationManager;

public class GpsStatus implements Measurement{

    private LocationManager location;

    /**
     * Contructs a new object and obtains the LocationManager service.
     **/
    public GpsStatus(Context context) {
        location = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }

    public String getName() {
        return "gps_status";
    }

    /**
     * Return whether GPS is active of not.
     *
     * @return 1 if GPS is active, 0 otherwise
     **/
    public Double getMeasurement() {
        if (location.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return 1.0;
        }

        return 0.0;
    }
}
