package in.mobifirst.tagtree.backend;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class FirebaseAuthUtils {

    public FirebaseAuthUtils() {
        FirebaseOptions options = null;
        try {
            options = new FirebaseOptions.Builder()
                    .setServiceAccount(new FileInputStream("InstaQueue-2f7ccdeea827.json"))
                    .setDatabaseUrl("https://instaqueue-9f086.firebaseio.com")
                    .build();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FirebaseApp.initializeApp(options);
    }
}
