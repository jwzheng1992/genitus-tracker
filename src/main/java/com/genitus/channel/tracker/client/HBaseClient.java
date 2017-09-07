package com.genitus.channel.tracker.client;

import com.genitus.channel.tracker.model.HBaseResult;
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
import java.util.LinkedList;
import java.util.List;
import java.util.zip.DataFormatException;

public class HBaseClient {
    private org.genitus.forceps.hbase.HBaseClient client;
    private static Logger logger = LoggerFactory.getLogger(HBaseClient.class);

    public HBaseClient(String quorum, String table, String family, String qualifier) throws IOException {
        try {
            org.genitus.forceps.hbase.HBaseClient.Builder builder = org.genitus.forceps.hbase.HBaseClient.newBuilder( quorum,  table,  family,  qualifier);
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

    /**
     * This method transform LinkedList<String> type logs into String type.
     * @param sid
     * @return
     * @throws IOException
     * @throws DataFormatException
     */
    public String getLog(String sid) throws IOException,DataFormatException {
        LinkedList<String> listData = getListLog(sid);
        HBaseResult hBaseResult = new HBaseResult();
        hBaseResult.setSid(sid);
        String[] svcLogs=new String[listData.size()];
        int i=0;
        for (String data:listData){
            svcLogs[i]=data;
            i++;
        }
        hBaseResult.setSvcLogs(svcLogs);
        return hBaseResult.toString();
    }

    /**
     * Thie method call getLogs method to get los as return. The return type is LinkedList<String>
     * Each string is a log.For a specific,it corresponding many logs. So this method return LinkedList<String>
     * @param sid
     * @return   LinkedList<String>. String is one log. One sid corresponding many logs.
     * @throws IOException
     * @throws DataFormatException
     */
    public  LinkedList<String> getListLog(String sid) throws IOException,DataFormatException{
        List<LogData> list = client.getLogs(sid);
        LinkedList<String>  listData = new LinkedList<String>();
        for (LogData logData:list){
            ByteBuffer byteBuffer = logData.data;
            String data = decodeByteBuffer(byteBuffer);
            listData.add(data);
        }
        return listData;
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
    public String decodeByteBuffer(ByteBuffer byteBuffer) throws IOException,DataFormatException{
        Codec codec = CodecFactory.getCodec(CodecFactory.DeflateType);
        Schema schema = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"SvcLog\",\"namespace\":\"org.genitus.karyo.model.log\",\"fields\":[{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"sid\",\"type\":\"string\"},{\"name\":\"uid\",\"type\":\"string\"},{\"name\":\"syncid\",\"type\":\"int\"},{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"ip\",\"type\":\"int\"},{\"name\":\"callName\",\"type\":\"string\"},{\"name\":\"logs\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"RawLog\",\"fields\":[{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"level\",\"type\":\"string\"},{\"name\":\"extras\",\"type\":{\"type\":\"map\",\"values\":\"string\"}},{\"name\":\"descs\",\"type\":{\"type\":\"array\",\"items\":\"string\",\"java-class\":\"java.util.List\"}}]},\"java-class\":\"java.util.List\"}},{\"name\":\"mediaData\",\"type\":{\"type\":\"record\",\"name\":\"MediaData\",\"fields\":[{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"data\",\"type\":{\"type\":\"bytes\",\"java-class\":\"[B\"}}]}}]}");
        GenericDatumReader<GenericRecord> genericRecordGenericDatumReader = new GenericDatumReader<GenericRecord>(schema);
        try {
            try {
                byte[]  data = codec.decompress(byteBuffer.array());
                if (data != null) {
                    BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data,null); //DecoderFactory.get.binaryDecoder(data, null);
                    GenericRecord genericRecord = genericRecordGenericDatumReader.read(null,decoder);
                    return genericRecord.toString();
                } else
                    return null;
            }catch (IOException e ){
                throw new IOException("IOException");
            }
        }catch (DataFormatException e){
            throw new IOException("DataFormatException");
        }
    }






}
