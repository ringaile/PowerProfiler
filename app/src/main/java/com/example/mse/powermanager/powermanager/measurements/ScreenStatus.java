package com.example.mse.powermanager.powermanager.measurements;

import android.content.ContentResolver;
import android.content.Context;
import android.os.PowerManager;
import android.provider.Settings;

/**
 * Created by Ringaile on 10/11/2014.
 */
public class ScreenStatus implements Measurement{

    private PowerManager powermanager;
    private ContentResolver contentResolver;

    public ScreenStatus(Context context) {
        powermanager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        contentResolver = context.getContentResolver();
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
        try
        {
            return android.provider.Settings.System.getInt(contentResolver, android.provider.Settings.System.SCREEN_BRIGHTNESS);
        }
        catch (Settings.SettingNotFoundException e) { e.printStackTrace(); }
        return 0;
    }
}
