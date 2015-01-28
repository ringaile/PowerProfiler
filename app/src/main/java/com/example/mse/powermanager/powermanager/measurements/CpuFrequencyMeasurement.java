package com.example.mse.powermanager.powermanager.measurements;

import java.io.File;
import java.io.RandomAccessFile;

public class CpuFrequencyMeasurement implements Measurement{


    public Double getMeasurement() {
        try {
            String freq = this.readProcFile();

            return Double.parseDouble(freq);
        } catch (Exception e) {
            return null;
        }
    }

    private String readProcFile()throws Exception {
        File file = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
        RandomAccessFile freq = new RandomAccessFile(file, "r");

        String frequency = freq.readLine();
        freq.close();

        return frequency;
    }

    public double getCpuFrequency()
    {
        return getMeasurement().doubleValue();
    }
}
