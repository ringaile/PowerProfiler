package com.example.mse.powermanager.powermanager.measurements;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.RandomAccessFile;

public class MemoryMeasurement implements Measurement
{
    private Context context;

//    public String getName() {
//        return "memory_free";
//    }
    public MemoryMeasurement(Context context)
    {
        this.context = context;
    }


    public Double getMeasurement() {
//        try {
//            String[] measurements = this.readProcFile();
//
//            Double total = Double.parseDouble(measurements[0]);
//            Double free = Double.parseDouble(measurements[1]);
//            Log.d("Mem total",String.valueOf(total));
//            Log.d("Mem free",String.valueOf(free));
//
//            return (free / total) * 100;
//        } catch (Exception e) {
//            return null;
//        }
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem;
//        Log.d("FREE MEM>",String.valueOf(availableMegs));
//        Log.d("TOTAL MEM",String.valueOf(mi.totalMem));
        double total = mi.totalMem;
        double free = mi.availMem;
        return Double.valueOf(free/total);
    }

    private String[] readProcFile()
            throws Exception {
        File file = new File("/proc/meminfo");
        RandomAccessFile mem = new RandomAccessFile(file, "r");
        String[] result = new String[2];

//        Log.d("1",mem.readLine());
//        Log.d("2",mem.readLine());

        result[0] = mem.readLine().replaceAll("\\D+", "");
        result[1] = mem.readLine().replaceAll("\\D+", "");

        mem.close();
        return result;
    }

    public double getMemoryFreeValue()
    {
        return getMeasurement().doubleValue();
    }
}
