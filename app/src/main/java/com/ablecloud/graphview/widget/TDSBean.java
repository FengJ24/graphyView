package com.ablecloud.graphview.widget;

/**
 * Created by fengjian on 2017/8/30.
 */

public class TDSBean{
    private String data;
    private int rawTDSInfo;
    private int cleanTDSInfo;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getRawTDSInfo() {
        return rawTDSInfo;
    }

    public void setRawTDSInfo(int rawTDSInfo) {
        this.rawTDSInfo = rawTDSInfo;
    }

    public int getCleanTDSInfo() {
        return cleanTDSInfo;
    }

    public void setCleanTDSInfo(int cleanTDSInfo) {
        this.cleanTDSInfo = cleanTDSInfo;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
