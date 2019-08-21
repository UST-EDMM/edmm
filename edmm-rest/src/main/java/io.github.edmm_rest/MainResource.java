package io.github.edmm_rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
@Api(value = "mainresource")
public class MainResource {
    private static Logger LOGGER = LoggerFactory.getLogger(MainResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response index() {
        LOGGER.info("root resource call");
        return Response.ok().build();
    }
}
