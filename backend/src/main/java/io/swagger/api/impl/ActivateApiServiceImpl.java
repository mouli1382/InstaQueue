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

        if(rationShopItem != null) {
            System.out.println(" RationshopItem phone number = " + rationShopItem.getPhone());
            ApiResponse apiResponse = activate(rationShopItem);
            return Response.ok().entity(apiResponse).build();
        } else {
            return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "Invalid Input!")).build();
        }
    }

    private ApiResponse activate(final RationShopItem rationShopItem) {
        TTLogger.info("passed in parameter = " + rationShopItem.getPhone());

        String str = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"tagtree-dev\",\n" +
                "  \"private_key_id\": \"3ded15b2737dac42b80c4a1a462bcdcdb6edd98a\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC/jB3ouQ4X8rUm\\nRpghJbPnJfGGNnsuZPXNqA1ITjZjqlMHFwXpCwi3C4vcs/hqdPW8zmGwgtvv0uIe\\ndNxFTDHghSlEsNOlDUYvGI3WTxXUM8+hAbEx1eAM/GGN9puyQcv/OP7jcounCqM1\\nqvtsfeCWGf5ArHbjcdW3VoY/r0bt+VQPnvbHZVrI0QL3C9+K9IlY90jVT7QACbnd\\ndjEe6pGjOENQYqvlhUfedo6Ic/XA9GrOckmGJ7KxpVCqU0hnLYg0kjCF6icLk9gp\\n5LsYDQxJ2lthj9cLn/+3p8/1THYGopp/KGDN4eompWFXqvgnLYmCuzZknkt7HahS\\nYj5exIDDAgMBAAECggEATW0a7eZXrxB5bvZcKhHubYHl5iWBl1hSD4ZdkBulWpYP\\np68DwZanOC5fo5/Py2BpsJ8P1+SdeIyawErmetB43NOWweBkPLRn6UOYmccwAK3M\\nwL1JXeahAT7HRDYp610zU4A7b77uemAPZvMtXEZpkOMC5iuQhxGNnOVJsNZt6mUK\\nG7bhFUDk0JvCAwotBnHaXI1oDsu54OOedCH3RleI7zi4VuW6DGaZEIZgqtVY09wO\\nShcSW7OqjDNOrfMbVqxCmodoJkENhgNTiw+x1zdm/L6Eq5DMR8bf2aohRGINSqI0\\nt5kV3Hh9gpE8DaFeFUg/M1oan8QZPYb8H3HGwswRsQKBgQDknOsUKnV033EBpPAA\\n4VVevlG3VWUdJn7HpQJgd0Jus89lz8n3C2GyjYMWRRIC8iZpqkSCj0t3nKPyppRB\\ngUfjvq21DYZbLFdD65vpgecyJvEnRE89hENWDiWSA7UTK5uHHKp4C0S6TGjG8V9t\\ncZfuqYEowT9A2HHq8NJ+QREgaQKBgQDWfnkfWQzUlujTsN5v8Kab++AEl4bakRbp\\nSy+pugn1gjko9bqlT8wm0hx7AXKD59qiJ+LcNQuyPvTpdXDzkqYDo9LjTMaZiwTw\\npLoClQZHaV+bvx02f+thF68wNOo8YL+g/3XnPNYp/umsWyEmbtLqh3+N9pGBTwyN\\nvqmFfZ+ySwKBgQDUHeTgqSSidE/ePrORnYIgjmYzvUA8c+NeSnSSHRW+sYfV5551\\nYlIb4cGngB7eLOAHWryGh352VRippHYa8WFKpzl1rD7liZbOpmXbm3RwzEElNfRw\\nF7CCwE2L1XTFfMip7KcfCxWR5iOxs3PfkG5wO0ZkEiomeK36V8h27Nh/QQKBgEmi\\nDS6HwXHRhIf3dcTz1h5CLzskUXUCzdy0pN4dMYIIfFrlUHejly/UfVZ0vr0tgM6d\\n5rE1vJgqKKVkXawgMcGaIbFKD/txz/ZUdk6gnhExyVKMHxkwfLtOCCQCZk7n36ED\\nUZRaPCMakVlLx2uMK/e7IDy54mWDn4mhZEyhPYtVAoGBAIerFxR4Hz5u7gDqQWPx\\nGlos5kyuelhnjl+SN0Fq6zfdBANByi6IwKVzMANyiOA8REshklqJ+8nHq+KgCPts\\noUY9T0jKXV/Fq3Acp04iff41TxA2y5JCOpZHu51vko7UqRSUHsw1YeiNDqtoOwiD\\nMK975LE5LaEXBfsdl6p3QbEN\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"firebase-adminsdk-0kwyr@tagtree-dev.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"109094219084630798122\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://accounts.google.com/o/oauth2/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-0kwyr%40tagtree-dev.iam.gserviceaccount.com\"\n" +
                "}\n";

        InputStream serviceAccount = new ByteArrayInputStream(str.getBytes());

        FirebaseOptions options = new FirebaseOptions.Builder()
            .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
            .setDatabaseUrl("https://tagtree-dev.firebaseio.com/")
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
