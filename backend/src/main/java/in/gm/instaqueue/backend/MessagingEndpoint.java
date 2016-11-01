/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Backend with Google Cloud Messaging" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/GcmEndpoints
*/

package in.gm.instaqueue.backend;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Named;

import static in.gm.instaqueue.backend.OfyService.ofy;


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
    private DatabaseReference mFirebaseRef;

    /**
     * Api Keys can be obtained from the google cloud console
     */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    public MessagingEndpoint() {

        //Initialize firebase database reference here.
        mFirebaseRef = FirebaseDatabase.getInstance().getReference();
        sendNotification(mFirebaseRef);
    }

    // Registers for changes to the firebase database and fires notificaiton when new items are added.
    private void sendNotification(DatabaseReference databaseReference) {

        databaseReference
                .child("topics")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        log.info("onChildAdded ....");
                        if (dataSnapshot.exists()) {
                            //send a downstream notification and let the client app handle the data.
                            Map<String, String> map = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, String>>() {
                            });
                            List<String> list = new ArrayList<String>(map.values());
                            log.info("check map values = "+ list);

                            pushMessage(list.get(0), list.get(1)/*, map.get("tokenNumber")*/);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void pushMessage(String tokenId, String phoneNumber/*, String tokenNumber*/) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL("https://fcm.googleapis.com/fcm/send").openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            // HTTP request header
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "key=" + API_KEY);
            connection.connect();

            JSONObject data = new JSONObject();
            data.put("to", "/topics/token_change_event_"+ phoneNumber);
            data.put("notification", new JSONObject()
                    .put("body", "Token number is " + phoneNumber)
                    .put("title", "You have got a new token!"));
            data.put("data", new JSONObject()
                    .put("tokenId", tokenId));

            OutputStream os = connection.getOutputStream();
            os.write(data.toString().getBytes("UTF-8"));
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
    }


    /**
     * Send to the first 10 devices (You can modify this to send to any number of devices or a specific device)
     *
     * @param message The message to send
     */
    public void sendMessage(@Named("message") String message) throws IOException {
        if (message == null || message.trim().length() == 0) {
            log.warning("Not sending message because it is empty");
            return;
        }
        // crop longer messages
        if (message.length() > 1000) {
            message = message.substring(0, 1000) + "[...]";
        }
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message", message).build();
        List<RegistrationRecord> records = ofy().load().type(RegistrationRecord.class).limit(10).list();
        for (RegistrationRecord record : records) {
            Result result = sender.send(msg, record.getRegId(), 5);
            if (result.getMessageId() != null) {
                log.info("Message sent to " + record.getRegId());
                String canonicalRegId = result.getCanonicalRegistrationId();
                if (canonicalRegId != null) {
                    // if the regId changed, we have to update the datastore
                    log.info("Registration Id changed for " + record.getRegId() + " updating to " + canonicalRegId);
                    record.setRegId(canonicalRegId);
                    ofy().save().entity(record).now();
                }
            } else {
                String error = result.getErrorCodeName();
                if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                    log.warning("Registration Id " + record.getRegId() + " no longer registered with GCM, removing from datastore");
                    // if the device is no longer registered with Gcm, remove it from the datastore
                    ofy().delete().entity(record).now();
                } else {
                    log.warning("Error when sending message : " + error);
                }
            }
        }
    }
}
