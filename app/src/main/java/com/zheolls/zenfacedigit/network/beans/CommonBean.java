package com.zheolls.zenfacedigit.network.beans;

import com.google.gson.JsonObject;
import com.zheolls.zenfacedigit.network.StatusCode;

public class CommonBean {
    private int statusCode;
    private String tip;
    private JsonObject data;

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

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }
}
