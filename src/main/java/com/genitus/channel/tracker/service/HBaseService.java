package com.genitus.channel.tracker.service;

import com.genitus.channel.tracker.client.HBaseClient;
import com.google.common.util.concurrent.AbstractIdleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HBaseService extends AbstractIdleService {
    private HBaseClient hBaseClient;
    private static Logger logger = LoggerFactory.getLogger(HBaseService.class);

    protected void startUp() throws Exception {

    }

    protected void shutDown() throws Exception {

    }

    public String getLog(String sid) throws Exception{
        return hBaseClient.getLog(sid);


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
