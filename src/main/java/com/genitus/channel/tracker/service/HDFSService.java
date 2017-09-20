package com.genitus.channel.tracker.service;

import com.genitus.channel.tracker.client.HDFSClient;
import com.genitus.channel.tracker.util.audio.AvroSerializerUtil;
import com.genitus.channel.tracker.util.audio.RemoteService;
import com.google.common.util.concurrent.AbstractIdleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;


public class HDFSService extends AbstractIdleService {
    private static Logger logger = LoggerFactory.getLogger(HDFSService.class);

    private Map<String, HDFSClient> hdfsClientMap;

    @Inject
    public HDFSService(Map<String, HDFSClient> hdfsClientMap){
        logger.info("hdfs Service 初始化");
        this.hdfsClientMap=hdfsClientMap;
    }

    protected void startUp() throws Exception {

    }

    protected void shutDown() throws Exception {

    }




    public HashMap<String,String> getLog(String sid) throws Exception{
        String city = getCity(sid);
        HDFSClient hdfsClient = hdfsClientMap.get(city);
        String dataLog = hdfsClient.getData(sid);
        //需要从dataLog中解析出aueauf
        String[] elem = dataLog.split("AUEAUF:");

        String aueauf = elem[1];
        String[] elem1 = aueauf.split("&&");
        String aue = elem1[0];
        String auf=elem1[1];
        logger.info("In HDFSService getLog method, aue is: "+aue+", auf is: "+auf);

        byte[] bytes = hdfsClient.getMedia(sid);


        //将音频转写为WAV格式
        new RemoteService().savaAudioFile(bytes,aue,auf, sid);
     //   new RemoteService().savaAudioFile(bytes,"speex-wb", "audio/L16;rate=16000", "abcdefg");


        String data = elem[0];
        HashMap<String,String> map = new HashMap<String,String>();
        map.put("data",data);
        map.put("media","/home/jwzheng/AudioDecode/");
        return map;
    }


    /**
     * parse sid to get city(nc or sc)
     * @param sid
     * @return
     */
    private String getCity(String sid){
        return sid.substring(12,14);
    }

}
