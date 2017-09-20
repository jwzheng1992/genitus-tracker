package com.genitus.channel.tracker.model.hbaseResult;

import com.alibaba.fastjson.JSON;

import java.util.*;

public class HBaseResult {
    private String sid;
    private Map<String,String> extrasInfo;
    private String[] svcLogs;
    public HBaseResult(){}
    public HBaseResult(String sid,String[] svcLogs){
        this.sid=sid;
        this.svcLogs=svcLogs;
    }

    public Map<String,String> getExtrasInfo() {
        return extrasInfo;
    }

    public void setExtrasInfo(Map<String,String> extrasInfo) {
        this.extrasInfo = extrasInfo;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String[] getSvcLogs() {
        return svcLogs;
    }

    public void setSvcLogs(String[] svcLogs) {
        this.svcLogs = svcLogs;
    }

    public String svcLogsToString(){
        if (svcLogs.length==0)
            return "";
        String str = "\"svcLogs\": [";
        for(int i=0;i<svcLogs.length-1;i++){
            str+=svcLogs[i]+",";
        }
        str+=svcLogs[svcLogs.length-1]+"]";
        return str;
    }

    public String extrasInfoToString(){
        return JSON.toJSONString(extrasInfo);
    }

    @Override
    public String toString() {
        return "{\"sid\":"+"\""+sid+"\","+"\"extrasInfo\":"+extrasInfoToString()+","+
                svcLogsToString()+"}";
    }
    private static final List<String> extras = Arrays.asList(("sub,eng_host,s_city,timestamp,appid,aue,auf,caller_name,client_ip,cver,domain,ent,imei,imsi,msc_mac,sn,country,sub_ntt,prs,ptt,rse,rst,sch,scn,sent,uid,age,gender,ret,province,city,operator,caller_appid,openudid,sub_ntt").split(","));

    public static void main(String[] args) {
        String sid = "iat7593a588@nc15e4fe2fdae0015270";
        String[] svcLogs = {
                "{\"type\": 1000, \"sid\": \"iat7593a588@nc15e4fe2fdae0015270\", \"uid\": \"\", \"syncid\": 18, \"timestamp\": 1504578830403, \"ip\": -1407516651, \"callName\": \"auw\", \"logs\": [{\"timestamp\": 1504578830403, \"level\": \"DEBUG\", \"extras\": {}, \"descs\": [\"st=1504578830402,func=handler,cost=1\", \"st=1504578830402,func=getClient,cost=0\", \"st=1504578830402,func=RecAudioWrite,cost=1\", \"st=1504578830402,func=audioWrite,cost=1\", \"st=1504578830402,func=audioWrite,cost=1\", \"st=1504578830403,func=sendColorLog,cost=0\", \"st=1504578830403,func=onResponseMessage,cost=0\"]}], \"mediaData\": {\"type\": 0, \"data\": {\"bytes\": \"\"}}}",
                "{\"type\": 1000, \"sid\": \"iat7593a588@nc15e4fe2fdae0015270\", \"uid\": \"\", \"syncid\": 35, \"timestamp\": 1504578831083, \"ip\": -1407516651, \"callName\": \"auw\", \"logs\": [{\"timestamp\": 1504578831083, \"level\": \"DEBUG\", \"extras\": {}, \"descs\": [\"st=1504578831081,func=handler,cost=2\", \"st=1504578831082,func=getClient,cost=0\", \"st=1504578831082,func=RecAudioWrite,cost=1\", \"st=1504578831082,func=audioWrite,cost=1\", \"st=1504578831082,func=audioWrite,cost=1\", \"st=1504578831083,func=sendColorLog,cost=0\", \"st=1504578831083,func=onResponseMessage,cost=0\"]}], \"mediaData\": {\"type\": 0, \"data\": {\"bytes\": \"\"}}}"
        };
        HBaseResult hBaseResult = new HBaseResult();
        hBaseResult.setSid(sid);
        hBaseResult.setSvcLogs(svcLogs);
        System.out.println(hBaseResult);
    }
}
