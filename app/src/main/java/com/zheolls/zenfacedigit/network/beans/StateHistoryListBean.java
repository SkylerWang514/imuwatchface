package com.zheolls.zenfacedigit.network.beans;

import com.zheolls.zenfacedigit.network.StatusCode;

import java.util.ArrayList;

/**
 * 在获取 状态历史信息 时，默认包含多条信息
 */
public class StateHistoryListBean {
    private int statusCode;
    private String tip;
    private ArrayList<DataBean> data;

    /**
     * 将会返回一个 StatusCode 枚举成员，若不存在则会抛出异常
     */
    public StatusCode getStatusCode() {
        return StatusCode.fromValue(statusCode);
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public ArrayList<DataBean> getData() {
        return data;
    }

    public void setData(ArrayList<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private int shid;
        private int uid;
        private int heartRate;
        private String time;
        private int blood;

        public int getShid() {
            return shid;
        }

        public void setShid(int shid) {
            this.shid = shid;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getHeartRate() {
            return heartRate;
        }

        public void setHeartRate(int heartRate) {
            this.heartRate = heartRate;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getBlood() {
            return blood;
        }

        public void setBlood(int blood) {
            this.blood = blood;
        }
    }
}
