package com.genitus.channel.tracker.model.parameter;

import java.util.HashMap;

/**
 * Created by Administrator on 2017/8/22.
 */
public class Parameter {
    private boolean search;
    private String cluster;
    private int startIndex;
    private int size;
    private String timestamp;
    private Content content;
    private HashMap<String,Object> query = new HashMap<String,Object>();
    private HashMap<String,Object> totalInfo=new HashMap<String,Object>();

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getCluster() {
        return cluster;
    }

    public void setSearch(boolean search){
        this.search=search;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setSize(int size) {
        this.size = size;
    }


    public void setTimeStamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setContent(Content content) {
        this.content = content;
        setQuery();
        setTotalInfo();
    }

    //在parse Json的时候不会自动掉用setQuery方法，因为json对象中没有query字段！！
    // 所以 要使得setQuery方法可以自动掉用，则将它放入setContent方法中。因为setContent方法是自动调用的
    public void setQuery(){
        initializeQuery();
    }

    public void setTotalInfo() {
        initializeTotalInfo();
    }
    public int getStartIndex() {
        return startIndex;
    }

    public int getSize() {
        return size;
    }


    public String getTimeStamp() {
        return timestamp;
    }

    public Content getContent() {
        return content;
    }

    public HashMap<String,Object> getQuery(){
        return query;
    }

    public boolean getSearch(){
        return search;
    }

    public HashMap<String,Object> getTotalInfo(){
        return totalInfo;
    }

    public void initializeQuery(){
        query.put("star",content.getStar());
        query.put("sid",content.getSid());
        query.put("uid",content.getUid());
        query.put("recs",content.getRecs());
        query.put("fromTimestamp",content.getFromTimestamp());
        query.put("toTimestamp",content.getToTimestamp());
    }
    public void initializeTotalInfo(){
        totalInfo.put("search",search);
        totalInfo.put("cluster",cluster);
        totalInfo.put("startIndex",startIndex);
        totalInfo.put("size",size);
        totalInfo.put("timestamp",timestamp);
        totalInfo.put("star",content.getStar());
        totalInfo.put("sid",content.getSid());
        totalInfo.put("uid",content.getUid());
        totalInfo.put("fromTimestamp",content.getFromTimestamp());
        totalInfo.put("toTimestamp",content.getToTimestamp());
        totalInfo.put("recs",content.getRecs());
    }




    @Override
    public String toString() {
        return "search : "+search+"\n"+
                "cluster : "+cluster+"\n"+
                "startIndex : "+startIndex+"\n"+
                "size : "+size+"\n"+
                "timestamp : "+timestamp+"\n"+
                "content : "+content+"\n";
    }
}
