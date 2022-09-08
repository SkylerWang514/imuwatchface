package com.zheolls.zenfacedigit.network.beans;

import com.zheolls.zenfacedigit.network.StatusCode;

public class GradeBean {
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
        private int gid;
        private int tid;
        private String grade;
        private String classNum;

        public DataBean() {}

        public DataBean(int gid, int tid, String grade, String classNum) {
            this.gid = gid;
            this.tid = tid;
            this.grade = grade;
            this.classNum = classNum;
        }

        public int getGid() {
            return gid;
        }

        public void setGid(int gid) {
            this.gid = gid;
        }

        public int getTid() {
            return tid;
        }

        public void setTid(int tid) {
            this.tid = tid;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getClassNum() {
            return classNum;
        }

        public void setClassNum(String classNum) {
            this.classNum = classNum;
        }

        public String getGradeString() {
            return grade + classNum;
        }
    }
}
