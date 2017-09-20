package com.genitus.channel.tracker.util.audio;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AudioData {
    @JSONField(ordinal=1)
    private int type;

    @JSONField(ordinal=2)
    private String sid;

    @JSONField(ordinal=3)
    private String uid;

    @JSONField(ordinal=4)
    private int syncid;

    @JSONField(ordinal=5)
    private long timestamp;

    @JSONField(ordinal=6)
    private int ip;

    @JSONField(ordinal=7)
    private String callname;

    @JSONField(ordinal=8)
    private List<AudioLog> logs = new ArrayList<AudioLog>();

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getSyncid() {
        return syncid;
    }

    public void setSyncid(int syncid) {
        this.syncid = syncid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getIp() {
        return ip;
    }

    public void setIp(int ip) {
        this.ip = ip;
    }

    public String getCallname() {
        return callname;
    }

    public void setCallname(String callname) {
        this.callname = callname;
    }

    public List<AudioLog> getLogs() {
        return logs;
    }

    public void setLogs(List<AudioLog> logs) {
        this.logs = logs;
    }

    @Override
    public String toString() {
        return "\"type\":"+type+","+
                "\"sid\":"+"\""+sid+"\","+
                "\"uid\":"+"\""+uid+"\","+
                "\"syncid\":"+syncid+","+
                "\"timestamp\":"+timestamp+","+
                "\"ip\":"+ip+","+
                "\"callname\":"+"\""+callname+"\","+
                "\"logs\":"+transform()+"";
    }

    public String transform(){
/*        Iterator<AudioLog> iter = logs.iterator();
        String str="";
        while (iter.hasNext()){
            str+=iter.next()+";";
        }
        return str;*/

        String json = JSON.toJSONString(logs);
        return json;
    }
}
