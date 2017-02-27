package io.swagger.api;

import io.swagger.model.*;
import io.swagger.api.ActivateApiService;
import io.swagger.api.factories.ActivateApiServiceFactory;

import io.swagger.annotations.ApiParam;
import io.swagger.jaxrs.*;

import io.swagger.model.ModelApiResponse;
import io.swagger.model.RationShopItem;

import java.util.List;
import io.swagger.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.*;

@Path("/activate")


@io.swagger.annotations.Api(description = "the activate API")
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-02-25T10:14:24.982Z")
public class ActivateApi  {
   private final ActivateApiService delegate = ActivateApiServiceFactory.getActivateApi();

    @POST
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "Calls the next person in the Queue", notes = "By passing in the appropriate options, you can call the next person in the queue at an FP shop", response = ModelApiResponse.class, tags={ "developers", })
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "Successfully called the next person", response = ModelApiResponse.class),
        
        @io.swagger.annotations.ApiResponse(code = 400, message = "Invalid rationShopId supplied", response = ModelApiResponse.class),
        
        @io.swagger.annotations.ApiResponse(code = 405, message = "Invalid input supplied", response = ModelApiResponse.class) })
    public Response tokenActivation(@ApiParam(value = "FP shop item to call the next person" ,required=true) RationShopItem rationShopItem
,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.tokenActivation(rationShopItem,securityContext);
    }
}
