package com.genitus.channel.tracker.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.genitus.channel.tracker.client.HBaseClient;
import com.genitus.channel.tracker.model.hbaseResult.HBaseResult;
import com.genitus.channel.tracker.util.audio.RemoteService;
import com.google.common.util.concurrent.AbstractIdleService;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.genitus.forceps.hbase.LogData;
import org.genitus.lancet.util.codec.Codec;
import org.genitus.lancet.util.codec.CodecFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.*;

public class HBaseService extends AbstractIdleService {
    private HBaseClient hBaseClient;
    private static Logger logger = LoggerFactory.getLogger(HBaseService.class);
    private Schema schema;
    public HBaseService(HBaseClient hBaseClient,Schema schema){
        this.hBaseClient=hBaseClient;
        this.schema=schema;
    }
    private static final List<String> extras = Arrays.asList(("sub,eng_host,s_city,timestamp,appid,aue,auf,caller_name,client_ip,cver,domain,ent,imei,imsi,msc_mac,sn,country,sub_ntt,prs,ptt,rse,rst,sch,scn,sent,uid,age,gender,ret,province,city,operator,caller_appid,openudid,sub_ntt,net_subtype,net_type").split(","));

    protected void startUp() throws Exception {

    }

    protected void shutDown() throws Exception {

    }




    /**
     * parse sid to get city(nc or sc)
     * @param sid
     * @return
     */
    private String getCity(String sid){
        return sid.substring(12,14);
    }


    /**HBaseService class对外提供了一个方法 就是当前方法 HashMap<String,String> getLog(String sid, String key)
     * 返回的HashMap<String,String>是：
     * "data" ,String;
     * "media",mediaSavePath
     * 另外，还主要提供了三个private方法，供内部调用。
     *
     * String formatLogData(String sid,List<LogData> dataList)：
     * String saveMedia(ByteBuffer byteBuffer ,String aue,String auf,String sid)
     * String decodeByteBuffer(ByteBuffer byteBuffer,String key )
     * @param sid
     * @return
     * @throws Exception
     */

    public HashMap<String,String> getLog(String sid ) throws Exception{
        HashMap<String,List<LogData>>  map =  hBaseClient.getLog(sid,"");
        List<LogData> dataList = map.get("data");
        if (dataList.isEmpty()){
            return null;
        }
        List<LogData> mediaList =  map.get("media");
        List<LogData> aueAufList = map.get("aueAuf");
        if (!aueAufList.isEmpty()){
            String aueAuf = aueAufList.get(0).rowkey;
            String[] elem = aueAuf.split("&&");
            String aue = elem[0];
            String auf = elem[1];
            logger.info("In HBaseService getLog method, aue is: "+aue+", auf is: "+auf);
            String extrasInfo1 = saveMedia(mediaList.get(0).data,aue,auf,sid);
        }


        HashMap<String,String> map1 =  new HashMap<String,String>();
        String data = formatLogData(sid,dataList);
        map1.put("data",data);
        map1.put("path","");
        return map1;
    }


