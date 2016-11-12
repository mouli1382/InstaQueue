/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package in.mobifirst.tagtree.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Named;

import static in.mobifirst.tagtree.backend.OfyService.ofy;


/**
 * An endpoint to send messages to devices registered with the backend
 * <p>
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 * <p>
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(
        name = "messaging",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.instaqueue.gm.in",
                ownerName = "backend.instaqueue.gm.in",
                packagePath = ""
        )
)
public class MessagingEndpoint {
    private static final Logger log = Logger.getLogger(MessagingEndpoint.class.getName());
    /**
     * Api Keys can be obtained from the google cloud console
     */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    public MessagingEndpoint() {
    }

    @ApiMethod(name = "pushMessage")
    public void pushMessage(@Named("tokenId") final String tokenId, @Named("phoneNumber") final String phoneNumber) throws IOException {
        if (tokenId == null || tokenId.length() == 0) {
            log.warning("Not sending message because tokenId is empty");
            return;
        }

        if (phoneNumber == null || phoneNumber.length() == 0) {
            log.warning("Not sending message because phoneNumber is empty");
            return;
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference
                .child("users")
                .orderByChild("phoneNumber")
                .equalTo(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            DataSnapshot firstChild = dataSnapshot.getChildren().iterator().next();
                            String record = (String) firstChild.getValue(false);
                            log.warning("regId = " + record);
//                        }
//
//
//                        if (dataSnapshot.exists()) {
//                            Map<String, String> map = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, String>>() {
//                            });
//
//                            if(map != null) {
//                                List<String> user = new ArrayList<>(map.values());

                                Sender sender = new Sender(API_KEY);
                                Message msg = new Message.Builder().addData("tokenId", tokenId).build();
//                                RegistrationRecord record = ofy().load().type(RegistrationRecord.class).filter("phoneNumber", phoneNumber).first().now();

//                                String record = user.get(0);
                                if (record != null) {
                                    Result result = null;
                                    try {
                                        result = sender.send(msg, record, 5);
                                        if (result.getMessageId() != null) {
                                            log.info("Message sent to " + record);
//                                            String canonicalRegId = result.getCanonicalRegistrationId();
//                                            if (canonicalRegId != null) {
//                                                // if the regId changed, we have to update the datastore
//                                                log.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRegId);
//                                                record.setRegId(canonicalRegId);
//                                                ofy().save().entity(record).now();
//                                            }
                                        } else {
                                            String error = result.getErrorCodeName();
                                            if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                                                log.warning("Registration Id " + record + " no longer registered with GCM, removing from datastore");
                                                // if the device is no longer registered with Gcm, remove it from the datastore
//                                                ofy().delete().entity(record).now();
                                            } else {
                                                log.warning("Error when sending message : " + error);
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                            }

                            }
//                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }
}
