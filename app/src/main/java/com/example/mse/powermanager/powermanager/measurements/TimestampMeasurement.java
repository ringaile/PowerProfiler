package com.example.mse.powermanager.powermanager.measurements;


public class TimestampMeasurement implements Measurement{

    public String getName() {

        return "timestamp";
    }

    public Double getMeasurement() {
        Long timestamp = new Long(System.currentTimeMillis() / 1000);
        return timestamp.doubleValue();
    }

    public long getTimestampValue()
    {
        return (System.currentTimeMillis() / 1000);
    }
}
