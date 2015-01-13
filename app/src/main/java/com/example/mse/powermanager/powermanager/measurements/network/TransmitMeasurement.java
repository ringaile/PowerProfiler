package com.example.mse.powermanager.powermanager.measurements.network;


public class TransmitMeasurement extends NetworkMeasurement {
    public TransmitMeasurement(String interface_name) {
        super(interface_name);
    }

    public double getSentNetworkValue()
    {
        String[] traffic = new String[0];
        try { traffic = super.readProcFile(); }
        catch (Exception e) { e.printStackTrace(); }
        Double received = Double.parseDouble(traffic[1]);
        return received.doubleValue();
    }
}