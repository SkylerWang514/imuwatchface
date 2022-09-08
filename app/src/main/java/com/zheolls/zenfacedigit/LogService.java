package com.zheolls.zenfacedigit;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Process;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.zheolls.zenfacedigit.const_value.SensorProperty;
import com.zheolls.zenfacedigit.sensor_listener.SensorDataListener;


public class LogService extends Service implements SensorEventListener {
    public static final String TAG = "LogService";

    private MyBinder mBinder = new MyBinder();

    public static int samplingRate = 50; //60Hz
    public static int heartRateSamplingInterval = 30 * 1000; //10Hz？
    public static int bufferSize = samplingRate * 60 * 10; //10min

    private static SensorManager sManager;
    private static Sensor mSensorAccelerometer;
    private static Sensor mSensorGyroscope;
    private static Sensor mSensorBarometer;
    private static Sensor mSensorHrtmeter;

    public static boolean logState = false;
    Sensor mOffBody = null;

    // 传感器数据侦听，分别是：加速度、陀螺仪、气压、心跳
    public static SensorDataListener accelerometerListener = null;
    public static SensorDataListener gyroscopeListener = null;
    public static SensorDataListener barometerListener = null;
    public static SensorDataListener heartBeatListener = null;

    public MyWatchFace myWatchFace = null;

    private static HandlerThread mSensorThread;
    private static Handler mSensorHandler;

    public static final String CHANNEL_IMU = "CHANNEL_IMU";
    private static final int notificationId = 001;
    private static final String NOTIFICATION_CHANNEL_ID = "001";
    private NotificationCompat.Builder mBuilder;
    NotificationManager notificationManager = null;

    PowerManager.WakeLock wakeLock = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // Notification channel ID is ignored for Android 7.1.1
        // (API level 25) and lower.

        // Get an instance of the NotificationManager service
        notificationManager = getSystemService(NotificationManager.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, CHANNEL_IMU, importance);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setShowBadge(false);

            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mOffBody = sManager.getDefaultSensor(34, true); // OffBody sensor, hidden API
        if (mOffBody == null) {
            Log.e(TAG, "No OffBody sensor");
            startSensors();
        } else {
            sManager.registerListener(this, mOffBody, SensorManager.SENSOR_DELAY_NORMAL);
        }

        NotificationCompat.Builder initiate = setNotification("Sensor Log", "Service Start");
        // Issue the notification with notification manager.

        startForeground(notificationId, initiate.build());

        Log.d(TAG, "onCreate() executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSenors();
        Log.d(TAG, "onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private NotificationCompat.Builder setNotification(String title, String text) {
        // Build intent for notification content
        Intent viewIntent = new Intent(this, MyWatchFace.class);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        mBuilder
                .setSmallIcon(R.drawable.smallic)
                .setContentTitle(title)
                .setContentText(text)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentIntent(viewPendingIntent);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setOnlyAlertOnce(true);
        }

        return mBuilder;
    }

    /**
     * This is the method that can be called to update the Notification
     */
    public void updateNotification(String title, String textUpdate) {
        mBuilder = setNotification(title, textUpdate);
        notificationManager.notify(notificationId, mBuilder.build());
    }


    class MyBinder extends Binder {

        public Service getService() {
            return LogService.this;
        }

        public void setMyWatchFace(MyWatchFace mywatchFace) {
            LogService.this.myWatchFace = mywatchFace;
        }

    }

    @SuppressLint("InvalidWakeLockTag")
    public void startSensors() {
        Log.v(TAG, "Sensor start");
        logState = true;
        PowerManager powerMgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerMgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        if (!wakeLock.isHeld()) {
            Log.v(TAG, "acquiring wakeLock");
            wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);
        }

