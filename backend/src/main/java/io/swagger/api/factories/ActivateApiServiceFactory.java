package io.swagger.api.factories;

import io.swagger.api.ActivateApiService;
import io.swagger.api.impl.ActivateApiServiceImpl;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-02-25T10:14:24.982Z")
public class ActivateApiServiceFactory {
    private final static ActivateApiService service = new ActivateApiServiceImpl();

    public static ActivateApiService getActivateApi() {
        return service;
    }
}
