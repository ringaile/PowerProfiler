package com.example.mse.powermanager.powermanager.measurements;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by Ringaile on 10/11/2014.
 */
public class ScreenStatus implements Measurement{

    private PowerManager powermanager;

    public ScreenStatus(Context context) {
        powermanager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
    }

    public String getName() {

        return "screen_status";
    }

    public Double getMeasurement() {
        if (powermanager.isScreenOn()) {
            return 1.0;
        }

        return 0.0;
    }

    public boolean getScreenStatusValue()
    {
        return powermanager.isScreenOn();
    }

    public double getScreenBrightnessValue()
    {
        return curBrightnessValue = android.provider.Settings.System.getInt(getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
    }
}
