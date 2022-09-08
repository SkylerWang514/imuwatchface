package com.zheolls.zenfacedigit;

import android.os.Environment;
import android.util.Log;

import com.zheolls.zenfacedigit.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ruixuandai on 11/7/17.
 */

public class IOLogData {
    private static final String TAG = "IOLogData";
    // 传感器数据保存文件夹
    static final String sensorDataFolder = Environment.getExternalStorageDirectory() + "/CPSL/Raw";
    // 系统状态日志文件夹
    static final String logFolder = Environment.getExternalStorageDirectory() + "/CPSL/Log";

    public static void checkFile(File log) {
        /*
        Check if the log exists
         */
        if (log.exists()) {
            return;
        } else {
            try {
                if (!log.getParentFile().exists()) {
                    //父目录不存在 创建父目录
                    Log.e(TAG, "creating parent directory..." + log.getParentFile());
                    if (!log.getParentFile().mkdirs()) {
                        Log.e(TAG, "created parent directory failed.");
                    }
                }
                log.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "/n cannot create new file");
            }
        }
    }

    public static boolean writeData(String fileName,
                                    byte[][] data, boolean append) {
        long start = System.currentTimeMillis();
        File log = new File(sensorDataFolder, fileName);
        FileOutputStream fOutStream;

        Log.d(TAG, log.getName());
        checkFile(log);

        try {
            fOutStream = new FileOutputStream(log, append);
            for (byte[] col : data) {
                fOutStream.write(col);
            }

            fOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "wrt fail!!!!!!!");
            return false;
        }
        Log.d(TAG, "Write Time:" + (System.currentTimeMillis() - start));
        return true;

    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static void writeLog(String TAG, String logString) {
        long currentTimestamp = System.currentTimeMillis();
        File debugLog = new File(logFolder, Util.filenameUsableTimeString(Util.dateDatetime(currentTimestamp)) + "-watchFaceLog.txt");
        checkFile(debugLog);

        try {
            FileOutputStream lOutStream = new FileOutputStream(debugLog, true);
            String tempString = Util.timeOnlyDatetime(currentTimestamp) + "---" +
                    TAG + ": " + logString + "\n";

            lOutStream.write(tempString.getBytes());

            lOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "wrt fail!!!!!!!");
        }

    }


    public static void writeActivityLog(String act) {
        long currentTimestamp = System.currentTimeMillis();
        File actLog = new File(logFolder, Util.filenameUsableTimeString(Util.dateDatetime(currentTimestamp)) + "-actLog.txt");
        checkFile(actLog);

        try {
            FileOutputStream lOutStream = new FileOutputStream(actLog, true);
            String tempString = Util.timeOnlyDatetime(currentTimestamp) + "," + currentTimestamp + "," + act + "\n";
            lOutStream.write(tempString.getBytes());
            lOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "wrt fail!!!!!!!");
        }
    }
}