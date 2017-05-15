package in.mobifirst.tagtree.flic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.util.ApplicationConstants;
import io.flic.lib.FlicBroadcastReceiver;
import io.flic.lib.FlicButton;
import io.flic.lib.FlicManager;

public class TTFlicBroadcastReceiver extends FlicBroadcastReceiver {

    @Override
    protected void onRequestAppCredentials(Context context) {
        // Set app credentials by calling FlicManager.setAppCredentials here
        FlicManager.setAppCredentials("5ba577b5-ad43-4c40-b90a-c4686304a246", "a7e2e585-8f17-4180-a737-860e822905c7", "TagTree");
    }

    @Override
    public void onButtonUpOrDown(final Context context, FlicButton button, boolean wasQueued, int timeDiff, boolean isUp, boolean isDown) {
        if (isUp) {

            Log.e("TTFlicBroadcastReceiver", button.getName());
            // Code for button up event here
            IQSharedPreferences iqSharedPreferences = ((IQStoreApplication) context.getApplicationContext()).getApplicationComponent().getIQSharedPreferences();


            FirebaseDatabaseManager firebaseDatabaseManager = ((IQStoreApplication) context.getApplicationContext()).getApplicationComponent().getFirebaseDatabaseManager();
            firebaseDatabaseManager.activate(iqSharedPreferences.getSting(ApplicationConstants.STORE_UID))
                    .addOnSuccessListener(new OnSuccessListener<Boolean>() {
                        @Override
                        public void onSuccess(Boolean aBoolean) {
                            Toast.makeText(context, "Calling up the next person in the Queue", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Congratulations! you have served all your customers.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Code for button down event here
//      Toast.makeText(context, "Down event grabbed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onButtonRemoved(Context context, FlicButton button) {
        // Button was removed
        Toast.makeText(context, "Button removed. Kindly try to connect it again.", Toast.LENGTH_SHORT).show();
    }
}