package com.genitus.channel.tracker.model.esResult;

import java.util.LinkedList;

/**
 * Created by Administrator on 2017/8/22.
 */
public class Result {
    private String status;
    private String desc;
    private int count;
    private String timestamp;
    private LinkedList<Hit> sidlist = new LinkedList<Hit>();

    public void setStatus(String status){
        this.status=status;
    }

    public String getStatus() {
        return status;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public LinkedList<Hit> getList() {
        return sidlist;
    }

    public void setList(LinkedList<Hit> sidlist) {
        this.sidlist = sidlist;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
