package com.genitus.channel.tracker.util.audio;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.*;

public class AudioInfo {

    @JSONField(ordinal=1)
    private String audio;

    @JSONField(ordinal=2)
    private String recResult;

    @JSONField(ordinal=3)
    private Map<String, String> extras = new HashMap<String, String>();

    @JSONField(ordinal=4)
    private List<AudioData> data = new ArrayList<AudioData>();

    public Map<String, String> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, String> extras) {
        this.extras = extras;
    }

    public String getRecResult() {
        return recResult;
    }

    public void setRecResult(String recResult) {
        this.recResult = recResult;
    }

    public void appendRecs(String recs) {
        if (this.recResult == null) {
            recResult = "";
        }
        recResult += recs;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public List<AudioData> getData() {
        return data;
    }

    public void setData(List<AudioData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{\"sid\":\""+"sid"+"\","+"\"svcLogs\":"+transform()+"}";
    }

    private String transform(){
/*        Iterator<AudioData> iter =data.iterator();
        String str="";
        while (iter.hasNext()){
            str+="["+iter.next()+"]";
        }
        return str;*/
        String json = JSON.toJSONString(data);
        return json;
    }
}
