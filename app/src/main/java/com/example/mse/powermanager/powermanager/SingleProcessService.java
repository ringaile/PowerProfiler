package com.example.mse.powermanager.powermanager;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.mse.powermanager.powermanager.singleProcessUtil.CpuInfo;
import com.example.mse.powermanager.powermanager.singleProcessUtil.CurrentInfo;
import com.example.mse.powermanager.powermanager.singleProcessUtil.MemoryInfo;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SingleProcessService extends Service {
    private static final String BLANK_STRING = "";

    private int delaytime;
    private DecimalFormat fomart;
    private MemoryInfo memoryInfo;
    private Handler handler = new Handler();
    private CpuInfo cpuInfo;
    private boolean isFloating;
    private String processName, packageName, startActivity;
    private int pid, uid;
    private boolean isServiceStop = false;

    public static String resultFilePath;
    public static boolean isStop = false;

    private String totalBatt;
    private String temperature;
    private String voltage;
    private CurrentInfo currentInfo;
    private BatteryInfoBroadcastReceiver batteryBroadcast = null;

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceStop = false;
        isStop = false;
        memoryInfo = new MemoryInfo();
        fomart = new DecimalFormat();
        fomart.setMaximumFractionDigits(2);
        fomart.setMinimumFractionDigits(0);
        currentInfo = new CurrentInfo();
        batteryBroadcast = new BatteryInfoBroadcastReceiver();
        registerReceiver(batteryBroadcast, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
    }

    public class BatteryInfoBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                totalBatt = String.valueOf(level * 100 / scale);
                voltage = String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) * 1.0 / 1000);
                temperature = String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) * 1.0 / 10);
            }

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, new Intent(this, ProcessListActivity.class), 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.icon).setWhen(System.currentTimeMillis()).setAutoCancel(true)
                .setContentTitle("PowerManager");
        startForeground(startId, builder.build());

        pid = intent.getExtras().getInt("pid");
        uid = intent.getExtras().getInt("uid");
        processName = intent.getExtras().getString("processName");
        packageName = intent.getExtras().getString("packageName");
        startActivity = intent.getExtras().getString("startActivity");

        cpuInfo = new CpuInfo(getBaseContext(), pid, Integer.toString(uid));
        readSettingInfo();
        handler.postDelayed(task, 1000);
        return START_NOT_STICKY;
    }

    /**
     * read configuration file.
     *
     * @throws java.io.IOException
     */
    private void readSettingInfo() {
        int interval = 1;
        delaytime = interval * 1000;
        isFloating = true;
    }


    private Runnable task = new Runnable() {

        public void run() {
            if (!isServiceStop) {
                dataRefresh();
                handler.postDelayed(this, delaytime);
            } else {
                Intent intent = new Intent();
                intent.putExtra("isServiceStop", true);
                sendBroadcast(intent);
                stopSelf();
            }
        }
    };

    /**
     * refresh the performance data showing in floating window.
     *
     * @throws java.io.FileNotFoundException
     *
     * @throws java.io.IOException
     */
    private void dataRefresh() {
        int pidMemory = memoryInfo.getPidMemorySize(pid, getBaseContext());
        long freeMemory = memoryInfo.getFreeMemorySize(getBaseContext());
        String freeMemoryKb = fomart.format((double) freeMemory / 1024);
        String processMemory = fomart.format((double) pidMemory / 1024);
        String currentBatt = String.valueOf(currentInfo.getCurrentValue());

        try {
            if (Math.abs(Double.parseDouble(currentBatt)) >= 500) {
                currentBatt = "N/A";
            }
        } catch (Exception e) {
            currentBatt = "N/A";
        }
        ArrayList<String> processInfo = cpuInfo.getCpuRatioInfo(totalBatt, currentBatt, temperature, voltage);
        if (isFloating) {
            String processCpuRatio = "0.00";
            String totalCpuRatio = "0.00";
            String trafficSize = "0";
            int tempTraffic = 0;
            double trafficMb = 0;
            boolean isMb = false;
            if (!processInfo.isEmpty()) {
                processCpuRatio = processInfo.get(0);
                totalCpuRatio = processInfo.get(1);
                trafficSize = processInfo.get(2);
                if (!(BLANK_STRING.equals(trafficSize)) && !("-1".equals(trafficSize))) {
                    tempTraffic = Integer.parseInt(trafficSize);
                    if (tempTraffic > 1024) {
                        isMb = true;
                        trafficMb = (double) tempTraffic / 1024;
                    }
                }

                if (processCpuRatio != null && totalCpuRatio != null) {
                    //txtUnusedMem.setText(getString(R.string.process_free_mem) + processMemory + "/" + freeMemoryKb + "MB");
                    Log.w("LogWriter", getString(R.string.process_free_mem) + processMemory + "/" + freeMemoryKb + "MB");
                    Log.w("LogWriter", getString(R.string.process_overall_cpu) + processCpuRatio + "%/" + totalCpuRatio + "%");
                    String batt = getString(R.string.current) + currentBatt;
                    if ("-1".equals(trafficSize)) {
                        Log.w("LogWriter", batt + "," + getString(R.string.traffic) + "N/A");
                    } else if (isMb) {
                        Log.w("LogWriter", batt + "," + getString(R.string.traffic) + fomart.format(trafficMb) + "MB");
                    }
                    else {
                        Log.w("LogWriter", batt + "," + getString(R.string.traffic) + trafficSize + "KB");
                    }
                }

                if ("0".equals(processMemory)) {
                    isServiceStop = true;
                    return;
                }
            }

        }
    }

    @Override
    public void onDestroy() {
        Log.i("LogError", "service onDestroy");
        handler.removeCallbacks(task);
        isStop = true;
        unregisterReceiver(batteryBroadcast);
        super.onDestroy();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
