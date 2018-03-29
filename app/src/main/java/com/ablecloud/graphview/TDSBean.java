package com.ablecloud.graphview;

/**
 * Created by fengjian on 2017/8/30.
 */

public class TDSBean {
    private String date;
    private int rawTDSInfo;
    private int cleanTDSInfo;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