    /**
     * 将List<LogData> dataList格式化为String类型
     * 同时 遍历LogData找到type=0&&callName=ssb的params，将params存储为extrasInfo字符串
     * 合并两个字符串 一起返回String
     * @param sid
     * @param dataList
     * @return
     * @throws Exception
     */
    private String formatLogData(String sid,List<LogData> dataList)throws Exception {
        LinkedList<String> listData = new LinkedList<String>();
       for (LogData logData:dataList){
            String data = decodeByteBuffer(logData.data,"").toString();
            listData.add(data);
        }
        Map<String, String> extrasMap = new HashMap<String, String>();
        for (LogData logData : dataList) {
            GenericRecord genericRecord = decodeByteBuffer(logData.data, "");
            //在type=0并且callName=ssb中找到extras信息 添加到extrasMap中
            if ((Integer) genericRecord.get("type") == 0 && (genericRecord.get("callName")).toString().equals("ssb")) {
                JSONArray jsonArray = JSONArray.parseArray(genericRecord.get("logs").toString());
                Map map1 = JSON.parseObject(jsonArray.get(0).toString(), Map.class);
                Map map2 = JSON.parseObject(map1.get("extras").toString(), Map.class);
                //添加timestamp字段
                if (map2.containsKey("timestamp"))
                    extrasMap.put("timestamp",map2.get("timestamp").toString());
                //添加client_ip信息
                if (map2.containsKey("client_ip"))
                    extrasMap.put("client_ip",map2.get("client_ip").toString());
                //添加client_ip信息
                if (map2.containsKey("ret"))
                    extrasMap.put("ret",map2.get("ret").toString());
                //添加client_ip信息
                if (map2.containsKey("sub"))
                    extrasMap.put("sub",map2.get("sub").toString());

                String params = map2.get("params").toString();
                System.out.println("formatLogData method: "+params);
                String[] elem = params.split(",");
                for (int k = 0; k < elem.length; k++) {
                    int pos = elem[k].indexOf("=");
                    String key = elem[k].substring(0, pos);
                    String value = elem[k].substring(pos + 1, elem[k].length());
                    if (extras.contains(key))
                        extrasMap.put(key, value);
                }

            }
            if ((Integer) genericRecord.get("type") == 1 ) {
                //在type=1的日志中 ，找到finalResults并解析，得到recs并添加该信息到extrasMap中
                JSONArray jsonArray = JSONArray.parseArray(genericRecord.get("logs").toString());
                for (int i=0;i<jsonArray.size();i++){
                    Map map1 = JSON.parseObject(jsonArray.get(i).toString(), Map.class);
                    Map map2 = JSON.parseObject(map1.get("extras").toString(), Map.class);
                    if (map2.containsKey("finalResult")){
                        String finalResult = map2.get("finalResult").toString();
                        System.out.println("finalResult is: "+ finalResult);
                        String words = getRecs(finalResult);
                        extrasMap.put("recs",words);
                    }
                }

                //在type=1的log中找extras信息。
                Map map1 = JSON.parseObject(jsonArray.get(0).toString(), Map.class);
                Map map2 = JSON.parseObject(map1.get("extras").toString(), Map.class);
                String params = map2.get("params").toString();
                System.out.println("formatLogData method: "+params);
                String[] elem = params.split(",");
                for (int k = 0; k < elem.length; k++) {
                    int pos = elem[k].indexOf("=");
                    String key = elem[k].substring(0, pos);
                    String value = elem[k].substring(pos + 1, elem[k].length());
                    if (extras.contains(key))
                        extrasMap.put(key, value);
                }
            }
        }

        HBaseResult hBaseResult = new HBaseResult();
        hBaseResult.setSid(sid);
        String[] svcLogs = new String[listData.size()];
        int i = 0;
        for (String data : listData) {
            svcLogs[i] = data;
            i++;
        }
        hBaseResult.setSvcLogs(svcLogs);
        hBaseResult.setExtrasInfo(extrasMap);
        //   System.out.println("************************hBaseResult.toString():*********************************");
        //    System.out.println(hBaseResult.toString());


        return hBaseResult.toString();
    }



    private String getRecs(String finalResult){
        System.out.println(finalResult);
        String str1 = finalResult.replace("\\\"","\"");
        System.out.println(str1);
        String words="";
        JSONArray jsonArray = JSONArray.parseArray(str1);
        for (int i=0;i<jsonArray.size();i++){
            System.out.println(jsonArray.get(i));
            Map map1 = JSON.parseObject(jsonArray.get(i).toString(), Map.class);
            JSONArray jsonArray1 = JSONArray.parseArray(map1.get("ws").toString());
            for (int j=0;j<jsonArray1.size();j++){
                Map map2 = JSON.parseObject(jsonArray1.get(j).toString(), Map.class);

                JSONArray jsonArray2 = JSONArray.parseArray(map2.get("cw").toString());
                Map map3 = JSON.parseObject( jsonArray2.get(0).toString(), Map.class);
                System.out.println(map3.get("w"));
                words+=map3.get("w");
                System.out.println(words);

            }

        }

        System.out.println("words is: "+words);
        return words;
    }