        mSensorThread = new HandlerThread("Sensor thread", Process.THREAD_PRIORITY_BACKGROUND);
        mSensorThread.start();
        mSensorHandler = new Handler(mSensorThread.getLooper()); //Blocks until looper is prepared, which is fairly quick

        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorAccelerometer = sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorGyroscope = sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorBarometer = sManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mSensorHrtmeter = sManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);


        // Sometimes the IMUListener may be registered twice
        if (gyroscopeListener == null)
            gyroscopeListener = new SensorDataListener(this, wakeLock,
                    SensorProperty.Gyroscope.name, bufferSize,
                    SensorProperty.Gyroscope.dimension);
        else {
            sManager.unregisterListener(gyroscopeListener, mSensorGyroscope);
            gyroscopeListener = new SensorDataListener(this, wakeLock,
                    SensorProperty.Gyroscope.name, bufferSize,
                    SensorProperty.Gyroscope.dimension);
        }

        if (accelerometerListener == null)
            accelerometerListener = new SensorDataListener(this, wakeLock,
                    SensorProperty.Accelerometer.name, bufferSize,
                    SensorProperty.Accelerometer.dimension);
        else {
            sManager.unregisterListener(accelerometerListener, mSensorAccelerometer);
            accelerometerListener = new SensorDataListener(this, wakeLock,
                    SensorProperty.Accelerometer.name, bufferSize,
                    SensorProperty.Accelerometer.dimension);
        }
        if (barometerListener == null)
            barometerListener = new SensorDataListener(this, wakeLock,
                    SensorProperty.Barometer.name, bufferSize,
                    SensorProperty.Barometer.dimension);
        else {
            sManager.unregisterListener(barometerListener, mSensorBarometer);
            barometerListener = new SensorDataListener(this, wakeLock,
                    SensorProperty.Barometer.name, bufferSize,
                    SensorProperty.Barometer.dimension);
        }
        if (heartBeatListener == null)
            heartBeatListener = new SensorDataListener(this, wakeLock,
                    SensorProperty.HeartBeat.name, bufferSize,
                    SensorProperty.HeartBeat.dimension);
        else {
            sManager.unregisterListener(heartBeatListener, mSensorHrtmeter);
            heartBeatListener = new SensorDataListener(this, wakeLock,
                    SensorProperty.HeartBeat.name, bufferSize,
                    SensorProperty.HeartBeat.dimension);
        }


//        // Make sure reset the time reference when start sensors
//        IMUSensorListener.startEventTime = -1l;

        Log.v(TAG, "wakeup: " + mSensorAccelerometer.isWakeUpSensor()); //false
        Log.v(TAG, "Max delay: " + mSensorAccelerometer.getMaxDelay());  //1000000us
        Log.v(TAG, "Min delay: " + mSensorAccelerometer.getMinDelay()); //5000us
        Log.v(TAG, "Power: " + mSensorAccelerometer.getPower());     //0.13mA
        Log.v(TAG, "FIFO: " + mSensorAccelerometer.getFifoMaxEventCount()); //447
        Log.v(TAG, "Reserved FIFO: " + mSensorAccelerometer.getFifoReservedEventCount()); //0


        sManager.registerListener(gyroscopeListener, mSensorGyroscope, 1000000 / samplingRate,
                10000000, mSensorHandler);
        sManager.registerListener(accelerometerListener, mSensorAccelerometer, 1000000 / samplingRate,
                10000000, mSensorHandler); //max delay: 20ms * 100 sample * 1000us/ms
        sManager.registerListener(barometerListener, mSensorBarometer, 1000000 / samplingRate,
                10000000, mSensorHandler);
        sManager.registerListener(heartBeatListener, mSensorHrtmeter, heartRateSamplingInterval,
                10000000, mSensorHandler);
        logState = true;
    }

    public void stopSenors() {
        Log.d(TAG, "Sensor stopped");
        logState = false;
        sManager.unregisterListener(gyroscopeListener, mSensorGyroscope);
        sManager.unregisterListener(accelerometerListener, mSensorAccelerometer);
        sManager.unregisterListener(barometerListener, mSensorBarometer);
        sManager.unregisterListener(heartBeatListener, mSensorHrtmeter);
        mSensorThread.quitSafely();
        gyroscopeListener.saveData();
        accelerometerListener.saveData();
        barometerListener.saveData();
        heartBeatListener.saveData();
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
        gyroscopeListener = null;
        accelerometerListener = null;
        barometerListener = null;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "OFFBody sensor:" + event.values[0]);

        if (event.values[0] < 1) {
            if (logState == true)
                stopSenors();
        }
        if (event.values[0] > 0) {
            if (logState == false)
                startSensors();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
