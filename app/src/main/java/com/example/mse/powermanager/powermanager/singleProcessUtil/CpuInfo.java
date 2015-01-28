package com.example.mse.powermanager.powermanager.singleProcessUtil;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class CpuInfo {

	private static final String LOG_TAG = "PowerManager" + CpuInfo.class.getSimpleName();

	private Context context;
	private long processCpu;
	private ArrayList<Long> idleCpu = new ArrayList<Long>();
	private ArrayList<Long> totalCpu = new ArrayList<Long>();
	private boolean isInitialStatics = true;
	private MemoryInfo mi;
	private long totalMemorySize;
	private long initialTraffic;
	private long lastestTraffic;
	private long traffic;
	private TrafficInfo trafficInfo;
	private ArrayList<String> cpuUsedRatio = new ArrayList<String>();
	private ArrayList<Long> totalCpu2 = new ArrayList<Long>();
	private long processCpu2;
	private ArrayList<Long> idleCpu2 = new ArrayList<Long>();
	private String processCpuRatio = "";
	private ArrayList<String> totalCpuRatio = new ArrayList<String>();
	private int pid;
	private static final String CPU_DIR_PATH = "/sys/devices/system/cpu/";
	private static final String CPU_STAT = "/proc/stat";
	private static final String COMMA = ",";

	public CpuInfo(Context context, int pid, String uid) {
		this.pid = pid;
		this.context = context;
		trafficInfo = new TrafficInfo(uid);
		mi = new MemoryInfo();
		totalMemorySize = mi.getTotalMemory();
		cpuUsedRatio = new ArrayList<String>();
	}

	public void readCpuStat() {
		String processPid = Integer.toString(pid);
		String cpuStatPath = "/proc/" + processPid + "/stat";
		try {
			RandomAccessFile processCpuInfo = new RandomAccessFile(cpuStatPath, "r");
			String line = "";
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.setLength(0);
			while ((line = processCpuInfo.readLine()) != null) {
				stringBuffer.append(line + "\n");
			}
			String[] tok = stringBuffer.toString().split(" ");
			processCpu = Long.parseLong(tok[13]) + Long.parseLong(tok[14]);
			processCpuInfo.close();
		} catch (FileNotFoundException e) {
			Log.e(LOG_TAG, "FileNotFoundException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		readTotalCpuStat();
	}

	private void readTotalCpuStat() {
		try {
			RandomAccessFile cpuInfo = new RandomAccessFile(CPU_STAT, "r");
			String line = "";
			while ((null != (line = cpuInfo.readLine())) && line.startsWith("cpu")) {
				String[] toks = line.split("\\s+");
				idleCpu.add(Long.parseLong(toks[4]));
				totalCpu.add(Long.parseLong(toks[1]) + Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
						+ Long.parseLong(toks[6]) + Long.parseLong(toks[5]) + Long.parseLong(toks[7]));
			}
			cpuInfo.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class CpuFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			// Check if filename matchs "cpu[0-9]"
			if (Pattern.matches("cpu[0-9]", pathname.getName())) {
				return true;
			}
			return false;
		}
	}


	public int getCpuNum() {
		try {
			// Get directory containing CPU info
			File dir = new File(CPU_DIR_PATH);
			// Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			return files.length;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

	public ArrayList<String> getCpuRatioInfo() {

		DecimalFormat format = new DecimalFormat();
		format.setGroupingUsed(false);
		format.setMaximumFractionDigits(2);
		format.setMinimumFractionDigits(2);

		cpuUsedRatio.clear();
		idleCpu.clear();
		totalCpu.clear();
		totalCpuRatio.clear();
		readCpuStat();

        if (isInitialStatics) {
            initialTraffic = trafficInfo.getTrafficInfo();
            isInitialStatics = false;
        } else {
            lastestTraffic = trafficInfo.getTrafficInfo();
            if (initialTraffic == -1)
                traffic = -1;
            else
                traffic = (lastestTraffic - initialTraffic + 1023) / 1024;
            StringBuffer totalCpuBuffer = new StringBuffer();
            if (null != totalCpu2 && totalCpu2.size() > 0) {
                processCpuRatio = format.format(100 * ((double) (processCpu - processCpu2) / ((double) (totalCpu.get(0) - totalCpu2.get(0)))));
                for (int i = 0; i < (totalCpu.size() > totalCpu2.size() ? totalCpu2.size() : totalCpu.size()); i++) {
                    String cpuRatio = "0.00";
                    if (totalCpu.get(i) - totalCpu2.get(i) > 0) {
                        cpuRatio = format
                                .format(100 * ((double) ((totalCpu.get(i) - idleCpu.get(i)) - (totalCpu2.get(i) - idleCpu2.get(i))) / (double) (totalCpu
                                        .get(i) - totalCpu2.get(i))));
                    }
                    totalCpuRatio.add(cpuRatio);
                    totalCpuBuffer.append(cpuRatio + COMMA);
                }
            } else {
                processCpuRatio = "0";
                totalCpuRatio.add("0");
                totalCpuBuffer.append("0,");
                totalCpu2 = (ArrayList<Long>) totalCpu.clone();
                processCpu2 = processCpu;
                idleCpu2 = (ArrayList<Long>) idleCpu.clone();
            }
            for (int i = 0; i < getCpuNum() - totalCpuRatio.size() + 1; i++) {
                totalCpuBuffer.append("0.00,");
            }
            if (isPositive(processCpuRatio) && isPositive(totalCpuRatio.get(0))) {
                totalCpu2 = (ArrayList<Long>) totalCpu.clone();
                processCpu2 = processCpu;
                idleCpu2 = (ArrayList<Long>) idleCpu.clone();
                cpuUsedRatio.add(processCpuRatio);
                cpuUsedRatio.add(totalCpuRatio.get(0));
                cpuUsedRatio.add(String.valueOf(traffic));
            }
        }
        return cpuUsedRatio;
	}


	private boolean isPositive(String text) {
		Double num;
		try {
			num = Double.parseDouble(text);
		} catch (NumberFormatException e) {
			return false;
		}
		return num >= 0;
	}

}
