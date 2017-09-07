package com.genitus.channel.tracker.service;

import com.genitus.channel.tracker.client.HDFSClient;
import com.google.common.util.concurrent.AbstractIdleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
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

    public String getLog(String sid) throws Exception{
        String city = getCity(sid);
        HDFSClient hdfsClient = hdfsClientMap.get(city);
        return  hdfsClient.getLog(sid);

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
