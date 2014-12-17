package com.example.mse.powermanager.powermanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.provider.Settings;
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
import android.bluetooth.BluetoothAdapter;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MeasurementReceiver extends BroadcastReceiver{
    private PowerManager pm;
    ///private List<MeasurementStruct> measurementIterations;
    private Context context;
    //PowerManagerActivity mainActivity;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.context = context;
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

                    iteration();
                    //PowerManagerApp.addMeasurementIteration(perforMeasurementIteration());

                } catch (Exception e) {
                    System.out.println(e);
                } finally {
                    lock.release();
                }


            }
        };
        th.start();
    }

    private void turnOff()
    {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.disable();

        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        WindowManager.LayoutParams lp = PowerManagerApp.mainActivity.getWindow().getAttributes();
        lp.screenBrightness =0.2f;// 100 / 100.0f;
        PowerManagerApp.mainActivity.getWindow().setAttributes(lp);
    }

    private void turnOn()
    {
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
    }


    private void iteration()
    {
//        if (measurementIterations == null)
//        {
//            measurementIterations = new ArrayList<>();
//        }

        Log.d("iteration", String.valueOf(PowerManagerApp.measurementIterations.size()));
        MeasurementStruct measurement = perforMeasurementIteration();
        PowerManagerApp.measurementIterations.add(measurement);
        float meanProcessorLoad = 0;
        float meanMemoryFree = 0;
        int numberOfIterations = 3;
        if (PowerManagerApp.measurementIterations.size() >= numberOfIterations)
        {
            for (MeasurementStruct iteratedStruct: PowerManagerApp.measurementIterations)
            {
                Log.d(">>>>> timestamp", String.valueOf(iteratedStruct.timestamp));
                Log.d("battery level", String.valueOf(iteratedStruct.batteryLevel));

                Log.d("wifi", String.valueOf(iteratedStruct.wifiStatus));
                Log.d("bluetooth", String.valueOf(iteratedStruct.bluetoothStatus));
                Log.d("mobile", String.valueOf(iteratedStruct.mobileStatus));
                Log.d("gps", String.valueOf(iteratedStruct.gpsStatus));
                Log.d("screen on", String.valueOf(iteratedStruct.screenStatus));

                Log.d("screen brightness", String.valueOf(iteratedStruct.screenBrightness));
                Log.d("memory free", String.valueOf(iteratedStruct.memoryFree));
                Log.d("cpu usage", String.valueOf(iteratedStruct.cpuUsage));
                Log.d("cpu frequency", String.valueOf(iteratedStruct.cpuFrequency));

                Log.d("sent network", String.valueOf(iteratedStruct.networkSent));
                Log.d("received network", String.valueOf(iteratedStruct.networkReceived));

                meanProcessorLoad += iteratedStruct.cpuUsage;
                meanMemoryFree += iteratedStruct.memoryFree;
            }
            meanProcessorLoad /= numberOfIterations;
            meanMemoryFree /= numberOfIterations;
            //TODO: process gathered data
            //Make some changes to the system
            Log.d("Mean processor load", String.valueOf(meanProcessorLoad));
            Log.d("Mean memory free", String.valueOf(meanMemoryFree));

//            //WiFi methods >>
//            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
//            boolean wifiEnabled = wifiManager.isWifiEnabled();
//            wifiManager.setWifiEnabled(true);
//            wifiManager.setWifiEnabled(false);
//
//            //Bluetooth methods >>
//            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//            mBluetoothAdapter.isEnabled();
//            mBluetoothAdapter.enable();
//            mBluetoothAdapter.disable();
//
//            //Brightness methods >>
//            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
//            //Get the current system brightness
//            try {
//                int brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
//            } catch (Settings.SettingNotFoundException e) { e.printStackTrace(); }
//
//            //Set brightness
//            //Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 20);
//            WindowManager.LayoutParams lp = mainActivity.getWindow().getAttributes();
//            lp.screenBrightness =0.2f;// 100 / 100.0f;
//            mainActivity.getWindow().setAttributes(lp);
//            //context.startActivity(new Intent(mainActivity,PowerManagerActivity.class));

            if (PowerManagerApp.mode == 0)
            {
                if ( (meanProcessorLoad > 50) || (meanMemoryFree < 50) )
                {
                    //ACTION
                    Log.d("ACTION", "saving action");
                }
            }
            else if (PowerManagerApp.mode == 1)
            {
                if ( (meanProcessorLoad > 75) || (meanMemoryFree < 75) )
                {
                    //ACTION
                    Log.d("ACTION", "normal action");
                }
            }
            else
            {

            }

            PowerManagerApp.measurementIterations.clear();
        }
    }


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
            if (interface_name.equals("wlan0"))
            {
                Log.d("wlan0","yep");
                measurement.networkReceived = (new ReceiveMeasurement(interface_name)).getReceivedNetworkValue();
                measurement.networkSent = (new TransmitMeasurement(interface_name)).getSentNetworkValue();
                break;
            }
        }

        return measurement;
    }


}
