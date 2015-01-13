package com.example.mse.powermanager.powermanager;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
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
import com.example.mse.powermanager.powermanager.singleProcessUtil.CpuInfo;
import com.example.mse.powermanager.powermanager.singleProcessUtil.CurrentInfo;
import com.example.mse.powermanager.powermanager.singleProcessUtil.MemoryInfo;
import com.example.mse.powermanager.powermanager.singleProcessUtil.ProcessInfo;
import com.example.mse.powermanager.powermanager.singleProcessUtil.Programe;
import com.example.mse.powermanager.powermanager.structs.MeasurementStruct;
import android.bluetooth.BluetoothAdapter;
import android.view.WindowManager;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class MeasurementReceiver extends BroadcastReceiver{
    private PowerManager pm;
    private Context context;

    //Process stuff
//    private int delaytime;
//    private DecimalFormat fomart;
//    private MemoryInfo memoryInfo;
//    private Handler handler = new Handler();
//    private CpuInfo cpuInfo;
//    private boolean isFloating;
//    private String processName, packageName, startActivity;
//    private int pid, uid;
//    private boolean isServiceStop = false;
//
//    public static String resultFilePath;
//    public static boolean isStop = false;
//
//    private String totalBatt;
//    private String temperature;
//    private String voltage;
//    private CurrentInfo currentInfo;
//    private SingleProcessService.BatteryInfoBroadcastReceiver batteryBroadcast = null;


    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.context = context;
        //String fileid = intent.getExtras().getString("fileid");
        pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        //Log.d("RECEIVER", "file_id " + fileid);
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
        //boolean wifiEnabled = wifiManager.isWifiEnabled();
        //Log.d("wifi enabled >", String.valueOf(wifiEnabled));

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.disable();
        //boolean bluetoothEnabled = mBluetoothAdapter.isEnabled();
        //Log.d("bluetooth enabled >", String.valueOf(bluetoothEnabled));

        PowerManagerApp.mainActivity.setScreenBrightness(0.1f);

//        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
//        WindowManager.LayoutParams lp = PowerManagerApp.mainActivity.getWindow().getAttributes();
//        lp.screenBrightness =0.2f;// 100 / 100.0f;
//        PowerManagerApp.mainActivity.getWindow().setAttributes(lp);
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
            meanMemoryFree *= 100;
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
                if ( (meanProcessorLoad > 50) || (meanMemoryFree < 75) )
                {
                    //ACTION
                    Log.d(">>> ACTION", "saving action");
                    PowerManagerApp.warningsList.add("Warning!\nProcessor load: "+String.format("%.2f",meanProcessorLoad)+"%\nMemory free: "+String.format("%.2f",meanMemoryFree)+"%");

                    turnOff();
                }
                else
                {
                    turnOn();
                }
            }
            else if (PowerManagerApp.mode == 1)
            {
                if ( (meanProcessorLoad > 75) || (meanMemoryFree < 25) )
                {
                    //ACTION
                    Log.d(">>> ACTION", "normal action");
                    PowerManagerApp.warningsList.add("Warning!\nProcessor load: "+String.format("%.2f",meanProcessorLoad)+"%\nMemory free: "+String.format("%.2f",meanMemoryFree)+"%");
                    turnOff();
                }
                else
                {
                    turnOn();
                }
            }
            else
            {

            }

            getProcessesList();
            showWarnings();

            PowerManagerApp.measurementIterations.clear();
            PowerManagerApp.warningsList.clear();
            //Log.d("CLEAR", "clear");
        }
    }

    private void showWarnings()
    {
        String resultStr = "";
        for (String warning: PowerManagerApp.warningsList)
        {
            resultStr += warning;
            resultStr += "\n";
        }
        final String result = resultStr;
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable()
        {
            public void run()
            {
                PowerManagerApp.mainActivity.showNotification(result);
                Toast.makeText(context, result, Toast.LENGTH_SHORT).show();
            }
        });
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
        measurement.memoryFree = (new MemoryMeasurement(context)).getMemoryFreeValue();

        //TODO: check this, we need only one value, not many
        for (String interface_name : ReceiveMeasurement.getInterfaceNames())
        {
            //Log.d(">>> interface name:", interface_name);
            if (interface_name.equals("wlan0"))
            {
                measurement.networkReceived = (new ReceiveMeasurement(interface_name)).getReceivedNetworkValue();
                measurement.networkSent = (new TransmitMeasurement(interface_name)).getSentNetworkValue();
                break;
            }
        }

        return measurement;
    }

    private void getProcessesList()
    {
        ProcessInfo processInfo = new ProcessInfo();
        List<Programe> processList = processInfo.getRunningProcess(PowerManagerApp.mainActivity.getBaseContext());
        for (Programe programe : processList)
        {
            if (programe.getPackageName() != null)
            {
                int pid = programe.getPid();
                int uid = programe.getUid();
                Log.d("PROCESS","name: "+programe.getProcessName()+"   pid: "+String.valueOf(pid)+"   uid: "+String.valueOf(uid));


                MemoryInfo memoryInfo = new MemoryInfo();
                long pidMemory = memoryInfo.getPidMemorySize(pid, PowerManagerApp.mainActivity.getBaseContext());
                long freeMemory = memoryInfo.getFreeMemorySize(PowerManagerApp.mainActivity.getBaseContext());
                long totalMemory = memoryInfo.getTotalMemory();
                DecimalFormat formatter = new DecimalFormat();
                formatter.setMaximumFractionDigits(2);
                formatter.setMinimumFractionDigits(0);
                String totalMemoryMb = formatter.format((double) totalMemory / 1024);
                String freeMemoryMb = formatter.format((double) freeMemory / 1024);
                String processMemoryMb = formatter.format((double) pidMemory / 1024);
                Log.d("PROCESS","MB -> process memory: "+processMemoryMb+"   free memory: "+freeMemoryMb+"   total memory: "+totalMemoryMb);
                double proportion = (double)pidMemory/(double)totalMemory;
                Log.d("PROCESS","mem porportion: "+formatter.format(proportion));
                if (proportion > 0.01)
                {
                    PowerManagerApp.warningsList.add("Warning! Process <"+programe.getProcessName()+"> uses "+formatter.format(proportion*100)+"% ("+processMemoryMb+"Mb) of total memory.");
                }

                //CPU stuff which is not working
//                CpuInfo cpuInfo = new CpuInfo(PowerManagerApp.mainActivity.getBaseContext(), pid, Integer.toString(uid));
//                cpuInfo.readCpuStat();
//
//                String processCpuRatio = "0.00";
//                String totalCpuRatio = "0.00";
//                String trafficSize = "0";

//                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
//                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//                totalBatt = String.valueOf(level * 100 / scale);
//                voltage = String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) * 1.0 / 1000);
//                temperature = String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) * 1.0 / 10);
//                CurrentInfo currentInfo = new CurrentInfo();
//                String currentBatt = String.valueOf(currentInfo.getCurrentValue());

//                ArrayList<String> cpuRatioInfo = cpuInfo.getCpuRatioInfo("", "", "", "");
//                if (!cpuRatioInfo.isEmpty())
//                {
//                    //Log.d(">>>","size: "+cpuRatioInfo.size());
//                    processCpuRatio = cpuRatioInfo.get(0);
//                    totalCpuRatio = cpuRatioInfo.get(1);
//                    trafficSize = cpuRatioInfo.get(2);
//                    Log.d(">>>PROCESS","processCpuRatio: "+processCpuRatio+"   totalCpuRatio: "+totalCpuRatio+"   trafficSize: "+trafficSize);
//                }

            }
        }
    }



}
