package com.example.mse.powermanager.powermanager.singleProcessUtil;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TrafficInfo {

	private String uid;

	public TrafficInfo(String uid) {
		this.uid = uid;
	}


	public long getTrafficInfo() {
		RandomAccessFile rafRcv = null, rafSnd = null;
		String rcvPath = "/proc/uid_stat/" + uid + "/tcp_rcv";
		String sndPath = "/proc/uid_stat/" + uid + "/tcp_snd";
		long rcvTraffic = -1;
		long sndTraffic = -1;
		try {
			rafRcv = new RandomAccessFile(rcvPath, "r");
			rafSnd = new RandomAccessFile(sndPath, "r");
			rcvTraffic = Long.parseLong(rafRcv.readLine());
			sndTraffic = Long.parseLong(rafSnd.readLine());
		} catch (FileNotFoundException e) {
			rcvTraffic = -1;
			sndTraffic = -1;
		} catch (NumberFormatException e) {
			Log.e("LogError", "NumberFormatException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("LogError", "IOException: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (rafRcv != null) {
					rafRcv.close();
				}
				if (rafSnd != null)
					rafSnd.close();
			} catch (IOException e) {
				Log.i("LogError",
						"close randomAccessFile exception: " + e.getMessage());
			}
		}
		if (rcvTraffic == -1 || sndTraffic == -1) {
			return -1;
		} else
			return rcvTraffic + sndTraffic;
	}
}