    /**
     * 在HBaseClient的getLog方法中，已经将media提取出来作为一个List<LogData>返回
     * 此处的saveMedia方法，首先解压缩bytebuffer，然后反序列化，最后转存为WAV格式并返回一个存储路径
     * @param byteBuffer
     * @param aue
     * @param auf
     * @param sid
     * @return
     * @throws Exception
     */
    private String saveMedia(ByteBuffer byteBuffer ,String aue,String auf,String sid) throws Exception{
        Codec codec = CodecFactory.getCodec(CodecFactory.DeflateType);
        GenericDatumReader<GenericRecord> genericRecordGenericDatumReader = new GenericDatumReader<GenericRecord>(schema);
        String extrasInfo="";
        byte[]  data = codec.decompress(byteBuffer.array());
        if (data != null) {
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data,null); //DecoderFactory.get.binaryDecoder(data, null);
            GenericRecord genericRecord = genericRecordGenericDatumReader.read(null,decoder);
            if ((Integer)genericRecord.get("type")==1){
                /*Object obj= genericRecord.get("mediaData");
                genericRecord = (GenericRecord)obj;
                Object obj1 = genericRecord.get("data");
                ByteBuffer buffer = (ByteBuffer)obj1;
                new RemoteService().savaAudioFile(buffer.array(),aue, auf, sid);*/

                Object obj= genericRecord.get("mediaData");
                GenericRecord genericRecord1 = (GenericRecord)obj;
                Object obj1 = genericRecord1.get("data");
                ByteBuffer buffer = (ByteBuffer)obj1;
                new RemoteService().savaAudioFile(buffer.array(),aue, auf, sid);

                JSONArray jsonArray = JSONArray.parseArray(genericRecord.get("logs").toString());
                Map map1 = JSON.parseObject(jsonArray.get(0).toString(), Map.class);
                Map map2 = JSON.parseObject(map1.get("extras").toString(), Map.class);
                String params = map2.get("params").toString();
                System.out.println("saveMedia method:");
                System.out.println(params);
                String[] elem = params.split(",");
                Map<String, String> extrasMap = new HashMap<String, String>();
                for (int k = 0; k < elem.length; k++) {
                    System.out.println("elem[k] is: " + elem[k]);
                    int pos = elem[k].indexOf("=");
                    String key = elem[k].substring(0, pos);
                    String value = elem[k].substring(pos + 1, elem[k].length());
                    if (extras.contains(key))
                        extrasMap.put(key, value);
                }

                Iterator<Map.Entry<String, String>> iter = extrasMap.entrySet().iterator();
                while (iter.hasNext())
                    extrasInfo += iter.next() + ",";
                System.out.println("extrasInfo is: " + extrasInfo);

            }

            return extrasInfo;
        } else {
            throw new Exception("In saveMedia method: byte[] data is null");
        }
    }


    private GenericRecord decodeByteBuffer(ByteBuffer byteBuffer,String key ) throws Exception{
        Codec codec = CodecFactory.getCodec(CodecFactory.DeflateType);
   //     Schema schema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"SvcLog\",\"namespace\":\"org.genitus.karyo.model.log\",\"fields\":[{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"sid\",\"type\":\"string\"},{\"name\":\"uid\",\"type\":\"string\"},{\"name\":\"syncid\",\"type\":\"int\"},{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"ip\",\"type\":\"int\"},{\"name\":\"callName\",\"type\":\"string\"},{\"name\":\"logs\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"RawLog\",\"fields\":[{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"level\",\"type\":\"string\"},{\"name\":\"extras\",\"type\":{\"type\":\"map\",\"values\":\"string\"}},{\"name\":\"descs\",\"type\":{\"type\":\"array\",\"items\":\"string\",\"java-class\":\"java.util.List\"}}]},\"java-class\":\"java.util.List\"}},{\"name\":\"mediaData\",\"type\":{\"type\":\"record\",\"name\":\"MediaData\",\"fields\":[{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"data\",\"type\":{\"type\":\"bytes\",\"java-class\":\"[B\"}}]}}]}");
        GenericDatumReader<GenericRecord> genericRecordGenericDatumReader = new GenericDatumReader<GenericRecord>(schema);
        byte[]  data = codec.decompress(byteBuffer.array());
        if (data != null) {
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data,null); //DecoderFactory.get.binaryDecoder(data, null);
            GenericRecord genericRecord = genericRecordGenericDatumReader.read(null,decoder);
            return genericRecord;
        } else {
            throw new Exception("In saveMedia method: byte[] data is null");

        }
    }

    private String decodeByteBuffer1(ByteBuffer byteBuffer,String key ) throws Exception{
        Codec codec = CodecFactory.getCodec(CodecFactory.DeflateType);
        //     Schema schema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"SvcLog\",\"namespace\":\"org.genitus.karyo.model.log\",\"fields\":[{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"sid\",\"type\":\"string\"},{\"name\":\"uid\",\"type\":\"string\"},{\"name\":\"syncid\",\"type\":\"int\"},{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"ip\",\"type\":\"int\"},{\"name\":\"callName\",\"type\":\"string\"},{\"name\":\"logs\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"RawLog\",\"fields\":[{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"level\",\"type\":\"string\"},{\"name\":\"extras\",\"type\":{\"type\":\"map\",\"values\":\"string\"}},{\"name\":\"descs\",\"type\":{\"type\":\"array\",\"items\":\"string\",\"java-class\":\"java.util.List\"}}]},\"java-class\":\"java.util.List\"}},{\"name\":\"mediaData\",\"type\":{\"type\":\"record\",\"name\":\"MediaData\",\"fields\":[{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"data\",\"type\":{\"type\":\"bytes\",\"java-class\":\"[B\"}}]}}]}");
        GenericDatumReader<GenericRecord> genericRecordGenericDatumReader = new GenericDatumReader<GenericRecord>(schema);
        byte[]  data = codec.decompress(byteBuffer.array());
        if (data != null) {
            //      System.out.println("************************Not empty****************************");
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data,null); //DecoderFactory.get.binaryDecoder(data, null);
            GenericRecord genericRecord = genericRecordGenericDatumReader.read(null,decoder);
            return genericRecord.toString();
        } else {
            throw new Exception("In saveMedia method: byte[] data is null");

        }
    }
}
