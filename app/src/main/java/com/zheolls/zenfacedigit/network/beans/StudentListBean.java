package com.zheolls.zenfacedigit.network.beans;

import com.zheolls.zenfacedigit.network.StatusCode;

import java.util.ArrayList;

/**
 * 在获取 学生信息 时，如果可能包含多条信息，
 * 请使用该类作为 DataBack 的 T
 */
public class StudentListBean {
    private int statusCode;
    private String tip;
    private ArrayList<StudentBean.DataBean> data;

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

    public ArrayList<StudentBean.DataBean> getData() {
        return data;
    }

    public void setData(ArrayList<StudentBean.DataBean> data) {
        this.data = data;
    }
}
