package com.genitus.channel.tracker.service;

import com.genitus.channel.tracker.client.KuduClient;
import com.google.common.util.concurrent.AbstractIdleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class KuduService extends AbstractIdleService {
    private static Logger logger = LoggerFactory.getLogger(KuduService.class);

    private KuduClient kuduClient;
    @Inject
    public KuduService(KuduClient kuduClient){
        logger.info("kudu service 初始化");
        this.kuduClient = kuduClient;
    }

    protected void startUp() throws Exception {
        logger.info("kudu service 启动");
    }

    protected void shutDown() throws Exception {
        logger.info("kudu service 关闭");
        if (kuduClient!=null) kuduClient.closeClient();
    }
}
