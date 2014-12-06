package com.example.mse.powermanager.powermanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.example.mse.powermanager.powermanager.measurements.BatteryMeasurement;
import com.example.mse.powermanager.powermanager.measurements.CpuUsageMeasurement;
import com.example.mse.powermanager.powermanager.measurements.GpsStatus;
import com.example.mse.powermanager.powermanager.measurements.MeasurementCollection;
import com.example.mse.powermanager.powermanager.measurements.MemoryMeasurement;
import com.example.mse.powermanager.powermanager.measurements.TimestampMeasurement;
import com.example.mse.powermanager.powermanager.measurements.WifiStatus;
import com.example.mse.powermanager.powermanager.measurements.network.ReceiveMeasurement;
import com.example.mse.powermanager.powermanager.measurements.network.TransmitMeasurement;
import com.example.mse.powermanager.powermanager.measurements.BluetoothStatus;
import com.example.mse.powermanager.powermanager.measurements.CpuFrequencyMeasurement;
import com.example.mse.powermanager.powermanager.measurements.MobileStatus;
import com.example.mse.powermanager.powermanager.measurements.ScreenStatus;
import com.example.mse.powermanager.powermanager.structs.MeasurementStruct;

import java.util.LinkedList;


public class MeasurementReceiver extends BroadcastReceiver{
    //private MeasurementCollection measurements;
    private PowerManager pm;

    @Override
    public void onReceive(Context context, Intent intent) {
        String fileid = intent.getExtras().getString("fileid");

        pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        //measurements = this.buildMeasurements();

        Log.d("RECEIVER", "file_id " + fileid);
        // spawn a new thread
        Thread th = new Thread() {
            public void run() {
                // acquire partial wake lock to prevent the system from sleeping
                PowerManager.WakeLock lock = pm.newWakeLock(
                        PowerManager.PARTIAL_WAKE_LOCK,
                        "charge_metric"
                );
                try {
                    lock.acquire();

                    PowerManagerApp.addMeasurementIteration(perforMeasurementIteration());
                    // save the measurements
//                    if(measurements != null){
//                        PowerManagerApp.addMeasurementCollection(measurements);
//                    }
                } catch (Exception e) {
                    System.out.println(e);
                } finally {
                    lock.release();
                }


            }
        };
        th.start();
    }

//    private MeasurementCollection buildMeasurements() {
//        Log.d("RECEIVER", "new instance");
//        MeasurementCollection measurements = new MeasurementCollection();
//
//        // add all measurements
//        measurements.addMeasurement(new TimestampMeasurement());
//        measurements.addMeasurement(new BatteryMeasurement(PowerManagerApp.getContext()));
//        measurements.addMeasurement(new ScreenStatus(PowerManagerApp.getContext()));
//        measurements.addMeasurement(new CpuFrequencyMeasurement());
//        measurements.addMeasurement(new CpuUsageMeasurement());
//        measurements.addMeasurement(new MemoryMeasurement());
//
////        for (String interface_name : ReceiveMeasurement.getInterfaceNames()) {
////            measurements.addMeasurement(new ReceiveMeasurement(interface_name));
////            measurements.addMeasurement(new TransmitMeasurement(interface_name));
////        }
//
//        measurements.addMeasurement(new MobileStatus(PowerManagerApp.getContext()));
//        measurements.addMeasurement(new WifiStatus(PowerManagerApp.getContext()));
//        measurements.addMeasurement(new BluetoothStatus(PowerManagerApp.getContext()));
//        measurements.addMeasurement(new GpsStatus(PowerManagerApp.getContext()));
//
//        return measurements;
//    }

//{"timestamp":1417611255,"bluetooth_status":0,"cpu_usage":5,"battery_level":66,"wifi_status":1,"mobile_status":0,"cpu_frequency":2265600,"gps_status":1,"memory_free":56.886164498080596,"screen_status":1}
    private MeasurementStruct perforMeasurementIteration()
    {
        MeasurementStruct measurement = new MeasurementStruct();
        measurement.timestamp = (new TimestampMeasurement()).getTimestampValue();
        measurement.batteryLevel = (new BatteryMeasurement(PowerManagerApp.getContext())).getBatteryLevelValue();

        measurement.mobileStatus = (new MobileStatus(PowerManagerApp.getContext())).getMobileStatusValue();
        measurement.wifiStatus = (new WifiStatus(PowerManagerApp.getContext())).getWifiStatusValue();
        measurement.bluetoothStatus = (new BluetoothStatus(PowerManagerApp.getContext())).getBluetoothStatusValue();
        measurement.gpsStatus = (new GpsStatus(PowerManagerApp.getContext())).getGpsStatusValue();
        measurement.screenStatus = (new ScreenStatus(PowerManagerApp.getContext())).getScreenStatusValue();

        measurement.screenBrightness = (new ScreenStatus(PowerManagerApp.getContext())).getScreenBrightnessValue();
        measurement.cpuFrequency = (new CpuFrequencyMeasurement()).getCpuFrequency();
        measurement.cpuUsage = (new CpuUsageMeasurement()).getCpuUsageValue();
        measurement.memoryFree = (new MemoryMeasurement()).getMemoryFreeValue();

        //TODO: check this, we need only one value, not many
        for (String interface_name : ReceiveMeasurement.getInterfaceNames())
        {
            Log.d(">>> interface name:", interface_name);
            measurement.networkReceived = (new ReceiveMeasurement(interface_name)).getReceivedNetworkValue();
            measurement.networkSent = (new TransmitMeasurement(interface_name)).getSentNetworkValue();
        }

        return measurement;
    }


}
