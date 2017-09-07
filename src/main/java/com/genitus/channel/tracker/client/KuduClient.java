package com.genitus.channel.tracker.client;

//import com.genitus.channel.tracker.service.KuduService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class KuduClient {
    private static Logger logger = LoggerFactory.getLogger(KuduClient.class);
    static {
        try {
            Class.forName("com.cloudera.impala.jdbc41.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("com.cloudera.impala.jdbc41.Driver noe exist",e);
            throw new RuntimeException();
        }
    }


    private Connection connection;

    /**
     * Constructor method.
     * @param url
     * @throws SQLException
     */
    public KuduClient(String url){
        logger.info("kudu client 初始化");
        try {
            String address = "jdbc:impala://"+url+"/session";
            connection = DriverManager.getConnection(address);
        }catch (SQLException e){
            logger.error("Kudu database connect failed",e);
            throw new RuntimeException();
        }
    }

    public void closeClient(){
       try {
           if (connection!=null)
               connection.close();
       }catch (SQLException e){
           logger.warn("kudu client close exception",e);
       }
        logger.info("kudu client closed.");
    }


    /**
     * Get data from kudu database using sid.
     * @param sid
     * @return
     * @throws SQLException
     */
    public String getLog(String sid) throws SQLException{
        //String sql = "select rawlog from session.client_session where day = '2017-09-04' and sid = "+sid;  //select rawlog from session.client_session where day = '2017-08-24' and sid = 'iated28b0bd@sc15e125c268e8010210'
        Date date = new Date(toTimestamp(sid));
        String day = new SimpleDateFormat("yyyy-MM-dd").format(date);
        String sql = "select rawlog from session.client_session where day = '"+day+"' and sid ='"+sid+"'";
        logger.info("sql is:"+sql);
        return getResult(sql);
    }

    /**
     * Get data from Kudu database using sql
     * This method is called by the above method:getLog(String sid)
     * @param sql
     * @return
     * @throws SQLException
     */
    private String getResult(String sql)throws SQLException{
        String json=null;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        if ( resultSet.next())
            json = (String)resultSet.getObject(1);
        else
            logger.warn("Can not get client log by this sid. The sql sentence is: "+sql);
        return json;
    }




    // test method
    public static void main(String[] args){
        try {
            KuduClient kuduClient = new KuduClient("jdbc:impala://172.26.5.11:21050/session");
            System.out.println(kuduClient.getLog("iat27a8b8a8@sc15dcbb153fd8410480"));
            //   System.out.println(result);
        }catch (SQLException e){
            //log....
            logger.error("",e);

        }
    }




    /**
     * Parse sid to get timestampe in mills
     *
     * @param sid sid.
     * @return timestampe in mills.
     */
    private long toTimestamp(String sid) {
        if (null == sid || sid.length() != 32) {
            return -1;
        }
        return Long.parseLong(sid.substring(14, 25), 16);
    }


}
