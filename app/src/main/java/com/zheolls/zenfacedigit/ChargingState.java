package com.zheolls.zenfacedigit;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

import com.zheolls.zenfacedigit.network.NetworkClient;
import com.zheolls.zenfacedigit.network.StatusCode;
import com.zheolls.zenfacedigit.network.UrlBuilder;
import com.zheolls.zenfacedigit.network.applications.DataCollect;
import com.zheolls.zenfacedigit.network.beans.FileBackBean;
import com.zheolls.zenfacedigit.network.helpers.request.DataBack;
import com.zheolls.zenfacedigit.network.helpers.upload.UploadHelper;
import com.zheolls.zenfacedigit.utils.LocalCache;
import com.zheolls.zenfacedigit.utils.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


public class ChargingState extends BroadcastReceiver {
    private static final String TAG = "ChargingState";
    private static MyWatchFace myWatchFace;
    private static boolean wifiSwitch = false;
    static WifiManager wifi;
    static WifiManager.WifiLock wifiLock;

    public ChargingState(MyWatchFace myWatchFace) {
        this.myWatchFace = myWatchFace;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        // wait charging connection stable
        try {
            Thread.sleep(5000L);  // 5s
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean isCharging = isPlugged(myWatchFace);
        Log.e(TAG, "isCharging?:" + isCharging);
        wifi = (WifiManager) myWatchFace.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiLock = wifi.createWifiLock(WifiManager.WIFI_MODE_FULL_HIGH_PERF, TAG);

        if (isCharging) {
            wifi.setWifiEnabled(true);//Turn on Wifi

            if (!wifiLock.isHeld()) {
                wifiLock.acquire();
            }
            // ????????????????????????????????????
            uploadSensorData();
            uploadLog();
        } else {
            if (wifiLock.isHeld())
                wifiLock.release();
            wifi.setWifiEnabled(!wifiSwitch);//Turn off Wifi

        }
    }

    public static boolean checkWifiOnAndConnected(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        if (wifiMgr.isWifiEnabled()) { // Wi-Fi adapter is ON

            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

            if (wifiInfo.getNetworkId() == -1) {
                return false; // Not connected to an access point
            }
            return true; // Connected to an access point
        } else {
            return false; // Wi-Fi adapter is OFF
        }
    }


    public static boolean isPlugged(Context context) {
        boolean isPlugged;
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;

        return isPlugged;
    }

    public static float getBatteryLevel() {
        Intent batteryStatus = myWatchFace.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return level / (float) scale;
    }

    public static String getCpu() {
        Log.i("getCPU", "CPU");
        String path = "/proc/stat";// ??????CPU????????????
        try {
            int pid = Binder.getCallingPid();
            String cpuPath = "/proc" + pid + "/stat";
            String cpu = "";
            String[] result = new String[3];
            File f = new File(cpuPath);
            if (!f.exists() || !f.canRead()) {
                return result.toString();
            }
            FileReader fr = null;
            BufferedReader localBuffererReader = null;
            fr = new FileReader(f);
            localBuffererReader = new BufferedReader(fr, 8192);
            cpu = localBuffererReader.readLine();
            if (null != cpu) {
                String[] cpuSplit = cpu.split(" ");
                result[0] = cpuSplit[1];
                result[1] = cpuSplit[13];
                result[2] = cpuSplit[14];
            }
            return result.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
        // return String.format("%.2f",rate);
    }

    public static String getRom() {
        Log.i("getROM", "ROM");
        StringBuilder sb = new StringBuilder();
        final StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long totalCounts = statFs.getBlockCountLong();//?????????block???
        long availableCounts = statFs.getAvailableBlocksLong(); //???????????????block???
        long size = statFs.getBlockSizeLong(); //?????????????????????????????????4KB==
        long availROMSize = availableCounts * size;//????????????????????????
        long totalROMSize = totalCounts * size; //?????????????????????

        return "??????????????????ROM : " + availROMSize + "??????ROM: " + totalROMSize;
    }

    public static String getRAM(Context context) {
        Log.i("getRAM", "RAM");
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            manager.getMemoryInfo(mi);
            // ?????????
            String totalMem = Formatter.formatFileSize(context, mi.totalMem);
            //
            float totalMemory = (float) (Runtime.getRuntime().totalMemory() * 1.0 / (1024 * 1024));
            return "???????????????????????? totalMem: " + totalMem + "app??????????????????totalMemory: " + totalMemory;
        } catch (Exception e) {
            return "test";
        }

    }

    private File[] getAllFile(String path) {
        Log.i(TAG, "????????????????????????????????????: " + path);
        File directory = new File(path);
        return directory.listFiles();
    }

    private void uploadSensorData() {
        Log.i(TAG, "????????????????????????");
        File[] sensorDataFiles = getAllFile(IOLogData.sensorDataFolder);
        if (sensorDataFiles == null || sensorDataFiles.length == 0) {
            Log.i(TAG, "?????????????????????????????????");
            return;
        }
        // ??????120??????????????????
        UploadHelper uploadHelper = NetworkClient.upload(
                UrlBuilder.builder(DataCollect.name, DataCollect.urls.uploadFiles),
                120L * 1000L
        );
        uploadHelper.setDataBackClass(FileBackBean.class);
        uploadHelper.addPostParam("uid", 1);
        uploadHelper.addPostParam("type", "sensor");
        uploadHelper.addFiles(DataCollect.fileTags.sensorData, sensorDataFiles);
        try {
            uploadHelper.doPostFiles(true, new DataBack<FileBackBean>() {
                @Override
                public void onResponse(FileBackBean data) {
                    if (data.getStatusCode() == StatusCode.Ok) {
                        Log.i(TAG, "?????????????????????????????????");
                    }
                    else {
                        Log.i(TAG, "????????????????????????" + data.getTip());
                        Map<StatusCode, ArrayList<String>> reverseData = data.getReverseData();
                        for (Map.Entry<StatusCode, ArrayList<String>> entry : reverseData.entrySet()) {
                            Log.e(TAG, entry.getKey().getDescription() + "?????? " + entry.getValue().size() + " ???");
                        }
                    }
                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, "??????????????????????????????" + message);
                }
            });

        }
        catch (UploadHelper.FileCountException fileCountException) {
            fileCountException.printStackTrace();
        }
    }

