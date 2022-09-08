package com.zheolls.zenfacedigit.sensor_listener;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.PowerManager;
import android.util.Log;

import com.zheolls.zenfacedigit.ChargingState;
import com.zheolls.zenfacedigit.const_value.SensorProperty;
import com.zheolls.zenfacedigit.IOLogData;
import com.zheolls.zenfacedigit.PermissionActivity;

public class SensorDataListener implements SensorEventListener {
    private final String TAG = getClass().getSimpleName();

    private final PowerManager.WakeLock wakeLock;
    // 用于呼起 Activity、Service 等
    private final Context context;

    // 传感器名称
    private final String sensorName;
    // 传感器数据维度
    private final int dimension;
    // 数据缓冲区管理器
    BufferManager bufferManager;

    public SensorDataListener(final Context context, final PowerManager.WakeLock wakeLock,
                              final String sensorName, final int bufferSize, final int dimension) {
        this.context = context;
        this.wakeLock = wakeLock;
        this.sensorName = sensorName;
        this.dimension = dimension;

        bufferManager = new BufferManager(sensorName, dimension, bufferSize);
    }

    public float getBufferUsedPercentage() {
        return bufferManager.getBufferUsedPercentage();
    }

    // 获取上次将缓冲区数据写入文件的时间戳
    public long getLastSaveTime() {
        return bufferManager.getLastSaveTime();
    }

    // 获取缓冲区中最新的传感器数据
    public float[] getLatestData() {
        return bufferManager.getLatestData();
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (!wakeLock.isHeld()) {
            Log.v(TAG, "请求 唤醒锁 中！");
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        }

        if (bufferManager.isFull()) {
            // TODO:这个放到这里感觉很怪,看能不能调整到别的什么位置
            if (sensorName.equals(SensorProperty.Accelerometer.name)) {
                IOLogData.writeLog(TAG, "battery: " + ChargingState.getBatteryLevel());
                IOLogData.writeLog(TAG, "CPU: " + ChargingState.getCpu());
                IOLogData.writeLog(TAG, "ROM:" + ChargingState.getRom());
                IOLogData.writeLog(TAG, "RAM:" + ChargingState.getRAM(context));
            }
        }

        if (!bufferManager.putData(sensorEvent.timestamp, sensorEvent.values)) {
            Log.e(TAG, "传感器数据写入失败，已再次请求权限！");
            context.startActivity(new Intent(context, PermissionActivity.class));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    // 主动保存文件
    public void saveData() {
        if (!bufferManager.saveData()) {
            Log.e(TAG, "传感器数据写入失败，已再次请求权限！");
            context.startActivity(new Intent(context, PermissionActivity.class));
        }
    }
}
