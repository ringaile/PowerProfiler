package com.example.mse.powermanager.powermanager.measurements.network;

public class ReceiveMeasurement extends NetworkMeasurement {
    public ReceiveMeasurement(String interface_name) {
        super(interface_name);
    }

    public double getReceivedNetworkValue()
    {
        String[] traffic = new String[0];
        try { traffic = super.readProcFile(); }
            catch (Exception e) { e.printStackTrace(); }
        Double received = Double.parseDouble(traffic[0]);
        return received.doubleValue();
    }

}
