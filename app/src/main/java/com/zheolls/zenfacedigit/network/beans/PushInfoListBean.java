package com.zheolls.zenfacedigit.network.beans;

import com.zheolls.zenfacedigit.network.StatusCode;

import java.util.ArrayList;

public class PushInfoListBean {
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
        public int phid;
        public String pushId;
        public int userId;
        public int utid;

        public int getPhid() {
            return phid;
        }

        public void setPhid(int phid) {
            this.phid = phid;
        }

        public String getPushId() {
            return pushId;
        }

        public void setPushId(String pushId) {
            this.pushId = pushId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getUtid() {
            return utid;
        }

        public void setUtid(int utid) {
            this.utid = utid;
        }
    }
}
