package com.example.mse.powermanager.powermanager.measurements;


public class TimestampMeasurement implements Measurement{

    public long getTimestampValue()
    {
        return (System.currentTimeMillis() / 1000);
    }
}
