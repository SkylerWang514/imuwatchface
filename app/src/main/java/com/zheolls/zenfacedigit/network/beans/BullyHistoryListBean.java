package com.zheolls.zenfacedigit.network.beans;


import com.zheolls.zenfacedigit.network.StatusCode;

import java.util.ArrayList;

/**
 * 在获取 被霸凌历史信息 时，默认包含多条信息
 */
public class BullyHistoryListBean {
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
        private int bhid;
        private int uid;
        private String address;
        private String time;
        private int suspected;
        private boolean handled;
        private boolean tag;
        private String picture;
        private String remark;

        public int getBhid() {
            return bhid;
        }

        public void setBhid(int bhid) {
            this.bhid = bhid;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getSuspected() {
            return suspected;
        }

        public void setSuspected(int suspected) {
            this.suspected = suspected;
        }

        public boolean isHandled() {
            return handled;
        }

        public void setHandled(boolean handled) {
            this.handled = handled;
        }

        public boolean isTag() {
            return tag;
        }

        public void setTag(boolean tag) {
            this.tag = tag;
        }

        public String getPicture() {
            return picture;
        }

        public void setPicture(String picture) {
            this.picture = picture;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
