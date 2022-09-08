package com.zheolls.zenfacedigit.network.beans;

import com.zheolls.zenfacedigit.network.StatusCode;

import java.util.ArrayList;

public class UserTypeListBean {
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
        public int utid;
        public String typeName;

        public int getUtid() {
            return utid;
        }

        public void setUtid(int utid) {
            this.utid = utid;
        }

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }
    }
}
