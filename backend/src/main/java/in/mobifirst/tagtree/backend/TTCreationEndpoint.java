/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package in.mobifirst.tagtree.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.tasks.Task;
import com.google.firebase.tasks.Tasks;

import javax.servlet.ServletContext;

import in.mobifirst.tagtree.backend.model.ApiResponse;
import in.mobifirst.tagtree.backend.model.Token;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "createTokenApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.tagtree.mobifirst.in",
                ownerName = "backend.tagtree.mobifirst.in",
                packagePath = ""
        )
)
public class TTCreationEndpoint {
    @ApiMethod(name = "addNewToken", httpMethod = "POST", path = "")
    public ApiResponse addNewToken(final Token token, ServletContext context) {
        TTLogger.info("passed in parameter = " + token.toMap());

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(context.getResourceAsStream("/WEB-INF/TagTree-Dev-5c7176a14844.json"))
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
            // Block on the task for a maximum of 500 milliseconds, otherwise time out.
            Task<Boolean> task = new FirebaseDatabaseManager(databaseReference)
                    .addNewToken(token);
//            Tasks.await(task, 500, TimeUnit.MILLISECONDS);
            Tasks.await(task);
            TTLogger.info("Successfully created the token...");
            return ApiResponse.successResponse();
        } catch (Exception e) {
            e.printStackTrace();
            TTLogger.info("Failed to create the token...");
        }
        return ApiResponse.errorResponse();
    }
}
