package com.example.mse.powermanager.powermanager.measurements;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryMeasurement implements Measurement{

    private Context context;

    public BatteryMeasurement(Context context) {

        this.context = context;
    }


    public double getBatteryLevelValue()
    {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent status = context.registerReceiver(null, filter);

        int level = status.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = status.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return (level / (double)scale) * 100;
    }
}
