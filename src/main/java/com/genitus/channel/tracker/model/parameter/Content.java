package com.genitus.channel.tracker.model.parameter;

/**
 * Created by Administrator on 2017/8/22.
 */
public class Content {
    private String sid;
    private String uid;
    private String recs;
    private String star;
    private String fromTimestamp;
    private String toTimestamp;

    public Content(){}
    public Content(String sid,String uid,String recs,String star,String cluster,String fromTimestamp,String toTimestamp){
        this.sid=sid;
        this.recs=recs;
        this.uid=uid;
        this.star=star;
        this.fromTimestamp=fromTimestamp;
        this.toTimestamp=toTimestamp;
    }


    public String getRecs() {
        return recs;
    }

    public String getSid() {
        return sid;
    }

    public String getUid() {
        return uid;
    }

    public String getStar() {
        return star;
    }

    public String getFromTimestamp() {
        return fromTimestamp;
    }

    public String getToTimestamp() {
        return toTimestamp;
    }

    public void setFromTimestamp(String fromTimestamp) {
        this.fromTimestamp = fromTimestamp;
    }

    public void setToTimestamp(String toTimestamp) {
        this.toTimestamp = toTimestamp;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public void setRecs(String recs) {
        this.recs = recs;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setStar(String star){
        this.star=star;
    }


    @Override
    public String toString() {
        return "{ star is: "+star+", uid is: "+uid+", sid is: "+sid+", recs is: "+recs+", fromTimestamp is: "+fromTimestamp+", toTimestamp is: "+toTimestamp+" }\n";
    }


}
