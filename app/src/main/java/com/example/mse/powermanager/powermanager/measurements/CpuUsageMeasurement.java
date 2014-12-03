package com.example.mse.powermanager.powermanager.measurements;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Ringaile on 10/11/2014.
 */
public class CpuUsageMeasurement implements Measurement {

    public String getName() {

        return "cpu_usage";
    }

    public Double getMeasurement() {
        try {
            Double[] sample1 = this.getSample();
            Thread.sleep(100);
            Double[] sample2 = this.getSample();

            Log.d("CPU", "sample1: " + sample1[0] + " - " + sample1[1]);
            Log.d("CPU", "sample2: " + sample2[0] + " - " + sample2[1]);

            return (sample2[0] - sample1[0]) / (sample2[1] - sample1[1]) * 100;
        } catch (InterruptedException ie) {
            return 0.0;
        }
    }

    private Double[] getSample() {
        try {
            String[] values = this.readProcFile();
            Double[] cpuTimes = new Double[2];

            cpuTimes[0] = 0.0;
            cpuTimes[1] = 0.0;

            for (int i=2; i<5; i++) {
                cpuTimes[0] += Double.parseDouble(values[i]);
            }

            for (int i=2; i<values.length; i++) {
                cpuTimes[1] += Double.parseDouble(values[i]);
            }

            return cpuTimes;
        } catch (IOException io) {
        }

        return null;
    }

    private String[] readProcFile()
            throws IOException {
        File file = new File("/proc/stat");
        BufferedReader rd = new BufferedReader(new FileReader(file));
        String line;

        while ((line = rd.readLine()) != null) {
            if (line.startsWith("cpu ")) {
                rd.close();
                return line.split(" ");
            }
        }
        rd.close();
        return null;
    }

    public double getCpuUsageValue()
    {
        return getMeasurement().doubleValue();
    }
}
