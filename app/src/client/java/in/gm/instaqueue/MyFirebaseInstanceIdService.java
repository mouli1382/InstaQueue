package in.gm.instaqueue;

import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;

import in.gm.instaqueue.application.IQClientApplication;
import in.gm.instaqueue.backend.registration.Registration;
import in.gm.instaqueue.util.ApplicationConstants;

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private static final String IQ_TOKENS_TOPIC = "token_change_event";

    /**
     * The Application's current Instance ID token is no longer valid and thus a new one must be requested.
     */
    public void onTokenRefresh() {
        // If you need to handle the generation of a token, initially or
        // after a refresh this is where you should do that.
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "FCM Token: " + token);

        // Once a token is generated, we subscribe to topic.
        String myPhoneNumber = ((IQClientApplication) getApplicationContext())
                .getApplicationComponent()
                .getIQSharedPreferences()
                .getSting(ApplicationConstants.PHONE_NUMBER_KEY);

        FirebaseMessaging.getInstance()
                .subscribeToTopic(IQ_TOKENS_TOPIC + "_" + myPhoneNumber);

        //Send the token to the app server for GCM push
        try {
            sendRegistrationToServer(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRegistrationToServer(String token) throws IOException {
        Registration.Builder builder = new Registration.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                // Need setRootUrl and setGoogleClientRequestInitializer only for local testing,
                // otherwise they can be skipped
                .setRootUrl("https://instaqueue-9f086.appspot.com/_ah/api/")
//                .setRootUrl("http://192.168.1.6:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                            throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });
        Registration regService = builder.build();
        regService.register(token).execute();
    }

}
