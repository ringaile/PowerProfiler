package com.example.mse.powermanager.powermanager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
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

//    public void showNotificationNums(double proc, double mem)
//    {
////        Intent intent = new Intent(this, NotificationReceiver.class);
////        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
//
//        // Build notification
//        // Actions are just fake
//        Notification noti = new Notification.Builder(PowerManagerApp.mainActivity)
//                .setContentTitle("Warning!")
//                .setContentText("Processor load: "+String.format("%.2f",proc)+"%\nMemory free: "+String.format("%.2f",mem)+"%").setSmallIcon(R.drawable.icon).build();
////                .setContentIntent(pIntent).build();
////                .addAction(R.drawable.icon, "Call", pIntent)
////                .addAction(R.drawable.icon, "More", pIntent)
////                .addAction(R.drawable.icon, "And more", pIntent).build();
//        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        // hide the notification after its selected
//        noti.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        notificationManager.notify(0, noti);
//    }

    public void showNotification(String text)
    {
        Notification noti = new Notification.Builder(PowerManagerApp.mainActivity)
                .setContentTitle("Warning!")
                .setContentText(text).setSmallIcon(R.drawable.icon).build();
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);
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
