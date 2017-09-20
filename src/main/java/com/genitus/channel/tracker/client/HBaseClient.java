package com.genitus.channel.tracker.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.zip.DataFormatException;

public class HBaseClient {
    private org.genitus.forceps.hbase.HBaseClient client;
    private Schema schema;
    private static Logger logger = LoggerFactory.getLogger(HBaseClient.class);

    public HBaseClient(String quorum, String table, String family, String qualifier,Schema schema) throws IOException {
        try {
            org.genitus.forceps.hbase.HBaseClient.Builder builder = org.genitus.forceps.hbase.HBaseClient.newBuilder( quorum,  table,  family,  qualifier);
            this.schema=schema;
            client = builder.build();
        }catch (IOException e){
            throw new IOException("IOException");
        }
    }



    public void closeHBaseClient()throws IOException{
        try {
            client.close();
        }catch (IOException e){
            throw new IOException("IOException");

        }

    }



    private String findAueAuf(List<LogData> logList)throws Exception{
        for (LogData logData:logList){
            Codec codec = CodecFactory.getCodec(CodecFactory.DeflateType);
            GenericDatumReader<GenericRecord> genericRecordGenericDatumReader = new GenericDatumReader<GenericRecord>(schema);
            byte[] data = codec.decompress(logData.data.array());
            if (data != null) {
                BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null); //DecoderFactory.get.binaryDecoder(data, null);
                GenericRecord genericRecord = genericRecordGenericDatumReader.read(null, decoder);
                if ((Integer) genericRecord.get("type") == 1){
                    JSONArray jsonArray = JSONArray.parseArray(genericRecord.get("logs").toString());
                    Map map1 = JSON.parseObject(jsonArray.get(0).toString(),Map.class);
                    Map map2 = JSON.parseObject(map1.get("extras").toString(),Map.class);
                    String params = map2.get("params").toString();
                //  System.out.println(params);
                    return params;
                }

            } else {
                throw new Exception("decode byte[] is null");
            }

        }
        return null;

    }


    /**
     * 将原本返回的List<LogData> logList中的data和media区分开，分别作为一个List<LogData>，放到HashMap中去
     * @param sid
     * @param key
     * @return
     * @throws Exception
     */
    //如果logList==null 则直接抛出异常throw new Exception("# --------- gwjiang: getLogs failed");
    //如果logList.isEmpty()!=true 则hashMap中包含了data media aueAuf三个对应的List<LogData>
    //如果logList.isEmpty() 则hashMap中同样包含了data media aueAuf三个对应的List<LogData> ，不过三个list均为empty
    public HashMap<String,List<LogData>> getLog(String sid, String key) throws Exception{
        List<LogData> logList = client.getLogs(sid);
        if (logList==null)
            System.out.println("get error");

     //   System.out.println("logList.size() is: "+logList.size());
        if (logList==null)
            throw new Exception("# --------- gwjiang: getLogs failed");

        HashMap<String,List<LogData>> map = new HashMap<String,List<LogData>>();
        List<LogData> dataList = new LinkedList<LogData>();
        List<LogData> mediaList = new LinkedList<LogData>();
        List<LogData> aueAufList = new LinkedList<LogData>();
        for (LogData logData:logList){
            ByteBuffer byteBuffer = logData.data;
            if (findMediaLog(byteBuffer,"")==1){
                mediaList.add(logData);
                dataList.add(logData);
            }
            else
                dataList.add(logData);
        }
        map.put("data",dataList);
        map.put("media",mediaList);

        if (!logList.isEmpty()){
            String params = findAueAuf(logList);
            logger.info("params: "+params);
            if (params!=null){
                String aueAuf = parseAueAuf(params);
                LogData logDataAueAuf = new LogData(aueAuf,ByteBuffer.wrap(aueAuf.getBytes()));
                aueAufList.add(logDataAueAuf);
                map.put("aueAuf",aueAufList);
            }
            else {
                map.put("aueAuf",aueAufList);
            }
        }
        else
            map.put("aueAuf",aueAufList);
        return map;
    }

    private String parseAueAuf(String params){
        if (params.contains("aue=")&&params.contains(",eos")){
            int startPos = params.indexOf("aue=");
            int endPos=params.indexOf(",eos");

            //     System.out.println( "params.substring(startPos,endPos) is: "+params.substring(startPos,endPos));
            String aueAuf = params.substring(startPos,endPos);  //aueAuf=  aue=speex-wb,auf=audio/L16;rate=16000

            String[] elem = aueAuf.split(","); // elem[0]= aue=speex-wb  elem[1]= auf=audio/L16;rate=16000

            String[] elem1 = elem[0].split("="); //elem1[0]= aue  elem1[1]= speex-wb
            String[] elem2 = elem[1].split("=");//elem2[0]= auf  elem2[1]= audio/L16;rate elem2[2]=16000

            String aue = elem1[1];
            String auf = elem2[1]+"="+elem2[2];
            //   System.out.println("In parseAueAuf method: "+aue+"&&"+auf);
            return aue+"&&"+auf; // speex-wb&&audio/L16;rate=16000
        }
        else return "";

    }


    /**
     * This method is similar with the same name method in HDFSClient.java.
     * The Only two differences between two method are as follow describe::
     * (1):The schema is different.
     * (2):We have a decompress process before we deserialized our data and media.
     * @param byteBuffer
     * @return
     * @throws IOException
     * @throws DataFormatException
     */
    //move to HBaseService
    public String decodeByteBuffer(ByteBuffer byteBuffer) throws Exception{
        Codec codec = CodecFactory.getCodec(CodecFactory.DeflateType);
       // Schema schema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"SvcLog\",\"namespace\":\"org.genitus.karyo.model.log\",\"fields\":[{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"sid\",\"type\":\"string\"},{\"name\":\"uid\",\"type\":\"string\"},{\"name\":\"syncid\",\"type\":\"int\"},{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"ip\",\"type\":\"int\"},{\"name\":\"callName\",\"type\":\"string\"},{\"name\":\"logs\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"RawLog\",\"fields\":[{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"level\",\"type\":\"string\"},{\"name\":\"extras\",\"type\":{\"type\":\"map\",\"values\":\"string\"}},{\"name\":\"descs\",\"type\":{\"type\":\"array\",\"items\":\"string\",\"java-class\":\"java.util.List\"}}]},\"java-class\":\"java.util.List\"}},{\"name\":\"mediaData\",\"type\":{\"type\":\"record\",\"name\":\"MediaData\",\"fields\":[{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"data\",\"type\":{\"type\":\"bytes\",\"java-class\":\"[B\"}}]}}]}");
        GenericDatumReader<GenericRecord> genericRecordGenericDatumReader = new GenericDatumReader<GenericRecord>(schema);

        byte[]  data = codec.decompress(byteBuffer.array());
        if (data != null) {
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data,null); //DecoderFactory.get.binaryDecoder(data, null);
            GenericRecord genericRecord = genericRecordGenericDatumReader.read(null,decoder);
            return genericRecord.toString();
        } else{
            logger.warn("decompress failed,it return null byte[]");
            throw new Exception("decompress failed,it return null byte[]");
        }
    }

    /**
     * 由于HBaseClient中的getLog方法返回的是List<LogData>类型，media和data都存在List中，因此我们需要找到media的部分。
     * 这个方法就是为了找到type=1的音频部分。
     * @param byteBuffer
     * @param key
     * @return
     * @throws Exception
     */
    private int findMediaLog(ByteBuffer byteBuffer,String key ) throws Exception{
        Codec codec = CodecFactory.getCodec(CodecFactory.DeflateType);
        GenericDatumReader<GenericRecord> genericRecordGenericDatumReader = new GenericDatumReader<GenericRecord>(schema);
        byte[] data = codec.decompress(byteBuffer.array());
        if (data != null) {
            BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null); //DecoderFactory.get.binaryDecoder(data, null);
            GenericRecord genericRecord = genericRecordGenericDatumReader.read(null, decoder);
            if ((Integer) genericRecord.get("type") == 1)
                return 1;
            else
                return 0;
        } else {
            throw new Exception("findMediaLog： byte[] data is null");
        }
    }



}
