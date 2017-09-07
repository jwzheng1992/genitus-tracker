package com.genitus.channel.tracker.client;

import com.genitus.channel.tracker.util.hdfsthrift.ThriftConnectionPoolFactory;
import com.genitus.channel.tracker.util.hdfsthrift.ThriftPoolConfig;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.genitus.sextant.SextantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.SQLException;


public class HDFSClient {
    private static Logger logger = LoggerFactory.getLogger(HDFSClient.class);
    private ThriftConnectionPoolFactory pool;
    private Schema mediaSchema;
    private Schema dataSchema;
  //  private Schema mediaSchema =   new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"SessionMedia\",\"namespace\":\"org.genitus.karyo.model.data\",\"fields\":[{\"name\":\"sid\",\"type\":\"string\"},{\"name\":\"uid\",\"type\":\"string\"},{\"name\":\"recStatus\",\"type\":\"int\"},{\"name\":\"mediaData\",\"type\":{\"type\":\"record\",\"name\":\"MediaData\",\"namespace\":\"org.genitus.karyo.model.log\",\"fields\":[{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"data\",\"type\":{\"type\":\"bytes\",\"java-class\":\"[B\"}}]}}]}");
 //   private Schema dataSchema= new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"SessionData\",\"namespace\":\"org.genitus.karyo.model.data\",\"fields\":[{\"name\":\"sid\",\"type\":\"string\"},{\"name\":\"svcLogs\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"SvcLog\",\"namespace\":\"org.genitus.karyo.model.log\",\"fields\":[{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"sid\",\"type\":\"string\"},{\"name\":\"uid\",\"type\":\"string\"},{\"name\":\"syncid\",\"type\":\"int\"},{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"ip\",\"type\":\"int\"},{\"name\":\"callName\",\"type\":\"string\"},{\"name\":\"logs\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"RawLog\",\"fields\":[{\"name\":\"timestamp\",\"type\":\"long\"},{\"name\":\"level\",\"type\":\"string\"},{\"name\":\"extras\",\"type\":{\"type\":\"map\",\"values\":\"string\"}},{\"name\":\"descs\",\"type\":{\"type\":\"array\",\"items\":\"string\",\"java-class\":\"java.util.List\"}}]},\"java-class\":\"java.util.List\"}},{\"name\":\"mediaData\",\"type\":{\"type\":\"record\",\"name\":\"MediaData\",\"fields\":[{\"name\":\"type\",\"type\":\"int\"},{\"name\":\"data\",\"type\":{\"type\":\"bytes\",\"java-class\":\"[B\"}}]}}]},\"java-class\":\"java.util.List\"}}]}");    //  args[7] : new schema string , use to parse hdfs data


    public HDFSClient(String host,int port ,int timeout){
        ThriftPoolConfig thriftPoolConfig = new ThriftPoolConfig( host, port , timeout);
        pool= new ThriftConnectionPoolFactory(thriftPoolConfig);
        logger.info("pool is initialized");
    }

    public HDFSClient(String ipPort,Schema mediaSchema,Schema dataSchema){
        this.dataSchema = dataSchema;
        this.mediaSchema = mediaSchema;
        String[] elem = ipPort.split(":");
        ThriftPoolConfig thriftPoolConfig = new ThriftPoolConfig( elem[0], Integer.parseInt(elem[1]) , 5000);
        pool= new ThriftConnectionPoolFactory(thriftPoolConfig);
        logger.info("pool is initialized");


    }

    // qudiaozhegefangfa xiezai get data get media limian
    public String getLog(String sid) throws Exception {
        ByteBuffer dataByteBuffer =  getData(sid);
        String data = decodeByteBuffer(dataByteBuffer,dataSchema);
        ByteBuffer mediaByteBuffer = getMedia(sid);
        String media = decodeByteBuffer(mediaByteBuffer,mediaSchema);
        String totalLog = "data is:"+data+"\nmedia is:"+media;
        return totalLog;
    }



    private ByteBuffer getData(String sid) throws Exception {
        logger.info("Get data...");
        TTransport tTransport = pool.getConnection();
        try {
            TProtocol protocol = new TBinaryProtocol(tTransport);
            SextantService.Client client = new SextantService.Client(protocol);
            return client.getData(sid);
        } finally {
            logger.info("closeTTransport!");
            closeTTransport(tTransport);
        }
    }

    private ByteBuffer getMedia(String sid) throws Exception {
        logger.info("Get media...");
        TTransport tTransport = pool.getConnection();
        try {
            TProtocol protocol = new TBinaryProtocol(tTransport);
            SextantService.Client client = new SextantService.Client(protocol);
            return client.getMedia(sid);
        } finally {
            logger.info("closeTTransport!");
            closeTTransport(tTransport);
        }
    }


    private void closeTTransport(TTransport tTransport) {
        try {
            if (tTransport!=null)
                pool.releaseConnection(tTransport);
        }catch (Exception e){
            logger.warn("hdfs pool releaseConnection exception",e);
        }
        logger.info("hdfs pool releaseConnection closed.");
    }


    /**
     * The data and media on hdfs are serialized data. Note:The data and media on Hbase are serialized and compressed data.
     * This method is used to deserialize the data and media on hdfs.
     * @param byteBuffer The byteBuffer is serialized using avro technique,so we need schema to deserialized it.
     * @param schema The schema is used to deserialized the avro bytebuffer
     * @return For log data, we will get the data we know;for media,we can continue decoding the output to get wav file.
     * @throws IOException
     */
    private static String decodeByteBuffer(ByteBuffer byteBuffer, Schema schema)throws IOException {
            try {
                GenericDatumReader<GenericRecord> genericDatumReader = new  GenericDatumReader<GenericRecord>(schema);
                byte[]  bytes = byteBuffer.array();
                if(bytes!=null){
                    BinaryDecoder binaryDecoder = DecoderFactory.get().binaryDecoder(bytes,null);
                    GenericRecord genericRecord = genericDatumReader.read(null,binaryDecoder);
                    return genericRecord.toString();
                }
                else
                    return null;
            }catch (IOException e1){
                throw new IOException("IOException");
            }
    }
}
