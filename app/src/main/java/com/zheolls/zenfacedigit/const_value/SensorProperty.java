package com.zheolls.zenfacedigit.const_value;

public class SensorProperty {
    // 加速度传感器
    public static class Accelerometer {
        public static final String name = "accelerometer";
        public static final int dimension = 3;
    }
    public static class Gyroscope {
        public static final String name = "gyroscope";
        public static final int dimension = 3;
    }
    public static class Barometer {
        public static final String name = "barometer";
        public static final int dimension = 1;
    }
    public static class HeartBeat {
        public static final String name = "heartBeat";
        public static final int dimension = 1;
    }
}