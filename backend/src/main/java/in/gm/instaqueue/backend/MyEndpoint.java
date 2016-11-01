package in.gm.instaqueue.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "myApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.instaqueue.gm.in",
                ownerName = "backend.instaqueue.gm.in",
                packagePath = ""
        )
)
public class MyEndpoint {
    private static final Logger log = Logger.getLogger(MyEndpoint.class.getName());

    public MyEndpoint() {
        log.info("Starting ....");

        //Initialize firebase here.
        new FirebaseAuthUtils();

        new MessagingEndpoint();
    }

    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name) {
        MyBean response = new MyBean();
        response.setData("Hi, " + name);

        return response;
    }


    @ApiMethod(name = "createCustomToken")
    public MyBean createCustomToken(@Named("uid") String uid, @Named("phoneNumber") String phoneNumber) {
        HashMap<String, Object> additionalClaims = new HashMap<>();
        additionalClaims.put("phoneNumber", phoneNumber);
        String customToken = FirebaseAuth.getInstance().createCustomToken(uid, additionalClaims);
        MyBean response = new MyBean();
        response.setData(customToken);

        return response;
    }

}
