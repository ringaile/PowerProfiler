package com.example.mse.powermanager.powermanager.measurements;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by Ringaile on 10/11/2014.
 */
public class CpuFrequencyMeasurement implements Measurement{

    public String getName() {

        return "cpu_frequency";
    }

    public Double getMeasurement() {
        try {
            String freq = this.readProcFile();

            return Double.parseDouble(freq);
        } catch (Exception e) {
            return null;
        }
    }

    private String readProcFile()
            throws Exception {
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
