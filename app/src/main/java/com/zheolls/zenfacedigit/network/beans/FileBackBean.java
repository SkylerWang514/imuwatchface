package com.zheolls.zenfacedigit.network.beans;

import com.zheolls.zenfacedigit.network.StatusCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FileBackBean {
    private int statusCode;
    private String tip;
    private Map<String, Integer> data;

    /**
     * 反转发回的数据，使用状态码作键，文件的相对路径作值
     * 使用状态码枚举常量作键，将具有相同状态码的文件聚合到一个列表中
     */
    public Map<StatusCode, ArrayList<String>> getReverseData() {
        Map<StatusCode, ArrayList<String>> reverseData = new HashMap<>();

        // 反转后的key，即状态码枚举常量
        StatusCode reverseKey;
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            reverseKey = StatusCode.fromValue(entry.getValue());
            if (!reverseData.containsKey(reverseKey)) {
                reverseData.put(reverseKey, new ArrayList<>());
            }
            Objects.requireNonNull(reverseData.get(reverseKey)).add(entry.getKey());
        }

        return reverseData;
    }

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

    public Map<String, Integer> getData() {
        return data;
    }

    public void setData(Map<String, Integer> data) {
        this.data = data;
    }
}
