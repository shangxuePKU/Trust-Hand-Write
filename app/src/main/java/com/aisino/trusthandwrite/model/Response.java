package com.aisino.trusthandwrite.model;


/**
 * Created by HXQ on 2017/5/16.
 */

public class Response<T> {
    private int status;
    private String message;
    private T datas;
    private int level;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getDatas() {
        return datas;
    }

    public void setDatas(T datas) {
        this.datas = datas;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
