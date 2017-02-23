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

import javax.servlet.ServletContext;

import in.mobifirst.tagtree.backend.model.ApiResponse;
import in.mobifirst.tagtree.backend.model.RationShopItem;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "tokenApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.tagtree.mobifirst.in",
                ownerName = "backend.tagtree.mobifirst.in",
                packagePath = ""
        )
)
public class TagTreeEndpoint {

    @ApiMethod(name = "activate", httpMethod = "POST", path = "")
    public ApiResponse activate(final RationShopItem rationShopItem, ServletContext context) {
        TagTreeLogger.info("passed in parameter = " + rationShopItem.toString());

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setServiceAccount(context.getResourceAsStream("/WEB-INF/TagTree-Dev-5c7176a14844.json"))
                .setDatabaseUrl("https://tagtree-dev.firebaseio.com/")
                .build();

        try {
            FirebaseApp.getInstance();
        } catch (Exception error) {
            TagTreeLogger.info("doesn't exist...");
        }

        try {
            FirebaseApp.initializeApp(options);
        } catch (Exception error) {
            TagTreeLogger.info("already exists...");
        }

        // As an admin, the app has access to read and write all data, regardless of Security Rules
        final DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference();
        TagTreeLogger.info("DatabaseRef obtained...");

        new FirebaseDatabaseManager(databaseReference).activate(rationShopItem);

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(0);
        apiResponse.setMessage("SUPER COOL!");

        return apiResponse;
    }
}
