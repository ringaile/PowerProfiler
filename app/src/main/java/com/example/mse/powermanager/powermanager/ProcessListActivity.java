package com.example.mse.powermanager.powermanager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.mse.powermanager.powermanager.singleProcessUtil.ProcessInfo;
import com.example.mse.powermanager.powermanager.singleProcessUtil.Programe;

import java.io.IOException;
import java.util.List;


public class ProcessListActivity extends Activity {

    private static final int TIMEOUT = 20000;

    private ProcessInfo processInfo;
    private Intent monitorService;
    private ListView lstViProgramme;
    private Button btnTest;
    private int pid, uid;
    private boolean isServiceStop = false;
    private UpdateReceiver receiver;
    private Long mExitTime = (long) 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_list);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_list);
        initTitleLayout();
        processInfo = new ProcessInfo();
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                monitorService = new Intent();
                monitorService.setClass(ProcessListActivity.this, SingleProcessService.class);
                if (getString(R.string.start_test).equals(btnTest.getText().toString())) {
                    ListAdapter adapter = (ListAdapter) lstViProgramme.getAdapter();
                    if (adapter.checkedProg != null) {
                        String packageName = adapter.checkedProg.getPackageName();
                        String processName = adapter.checkedProg.getProcessName();
                        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
                        String startActivity = "";
                        Log.d("Log", packageName);
                        // clear logcat
                        try {
                            Runtime.getRuntime().exec("logcat -c");
                        } catch (IOException e) {
                            Log.d("Log", e.getMessage());
                        }
                        try {
                            startActivity = intent.resolveActivity(getPackageManager()).getShortClassName();
                            startActivity(intent);
                        } catch (Exception e) {
                            return;
                        }
                        waitForAppStart(packageName);
                        monitorService.putExtra("processName", processName);
                        monitorService.putExtra("pid", pid);
                        monitorService.putExtra("uid", uid);
                        monitorService.putExtra("packageName", packageName);
                        monitorService.putExtra("startActivity", startActivity);
                        startService(monitorService);
                        isServiceStop = false;
                        btnTest.setText(getString(R.string.stop_test));
                    }
                } else {
                    btnTest.setText(getString(R.string.start_test));
                    stopService(monitorService);
                }
            }
        });
        lstViProgramme.setAdapter(new ListAdapter());
        lstViProgramme.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RadioButton rdBtn = (RadioButton) ((LinearLayout) view).getChildAt(0);
                rdBtn.setChecked(true);
            }
        });

        receiver = new UpdateReceiver();
        IntentFilter filter = new IntentFilter();
        registerReceiver(receiver, filter);

    }

    private void initTitleLayout() {
        lstViProgramme = (ListView) findViewById(R.id.processList);
        btnTest = (Button) findViewById(R.id.test);
    }

    private void waitForAppStart(String packageName) {
        boolean isProcessStarted = false;
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + TIMEOUT) {
            List<Programe> processList = processInfo.getRunningProcess(getBaseContext());
            for (Programe programe : processList) {
                if ((programe.getPackageName() != null) && (programe.getPackageName().equals(packageName))) {
                    pid = programe.getPid();
                    uid = programe.getUid();
                    if (pid != 0) {
                        isProcessStarted = true;
                        break;
                    }
                }
            }
            if (isProcessStarted) {
                break;
            }
        }
    }

    public class UpdateReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            isServiceStop = intent.getExtras().getBoolean("isServiceStop");
            if (isServiceStop) {
                btnTest.setText(getString(R.string.start_test));
            }
        }
    }

    protected void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        if (isServiceStop) {
            btnTest.setText(getString(R.string.start_test));
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                mExitTime = System.currentTimeMillis();
            } else {
                if (monitorService != null) {
                    stopService(monitorService);
                }
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    static class Viewholder {
        TextView txtAppName;
        ImageView imgViAppIcon;
        RadioButton rdoBtnApp;
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private class ListAdapter extends BaseAdapter {
        List<Programe> programes;
        Programe checkedProg;
        int lastCheckedPosition = -1;

        public ListAdapter() {
            programes = processInfo.getRunningProcess(getBaseContext());
        }

        @Override
        public int getCount() {
            return programes.size();
        }

        @Override
        public Object getItem(int position) {
            return programes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Programe pr = (Programe) programes.get(position);
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            Viewholder holder = (Viewholder) convertView.getTag();
            if (holder == null) {
                holder = new Viewholder();
                convertView.setTag(holder);
                holder.imgViAppIcon = (ImageView) convertView.findViewById(R.id.image);
                holder.txtAppName = (TextView) convertView.findViewById(R.id.text);
                holder.rdoBtnApp = (RadioButton) convertView.findViewById(R.id.rb);
                holder.rdoBtnApp.setFocusable(false);
                holder.rdoBtnApp.setOnCheckedChangeListener(checkedChangeListener);
            }
            holder.imgViAppIcon.setImageDrawable(pr.getIcon());
            holder.txtAppName.setText(pr.getProcessName());
            holder.rdoBtnApp.setId(position);
            holder.rdoBtnApp.setChecked(checkedProg != null && getItem(position) == checkedProg);
            return convertView;
        }

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    final int checkedPosition = buttonView.getId();
                    if (lastCheckedPosition != -1) {
                        RadioButton tempButton = (RadioButton) findViewById(lastCheckedPosition);
                        if ((tempButton != null) && (lastCheckedPosition != checkedPosition)) {
                            tempButton.setChecked(false);
                        }
                    }
                    checkedProg = programes.get(checkedPosition);
                    lastCheckedPosition = checkedPosition;
                }
            }
        };
    }
}
