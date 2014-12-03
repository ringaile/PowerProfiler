package com.example.mse.powermanager.powermanager.measurements;

import java.io.File;
import java.io.RandomAccessFile;

public class MemoryMeasurement implements Measurement {

    public String getName() {
        return "memory_free";
    }

    public Double getMeasurement() {
        try {
            String[] measurements = this.readProcFile();

            Double total = Double.parseDouble(measurements[0]);
            Double free = Double.parseDouble(measurements[1]);

            return (free / total) * 100;
        } catch (Exception e) {
            return null;
        }
    }

    private String[] readProcFile()
            throws Exception {
        File file = new File("/proc/meminfo");
        RandomAccessFile mem = new RandomAccessFile(file, "r");
        String[] result = new String[2];

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
