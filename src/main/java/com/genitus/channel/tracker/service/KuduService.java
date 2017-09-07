package com.genitus.channel.tracker.service;

import com.genitus.channel.tracker.client.KuduClient;
import com.google.common.util.concurrent.AbstractIdleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.SQLException;

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


    /**
     * get client log from kudu
     * @param sid
     * @return if the city is beijing,then return client log;
     *          ifthe city is guangzhou,return empty string;
     * @throws SQLException
     */
    public String getClientLog(String sid)throws SQLException{
        return kuduClient.getLog(sid);
    }
}
