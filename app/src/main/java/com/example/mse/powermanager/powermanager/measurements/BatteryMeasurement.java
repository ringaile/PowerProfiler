package com.example.mse.powermanager.powermanager.measurements;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

/**
 * Created by Ringaile on 10/11/2014.
 */
public class BatteryMeasurement implements Measurement{

    private Context context;

    public BatteryMeasurement(Context context) {

        this.context = context;
    }

//    public String getName() {
//
//        return "battery_level";
//    }
//
//    public Double getMeasurement() {
//        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
//        Intent status = context.registerReceiver(null, filter);
//
//        int level = status.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//        int scale = status.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//
//        return (level / (double)scale) * 100;
//    }

    public double getBatteryLevelValue()
    {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent status = context.registerReceiver(null, filter);

        int level = status.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = status.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return (level / (double)scale) * 100;
    }
}
