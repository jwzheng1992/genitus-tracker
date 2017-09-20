package com.genitus.channel.tracker.util;

import com.genitus.channel.tracker.model.esResult.Hit;
import com.genitus.channel.tracker.model.esResult.Result;
import org.elasticsearch.search.SearchHit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {
    public static void printlnMap(HashMap<String,Object> map){
        Iterator<Map.Entry<String, Object>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> entry = iterator.next();
            System.out.println(entry.getKey()+":"+entry.getValue());
        }
    }

    public static Result saveAsJsonObject(SearchHit[] hits){
        Result result = new Result();
        result.setStatus("status_successful");
        result.setDesc("desc_successful");
        result.setCount(hits.length);
        LinkedList<Hit> linkedList=new LinkedList<Hit>();
        for (int i=0;i<hits.length;i++){
            Hit hit = copySearchHit(hits[i]);
            linkedList.add(hit);
        }
        result.setList(linkedList);
        return result;



    }

    public static Hit copySearchHit(SearchHit searchHit){
        Hit hit = new Hit();
        //获取searchHit中每个hit的搜索结果
        Map<String,Object> map = searchHit.getSource();
        String ret = (String)map.get("ret");
        String sub = (String)map.get("sub");
        String gender = (String)map.get("gender");
        String rse = (String)map.get("rse");
        String net_type = (String)map.get("net_type");
        String net_subtype = (String)map.get("net_subtype");
        String ent = (String)map.get("ent");
        String vad_enable = (String)map.get("vad_enable");
        String cver = (String)map.get("cver");
        String sid = (String)map.get("sid");
        String aue = (String)map.get("aue");
        String uid = (String)map.get("uid");
        String rst = (String)map.get("rst");
        String timestamp = (String)map.get("@timestamp");
        //     System.out.println("@timestamp:"+timestamp);
        String sis_ip = (String)map.get("sis_ip");
        String recs = (String)map.get("recs");
        String appid = (String)map.get("appid");
        String imei = (String)map.get("imei");
        String android_id = (String)map.get("android_id");
        String age = (String)map.get("age");
        String eng_ip = (String)map.get("eng_ip");


        hit.setRet(ret);hit.setSub(sub);hit.setGender(gender); hit.setRse(rse);hit.setNet_type(net_type);
        hit.setNet_subtype(net_subtype);hit.setEnt(ent);hit.setVad_enable(vad_enable);hit.setCver(cver);hit.setSid(sid);
        hit.setAue(aue);hit.setUid(uid);hit.setRst(rst);hit.setTimestamp(timestamp);hit.setSis_ip(sis_ip);
        hit.setRecs(recs);hit.setAppid(appid);hit.setImei(imei);hit.setAndroid_id(android_id);hit.setAge(age);
        hit.setEng_ip(eng_ip);

        return hit;
    }

    public static String makeTimeStampEarlier(String timestamp,SimpleDateFormat sdf){
        Date date=null;
        try {
            date = sdf.parse(timestamp);
        }catch (ParseException e){
            System.out.println("*********************** ParseException *****************************");
        }

        long filterTimeStamp = date.getTime()-20000;

        date = new Date(filterTimeStamp);

        return sdf.format(date);
    }


    public static long changToLongType(String timestamp, SimpleDateFormat sdf){
        Date date = null;
        try {
            date=sdf.parse(timestamp);
        }catch (ParseException e){
            System.out.println("ParseException");
        }
        //      System.out.println(date.getTime());
        return date.getTime();

    }
}
