package com.genitus.channel.tracker.model.esResult;

/**
 * Created by Administrator on 2017/8/22.
 */
public class Hit {
    private String ret;
    private String sub;
    private String _sid;
    private String gender;
    private String rse;
    private String net_type;
    private String net_subtype;
    private String ent;
    private String vad_enable;
    private String cver;
    private String sid;
    private String aue;
    private String uid;
    private String rst;
    private String timestamp;  //前端在显示的时候加上@
    private String sis_ip;
    private String recs;
    private String appid;
    private String imei;
    private String android_id;
    private String age;
    private String eng_ip;

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getAppid() {
        return appid;
    }

    public String getCver() {
        return cver;
    }

    public String getEnt() {
        return ent;
    }

    public String getImei() {
        return imei;
    }

    public String getNet_type() {
        return net_type;
    }

    public String getRet() {
        return ret;
    }

    public String getRse() {
        return rse;
    }

    public String getRst() {
        return rst;
    }

    public String getSis_ip() {
        return sis_ip;
    }

    public String getSub() {
        return sub;
    }

    public String getVad_enable() {
        return vad_enable;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setCver(String cver) {
        this.cver = cver;
    }

    public void setEnt(String ent) {
        this.ent = ent;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public void setNet_type(String net_type) {
        this.net_type = net_type;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public void setRse(String rse) {
        this.rse = rse;
    }

    public void setRst(String rst) {
        this.rst = rst;
    }

    public void setSis_ip(String sis_ip) {
        this.sis_ip = sis_ip;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public void setVad_enable(String vad_enable) {
        this.vad_enable = vad_enable;
    }


    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void set_sid(String _sid) {
        this._sid = _sid;
    }

    public String get_sid() {
        return _sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSid() {
        return sid;
    }

    public void setRecs(String recs) {
        this.recs = recs;
    }

    public String getRecs() {
        return recs;
    }

    public void setNet_subtype(String net_subtype) {
        this.net_subtype = net_subtype;
    }

    public String getNet_subtype() {
        return net_subtype;
    }

    public void setEng_ip(String eng_ip) {
        this.eng_ip = eng_ip;
    }

    public String getEng_ip() {
        return eng_ip;
    }

    public void setAue(String aue) {
        this.aue = aue;
    }

    public String getAue() {
        return aue;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }

    public String getAndroid_id() {
        return android_id;
    }
}
