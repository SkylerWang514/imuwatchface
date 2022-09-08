package com.zheolls.zenfacedigit.sensor_listener;

import android.util.Log;

import com.zheolls.zenfacedigit.IOLogData;
import com.zheolls.zenfacedigit.utils.Util;

import java.nio.FloatBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;

/**
 * 管理传感器数据的缓冲区,特性:
 * - 在向缓冲区中添加数据时,若缓冲区已满则会自动将缓冲区中的数据写入到文件中
 * - 也可以通过调用 saveData 主动将缓冲区中的数据保存到文件中
 */
public class BufferManager {
    private final String TAG = getClass().getSimpleName();

    // 数据采集时间缓冲区
    private LongBuffer timeBuffer = null;
    // 传感器数据的维度数
    private final int dimension;
    // 传感器的数据缓冲区,可能包含多个维度,所以使用列表保存
    private ArrayList<FloatBuffer> dataBuffers = new ArrayList<>();
    // 最新的传感器数据
    private float[] latestData = null;
    // 缓冲区大小
    private final int bufferSize;
    // 传感器名称
    private final String sensorName;
    // 上次写入文件的时间戳
    private long lastSaveTime = 0;

    BufferManager(final String sensorName, final int dimension, final int bufferSize) {
        this.sensorName = sensorName;
        this.dimension = dimension;
        this.bufferSize = bufferSize;

        init();
    }

    // 初始化 缓冲区 以及 最新数据数组
    private void init() {
        // 时间缓冲区初始化
        timeBuffer = LongBuffer.allocate(bufferSize);
        // 传感器数据缓冲区初始化
        for (int i = 0; i < dimension; i++) {
            dataBuffers.add(FloatBuffer.allocate(bufferSize));
        }
        // 最新传感器数据初始化
        latestData = new float[dimension];
    }

    // 清空缓冲区
    private void clear() {
        timeBuffer.clear();
        for (FloatBuffer dataBuffer : dataBuffers) {
            dataBuffer.clear();
        }
    }

    // 将缓冲区的游标调整到缓冲区开始
    private void rewind() {
        timeBuffer.rewind();
        for (FloatBuffer dataBuffer : dataBuffers) {
            dataBuffer.rewind();
        }
    }

    // 缓冲区是否仍然有空间可用
    // TODO: 这里对外开放有点不妥，主要是为了log正常写入文件，后期需要修改
    public boolean isFull() {
        return !timeBuffer.hasRemaining();
    }

    // 缓冲区是否是空的
    private boolean isEmpty() {
        return timeBuffer.position() == 0;
    }

    // 获取缓冲区使用率
    public float getBufferUsedPercentage() {
        return (float) timeBuffer.position() / timeBuffer.capacity() * 100;
    }

    /**
     * 写入一组传感器数据,注意:
     * - 缓冲区满时将会自动把数据写入到文件中
     * - 返回 false 意味着将数据写入文件中时写入失败,可能是权限不足导致的
     */
    public boolean putData(long timeStamp, float[] values) {
        // 若缓冲区已满,则先将数据写入到文件中
        if (isFull()) {
            if (!saveData()) {
                // 若写入失败，则会丢失一次传感器采集到的数据，已采集到的传感器数据不会丢失
                return false;
            }
        }

        // 将传感器数据写入缓冲区
        timeBuffer.put(Util.sensorEventTimestampToUnixTimestamp(timeStamp));
        for (int i = 0; i < dimension; i++) {
            dataBuffers.get(i).put(values[i]);
            // 更新 最新传感器数据
            latestData[i] = values[i];
        }
        return true;
    }

    // 获取缓冲区中最新的传感器数据
    public float[] getLatestData() {
        return latestData;
    }

    // 获取第一条数据的时间戳
    private long getFirstDataTimestamp() {
        // 备份当前缓冲区的位置
        return timeBuffer.get(0);
    }

    // 获取上次将缓冲区数据写入文件的时间戳
    public long getLastSaveTime() {
        return lastSaveTime;
    }

    /**
     * 将缓冲区中的所有数据转换为字节数组，分为两个步骤
     * 1. 首先将数据转换为字符串：timeStamp,dimension1,dimension2,...
     * 2. 将字符串转换为字节数组
     * <p>
     * 这个方法将不会清空缓冲区，需要手动调用 clear 方法清空缓冲区
     */
    private byte[][] toByteArray() {
        // 首先获取缓冲区当前的使用量,仅保存已用缓冲区中的数据
        final int currentUsage = timeBuffer.position();

        // 将所有缓冲区的指针指向缓冲区开始
        rewind();

        // 结果数组
        byte[][] result = new byte[currentUsage][];

        StringBuilder stringBuilder = null;
        for (int sampleIndex = 0; sampleIndex < currentUsage; sampleIndex++) {
            stringBuilder = new StringBuilder();
            // 将人类可读的时间戳添加到字符串中
            stringBuilder.append(Util.milliSecondDatetime(timeBuffer.get(sampleIndex))).append(",");
            // 将时间戳添加到字符串中
            stringBuilder.append(timeBuffer.get(sampleIndex)).append(",");
            // 将不同维度的传感器数据添加到字符串中
            for (int dimension = 0; dimension < this.dimension - 1; dimension++) {
                // 先定位到维度,然后再定位到某一次的数据
                stringBuilder.append(dataBuffers.get(dimension).get(sampleIndex)).append(",");
            }
            // 最后一个维度特殊化处理
            stringBuilder.append(dataBuffers.get(dimension - 1).get(sampleIndex)).append("\n");
            // 将构造出来的字符串导出为字节数组并添加到结果中
            result[sampleIndex] = stringBuilder.toString().getBytes();
        }

        // 恢复缓冲区指针到读取前的位置
        resetPosition(currentUsage);

        return result;
    }

    // 将缓冲区的指针设置到 position 指定的位置
    private void resetPosition(final int position) {
        timeBuffer.position(position);
        for (FloatBuffer dataBuffer : dataBuffers) {
            dataBuffer.position(position);
        }
    }

    /**
     * 将缓冲区中的现有数据保存到文件中
     */
    public boolean saveData() {
        // 若缓冲区空，则直接返回写入成功
        if (isEmpty()) {
            return true;
        }

        // 获取字节数组形式的数据
        byte[][] data = this.toByteArray();
        // 构造文件名
        String filename = Util.filenameUsableTimeString(Util.milliSecondDatetime(getFirstDataTimestamp())) +
                "-" + data.length + "-" + sensorName + ".log";

        // 将数据保存到文件中
        boolean writeResult = IOLogData.writeData(filename, data, false);
        if (writeResult == true) {
            // 更新写入文件时间
            lastSaveTime = System.currentTimeMillis();
            // 写入成功则将缓冲区清空
            clear();
        } else {
            Log.e(TAG, "保存文件失败!");
        }
        return writeResult;
    }
}
