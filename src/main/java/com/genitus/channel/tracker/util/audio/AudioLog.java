package com.genitus.channel.tracker.util.audio;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

public class AudioLog{
        private long timestamp;

        private String level;

        private String clientPerfLog;

        private List<String> descs = new ArrayList<String>();

        private Map<String, String> extras = new HashMap<String, String>();

        public String getClientPerfLog(){
            return this.clientPerfLog;
        }

        public void setClientPerfLog(String log) {
            this.clientPerfLog = log;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public Map<String, String> getExtras() {
            return extras;
        }

        public void setExtras(Map<String, String> extras) {
            this.extras = extras;
        }

        public List<String> getDescs() {
            return descs;
        }

        public void setDescs(List<String> descs) {
            this.descs = descs;
        }

    @Override
    public String toString() {
        return "\"timestamp\":"+timestamp+","+
                "\"level\":"+"\""+level+"\","+
                "\"descs\":"+transform1()+","+
                "\"extras\":"+transform2()+"";


    }
    public String transform1(){
        String json = JSON.toJSONString(descs);
        return json;
    }
    public String transform2(){
        String json = JSON.toJSONString(extras);
        return json;
    }

    public static void main(String[] args){
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("123","123");
        map.put("13423","12123");



      //  JSONArray json = JSONArray.fromObject(map);

        LinkedList<String> list = new LinkedList<String>();
        list.add("123132");
        list.add("sdas");
        list.add("weq123");
        String json = JSON.toJSONString(map);
        System.out.println(json);
    }


}
