package com.zheolls.zenfacedigit.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class ToastMaker {
    private final static String TAG = "ToastMaker";

    public static void longToast(Activity activity, String message) {
        toast(activity, message, Toast.LENGTH_LONG);
    }

    public static void shortToast(Activity activity, String message) {
        toast(activity, message, Toast.LENGTH_SHORT);
    }

    private static void toast(Activity activity, String message, int length) {
        activity.runOnUiThread(() -> Toast.makeText(activity, message, length).show());
        Log.i(TAG, "<Toast> content: " + message + "; activity: " + activity.getClass().getSimpleName());
    }
}
