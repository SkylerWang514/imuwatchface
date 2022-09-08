package com.zheolls.zenfacedigit;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.zheolls.zenfacedigit.network.ServerInfo;

public class MyApplication extends Application {
    private final String TAG = getClass().getSimpleName();

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "MyApplication create");
        context = getApplicationContext();
        // 尝试通过本地缓存初始化IP
        ServerInfo.init();
    }
}
