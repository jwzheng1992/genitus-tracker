package com.genitus.channel.tracker.router;

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


    @Path("search/{sid}") @GET @Timed
    public Response getLogBySid(@PathParam("sid") String sid) {
        try {
            String response = "";
            return Response.ok(response).build();
        }catch (Exception e){
            return Response.status(500).build();
        }
    }
}
