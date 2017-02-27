package io.swagger.api.impl;

import io.swagger.api.*;
import io.swagger.model.*;

import io.swagger.model.ModelApiResponse;
import io.swagger.model.RationShopItem;

import java.util.List;

import io.swagger.api.NotFoundException;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.tasks.Task;
import com.google.firebase.tasks.Tasks;
import com.google.firebase.internal.NonNull;
import com.google.firebase.tasks.OnFailureListener;
import com.google.firebase.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.auth.FirebaseCredential;

import javax.servlet.ServletContext;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import io.swagger.model.RationShopItem;
import io.swagger.api.impl.model.ApiResponse;
import io.swagger.api.impl.model.Token;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-02-25T10:14:24.982Z")
public class ActivateApiServiceImpl extends ActivateApiService {
    @Override
    public Response tokenActivation(RationShopItem rationShopItem, SecurityContext securityContext) throws NotFoundException {
        // do some magic!
        // if(rationShopItem != null) {
        //     System.out.println(" RationshopItem phone number = " + rationShopItem.getPhone());
        //     ApiResponse apiResponse = addNewToken(token);
        //     return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, rationShopItem.getPhone())).build();
        // } else {
        // return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "Narasimha Garu")).build();
        // }

        if (rationShopItem != null) {
            System.out.println(" RationshopItem phone number = " + rationShopItem.getPhone());
            ApiResponse apiResponse = activate(rationShopItem);
            return Response.ok().entity(apiResponse).build();
        } else {
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "Invalid Input!")).build();
        }
    }

    private ApiResponse activate(final RationShopItem rationShopItem) {
        TTLogger.info("passed in parameter = " + rationShopItem.getPhone());

        String prod = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"tagtree-4ef29\",\n" +
                "  \"private_key_id\": \"ad97d383e48fca5f58496c637ccaeabeb97b5b36\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC36wWm0yj7H5vG\\nsMuWrBOvOzT7x46zAtfyPBgKup5uXptUs5LJt4Et/mZ6cO8WCsurmoVbKvpYVTpb\\nk1ulOkP98wXMAd34H1C2OpQ9+9cnD1jaUlSfS6IXMHvTXAZ01J9Vm7SYh/ULZL/I\\n+xkOifPN8lODMUi+1itBFDAWIMW79uHUwbVp9X/4Akn3zMY4EM4+moIrGtitdZN2\\nvGEBn6JusmLGt1g0RTO1JXjj13LqDaG8pTM2COYhtrDzy7r/4fzkOnww8GEgBW/W\\nFHNXvICk8NGRHPyZvcDS6q/kVGAS3ysTuX1etJ8QtaOWOgkbDE5srBnJIGEbfcgV\\n0+oO6b1FAgMBAAECggEAXG0gtvixRJpa55wbhAnQDqTbeJiN7wpDyblHf8TPL7od\\np5Bi+TpeEeYiS/ALRvF/7ypFUE6l5tRV8oV4be19E9cYl7Bsg3ABWMLRkuDH/Bor\\nVfRAEJmqyKqV398EY5wiwpvwQyM4E1S7SN5fdj3/pwTb0TdoTndE33ysucIYfN+2\\n0gFAlLIVPCMnxaq8N/YUJ1MRIBVCUg4JAM62LDmpGlMGhsq56TlwXOuL+fl1gybJ\\nPmzHN8S8qTOVmxmPoF42SoMjl176buCCgHbiAvGXa5ip4PEOK0o+pDVQHrap3fF4\\np25fuWnhFG+YdZbHOn5R4X1ZiV5+f9M+q0S7ImwFgQKBgQDwxrFqP6cD7ucZnHmC\\nIKyW1ss/doOIX3lT6XC5wt5Y1YwPDsgbHE068JrGhwuiwEOhjJ7kzUkuefQVmq95\\n93t/f9XNjxJspELCq2UI+0j9Ll/HqrAvAmKKphq+E7F0A8xehkev6lMHb1rc7oJQ\\nKbbOisYlZC+zoSALEjHWSBW0JQKBgQDDi//XdV84VvzMMF5L7cXrs8W2Q4YQAT+n\\nfgAtwWMvtvMwu/GAu9F/yaoxeej1Kkq/bBnhlaM98qxgvDafLbYiKfgMKNK9gcbw\\n2G/0NPPXTszBCkYpyGJq6RpgDMLpKaN2ncJ16QYz4N6O7g17aw+WWPgknJgBbjIg\\n7EHGnTIKoQKBgDMkAMrweij7tVpbQfPBvObM+J6iLY3puXt5Odg6678ynG0WVqpr\\neHvsXvOL+4y2CadmltlCQSj9/joYgO0HA9Qw8tiWavNocEWo4ezmcrpT/0QJnSJe\\n/08zuLpmtGpP5DivjMpwmvIZVNNYVZVxRsLX9v46KWed8ZBobO12oiWBAoGAPFQl\\n41gfv2b+6RQHE4/Q3w4AcmHUq31nIQp7jaI7Mo5T6vYg5OGNGcqiyk7dMz4P/4yf\\nU3xx93u1+MY4Z9WXemTOVRoHIlY2hTmRGJt5mzSBcRD1YpCi5G70JyvoujyRZNf5\\nFZ2Fv12h7CC/YhM+pNjf/ZOeRdq0dnnNNTRsKuECgYEAzz8RHpDgPkHLU3Z/sojt\\ncRI0hrQqdCxfiUbMYeBkH5AodzLJzp88kh3RDy/sDieQh9IdakDWYMQCwu9htpHE\\nClop9ztSf0+Yy2a/0YwWXyB5BDo/QYNyadE1TvCnfA3gsHHNsVbfO3hpG0FFfCuM\\nLpWkfgiAw1gS88vWYsiinkM=\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"firebase-adminsdk-jzbed@tagtree-4ef29.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"116244324420969168832\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://accounts.google.com/o/oauth2/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-jzbed%40tagtree-4ef29.iam.gserviceaccount.com\"\n" +
                "}\n";

        InputStream serviceAccount = new ByteArrayInputStream(prod.getBytes());

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://tagtree-4ef29.firebaseio.com")
                .build();

        try {
            FirebaseApp.getInstance();
        } catch (Exception error) {
            TTLogger.info("doesn't exist...");
        }

        try {
            FirebaseApp.initializeApp(options);
        } catch (Exception error) {
            TTLogger.info("already exists...");
        }

        // As an admin, the app has access to read and write all data, regardless of Security Rules
        final DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference();
        TTLogger.info("DatabaseRef obtained...");

        try {
            Task<Void> task = new FirebaseDatabaseManager(databaseReference)
                    .activateOrComplete(rationShopItem)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            TTLogger.info("Successfully called the next person in the Queue...");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            TTLogger.info("Failed to call the next person in the Queue...");
                        }
                    });
            Tasks.await(task);

            if (task.isSuccessful()) {
                return ApiResponse.successResponse();
            }
        } catch (Exception e) {
            TTLogger.info("Failed to call the next person in the Queue...");
        }
        return ApiResponse.errorResponse();
    }
}