    private void uploadLog() {
        Log.i(TAG, "???????????????");
        File[] logFiles = getAllFile(IOLogData.logFolder);
        if (logFiles == null || logFiles.length == 0) {
            Log.i(TAG, "??????????????????????????????");
            return;
        }
        UploadHelper uploadHelper = NetworkClient.upload(UrlBuilder.builder(DataCollect.name, DataCollect.urls.uploadFiles));
        uploadHelper.setDataBackClass(FileBackBean.class);
        uploadHelper.addPostParam("uid", 1);
        uploadHelper.addPostParam("type", "log");
        uploadHelper.addFiles(DataCollect.fileTags.sensorData, logFiles);
        try {
            uploadHelper.doPostFiles(false, new DataBack<FileBackBean>() {
                @Override
                public void onResponse(FileBackBean data) {
                    if (data.getStatusCode() == StatusCode.Ok) {
                        Log.i(TAG, "??????????????????????????????");

                        int deleteCount = 0;
                        // ??????????????????????????????
                        for (File logFile : logFiles) {
                            if (!logFile.getName().startsWith(
                                    Util.filenameUsableTimeString(
                                            Util.dateDatetime(System.currentTimeMillis())))) {
                                if (logFile.delete()) {
                                    deleteCount += 1;
                                }
                            }
                        }
                        Log.i(TAG, "????????? " + deleteCount + " ???????????????");
                    }
                    else {
                        Log.i(TAG, "????????????????????????" + data.getTip());
                        Map<StatusCode, ArrayList<String>> reverseData = data.getReverseData();
                        for (Map.Entry<StatusCode, ArrayList<String>> entry : reverseData.entrySet()) {
                            Log.e(TAG, entry.getKey().getDescription() + "?????? " + entry.getValue().size() + " ???");
                        }
                    }
                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, "?????????????????????" + message);
                }
            });

        }
        catch (UploadHelper.FileCountException fileCountException) {
            fileCountException.printStackTrace();
        }
    }
}
