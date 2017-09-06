package com.genitus.channel.tracker.client;

import com.genitus.channel.tracker.service.KuduService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KuduClient {
    private static Logger logger = LoggerFactory.getLogger(KuduService.class);

    public KuduClient(String kuduAddress){
        logger.info("kudu client 初始化");
    }

    public void closeClient(){
        logger.info("kudu client 关闭");
    }
}
