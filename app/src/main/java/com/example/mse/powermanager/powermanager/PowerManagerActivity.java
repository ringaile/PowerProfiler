package com.example.mse.powermanager.powermanager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;


public class PowerManagerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        PowerManagerApp.mainActivity = this;
    }

//    public void readSomeCPUShit() throws Exception
//    {
//        File file = new File("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
//        RandomAccessFile rafile = new RandomAccessFile(file, "r");
//
//        String line;
//        while ((line = rafile.readLine()) != null)
//        {
//            Log.e("CPU shit out", line);
//        }
//        rafile.close();
//    }

    public void buttonStartOnClick(View v)
    {
        Button b = (Button) v;
            if (!PowerManagerActivity.this.isAlarmSet()) {
                PowerManagerActivity.this.setRepeatingAlarm();

                b.setText("Stop Measuring");

                Toast.makeText(
                        getApplicationContext(),
                        "Measurement started",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                PowerManagerActivity.this.removeRepeatingAlarm();

                b.setText("Start Measuring");

                Toast.makeText(
                        getApplicationContext(),
                        "Measurement stopped",
                        Toast.LENGTH_SHORT
                ).show();
            }
    }

    public void buttonSavingOnClick(View v)
    {
        Log.d("Set to mode:", "SAVING");
        PowerManagerApp.mode = 0;
    }

    public void buttonNormalOnClick(View v)
    {
        Log.d("Set to mode:", "NORMAL");
        PowerManagerApp.mode = 1;
    }

    public void buttonPerformanceOnClick(View v)
    {
        Log.d("Set to mode:", "PERFORMANCE");
        PowerManagerApp.mode = 2;
    }

    public void setScreenBrightness(float brightness)
    {
        Settings.System.putInt(PowerManagerApp.getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        final WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = brightness;// 100 / 100.0f;
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                getWindow().setAttributes(lp);
            }
        });
        ///getWindow().setAttributes(lp);
        //PowerManagerApp.getContext().startActivity(new Intent(this,PowerManagerActivity.class));
    }


    public void buttonListOnClick(View v)
    {
        Button b = (Button) v;
        Intent intent = new Intent(this, ProcessListActivity.class);
        startActivity(intent);
    }

    private boolean isAlarmSet(){
        Intent intent = new Intent(PowerManagerApp.getContext(), MeasurementReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(
                PowerManagerApp.getContext(),
                0,
                intent,
                PendingIntent.FLAG_NO_CREATE
        );

        if (pending != null) {
            return true;
        }

        return false;
    }

    private void removeRepeatingAlarm(){
        AlarmManager am = (AlarmManager)PowerManagerApp.getContext().getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(PowerManagerApp.getContext(), MeasurementReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(
                PowerManagerApp.getContext(),
                0,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );
        PowerManagerApp.setFileId("");
        am.cancel(pending);
        pending.cancel();
    }

    private void setRepeatingAlarm(){
        Thread th = new Thread(new Runnable() {
            public void run() {
                AlarmManager am = (AlarmManager)PowerManagerApp.getContext().getSystemService(Context.ALARM_SERVICE);
                PowerManagerApp.setFileId("" + (System.currentTimeMillis() / 1000));
                Intent intent = new Intent(PowerManagerApp.getContext(), MeasurementReceiver.class);
                // use the current timestamp as id for the CSVWriter
                intent.putExtra("fileid", "" + (System.currentTimeMillis() / 1000));

                // initialize PendingIntent
                PendingIntent pending = PendingIntent.getBroadcast(
                        PowerManagerApp.getContext(),
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );

                // set repeating alarm every minute
                am.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis(),
                        1 * 1000,
                        pending
                );
            }
        });

        th.start();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
