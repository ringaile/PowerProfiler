package com.example.mse.powermanager.powermanager.structs;

/**
 * Created by stanislavkimov on 12/3/14.
 */
public class MeasurementStruct
{
    public long timestamp;
    public double batteryLevel;

    public boolean wifiStatus;
    public boolean bluetoothStatus;
    public boolean gpsStatus;
    public boolean mobileStatus;
    public boolean screenStatus;

    public double screenBrightness;
    public double memoryFree;
    public double cpuUsage;
    public double cpuFrequency;

    public double networkReceived;
    public double networkSent;
}
