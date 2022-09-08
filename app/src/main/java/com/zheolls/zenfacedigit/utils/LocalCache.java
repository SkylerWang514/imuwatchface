package com.zheolls.zenfacedigit.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.zheolls.zenfacedigit.MyApplication;

import java.util.HashMap;
import java.util.Map;

public class LocalCache {
    private static final String TAG = LocalCache.class.getSimpleName();
    private static final String filename = "local_cache";
    private static final String userInfoKey = "userInfo";
    private static final String userTypeKey = "userType";

    public static enum UserType {
        // 老师
        Teacher,
        // 监护人
        Parent,
        // 学生
        Student,
        // 未存入用户缓存信息
        Unset;

        // CAUTION 这里需要与数据库中 UserType表 的内容保持一致，所以说是硬编码的值，需要注意
        private static final Map<UserType, Integer> userTypeMap = new HashMap<UserType, Integer>() {{
            put(UserType.Parent, 1);
            put(UserType.Teacher, 2);
            put(UserType.Student, 3);
            put(UserType.Unset, -1);
        }};

        /**
         * @return 用户类型对应的数据库中的主键值，与数据库强相关
         */
        public int typeId() {
            return userTypeMap.get(this);
        }
    }

    // 用户缓存信息组织
    public static class UserCache {
        // 用户登录时的用户名
        private String userInfo;
        // 用户类型
        private UserType userType;

        public UserCache(String userInfo, UserType userType) {
            this.userInfo = userInfo;
            this.userType = userType;
        }

        public String getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(String userInfo) {
            this.userInfo = userInfo;
        }

        public UserType getUserType() {
            return userType;
        }

        public void setUserType(UserType userType) {
            this.userType = userType;
        }
    }

    /**
     * 存入用户缓存信息
     */
    public static void putUserCache(UserCache userCache) {
        SharedPreferences.Editor editor = MyApplication.context
                .getSharedPreferences(filename, Context.MODE_PRIVATE)
                .edit();
        editor.putString(userInfoKey, userCache.getUserInfo());
        editor.putString(userTypeKey, userCache.getUserType().name());
        editor.apply();
        Log.e(TAG, "putUserCache: userInfo: " + userCache.getUserInfo() +
                ", userType: " + userCache.getUserType().name());
    }

    /**
     * 获取用户缓存信息
     * 在未存入用户信息时将返回 UserCache("", UserType.Unset)
     */
    public static UserCache getUserCache() {
        SharedPreferences sp = MyApplication.context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        String userInfo = sp.getString(userInfoKey, "");
        UserType userType = UserType.Unset;
        try {
            userType = UserType.valueOf(sp.getString(userTypeKey, ""));
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return new UserCache(userInfo, userType);
    }

    /**
     * 通过检查 key 是否是 用户id 和 用户类型 的 key值，判断key是否合法
     *
     * @return true 合法，false 不合法
     */
    private static boolean isKeyValid(String key) {
        return !key.equals(userInfoKey) && !key.equals(userTypeKey);
    }

    /**
     * 使用键值对的形式写入一组数据
     * 支持的数据类型：String, Integer, Boolean, Float, Long
     *
     * @param key     键
     * @param value   值
     */
    public static boolean putData(String key, Object value) {
        // 若 key 不合法直接返回 false
        if (!isKeyValid(key)) {
            return false;
        }

        SharedPreferences.Editor editor = MyApplication.context
                .getSharedPreferences(filename, Context.MODE_PRIVATE)
                .edit();
        switch (value.getClass().getSimpleName()) {
            case "String":
                editor.putString(key, (String) value);
                break;
            case "Integer":
                editor.putInt(key, (Integer) value);
                break;
            case "Boolean":
                editor.putBoolean(key, (Boolean) value);
                break;
            case "Float":
                editor.putFloat(key, (Float) value);
                break;
            case "Long":
                editor.putLong(key, (Long) value);
                break;
        }
        return editor.commit();
    }

    /**
     * 获取一对数据，将通过 defaultValue 判断数据类型
     * 支持的数据类型：String, Integer, Boolean, Float, Long
     *
     * @param key          键
     * @param defaultValue 默认值
     *                     - 在找不到时将会返回该值
     *                     - 如果是不支持的数据类型将返回 null
     */
    public static Object getData(String key, Object defaultValue) {
        SharedPreferences sp = MyApplication.context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        switch (defaultValue.getClass().getSimpleName()) {
            case "String":
                return sp.getString(key, (String) defaultValue);
            case "Integer":
                return sp.getInt(key, (Integer) defaultValue);
            case "Boolean":
                return sp.getBoolean(key, (Boolean) defaultValue);
            case "Float":
                return sp.getFloat(key, (Float) defaultValue);
            case "Long":
                return sp.getLong(key, (Long) defaultValue);
            default:
                return null;
        }
    }
}
