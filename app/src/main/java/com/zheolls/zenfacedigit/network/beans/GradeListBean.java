package com.zheolls.zenfacedigit.network.beans;

import com.zheolls.zenfacedigit.network.StatusCode;

import java.util.ArrayList;

/**
 * 在获取班级信息时，如果可能包含多条班级信息时，
 * 请使用这个类作为 DataBack 接口的 T
 */
public class GradeListBean {
    private int statusCode;
    private String tip;
    private ArrayList<GradeBean.DataBean> data;

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

    public ArrayList<GradeBean.DataBean> getData() {
        return data;
    }

    public void setData(ArrayList<GradeBean.DataBean> data) {
        this.data = data;
    }
}
