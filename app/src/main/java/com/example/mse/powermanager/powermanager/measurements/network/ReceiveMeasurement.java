package com.example.mse.powermanager.powermanager.measurements.network;

public class ReceiveMeasurement extends NetworkMeasurement {
    public ReceiveMeasurement(String interface_name) {
        super(interface_name);
    }

    public String getName() {
        return "rx_" + super.getName();
    }

    public Double getMeasurement() {
        try {
            String[] traffic = super.readProcFile();

            Double received = Double.parseDouble(traffic[0]);

            return received;
        } catch (Exception e) {
            return null;
        }
    }
}
