package com.genitus.channel.tracker.router;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.genitus.channel.tracker.service.ESService;
import com.genitus.channel.tracker.service.HBaseService;
import com.genitus.channel.tracker.service.HDFSService;
import com.genitus.channel.tracker.service.KuduService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Path("/tracker")
@Produces(MediaType.APPLICATION_JSON)
public class RestApiRouter {
    private static Logger logger = LoggerFactory.getLogger(RestApiRouter.class);

    private KuduService kuduService;
    private HBaseService hBaseService;
    private HDFSService hdfsService;
    private ESService esService;

    @Inject
    public RestApiRouter(KuduService kuduService,HBaseService hBaseService,HDFSService hdfsService,ESService esService){
        this.esService = esService;
        this.hdfsService = hdfsService;
        this.hBaseService = hBaseService;
        this.kuduService = kuduService;
    }


    @Path("search/clientlog/{sid}") @GET @Timed
    public Response geClienttLogBySid(@PathParam("sid") String sid) throws Exception{
        try {
            String response = kuduService.getClientLog(sid);
            System.out.println("Client log is:");
            System.out.println(response);
            if (response!=null)
                return Response.ok(response).build();
            else
                return Response.ok("Can find this log by this sid: "+sid).build();
        }catch (SQLException e){
            logger.error("Get client log error by sid: "+sid,e);
            return Response.status(500).build();
        }
    }

    @Path("search/serverlog/{sid}") @GET @Timed
    public Response getServerLogBySid(@PathParam("sid") String sid)  {
        try {
           HashMap<String,String> map = hBaseService.getLog(sid);
           if (map!=null){
               logger.info("It is on hbase...");
               System.out.println("Server log is:");
               System.out.println(map.get("data"));
               return  Response.ok(map.get("data")).build();

          //     return  Response.ok(JSON.toJSONString(map)).build();
           }
            logger.info("It is not save on hbase, we will search on hdfs...");
            map =  hdfsService.getLog(sid);
            System.out.println("Server log is:");
            System.out.println(map.get("data"));
            return Response.ok(map.get("data")).build();
          //  return  Response.ok(JSON.toJSONString(map)).build();
        }catch (Exception e){
            logger.error("Get server log error by sid: "+sid,e);
            return Response.status(500).build();
        }
    }


    @Path("search/{json}") @GET @Timed
    public Response getSidFromESCluster(@PathParam("json") String json)  {
        try {
            String[] json1 = json.split("=");
            String json2 = json1[1].replace("'","\"");
            String json3 = json2.replace(")","}");
            String json4 = json3.replace("(","{");

         //   String[] json1 = json.split("=");
      //      System.out.println("json[1] is: "+json1[1]);
       //     return Response.ok(json1[1]).build();
            logger.info(json4);
            System.out.println(json4);
            System.out.println(json4);
            String response = esService.search(json4);
            System.out.println("ES result is:");
            System.out.println(response);

            return Response.ok(response).build();
        }catch (Exception e){
            logger.error("Get sid error",e);
            return Response.status(500).build();
        }
    }






}
