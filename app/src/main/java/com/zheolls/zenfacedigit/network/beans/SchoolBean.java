package com.zheolls.zenfacedigit.network.beans;

import com.zheolls.zenfacedigit.network.StatusCode;

public class SchoolBean {
    private int statusCode;
    private String tip;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private int sid;
        private String sname;
        private String address;

        public DataBean() {}

        public DataBean(int sid, String sname, String address) {
            this.sid = sid;
            this.sname = sname;
            this.address = address;
        }

        public int getSid() {
            return sid;
        }

        public void setSid(int sid) {
            this.sid = sid;
        }

        public String getSname() {
            return sname;
        }

        public void setSname(String sname) {
            this.sname = sname;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }
}
