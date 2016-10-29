package in.gm.instaqueue.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseManager implements DatabaseManager {

    private DatabaseReference mDatabaseReference;

    public FirebaseDatabaseManager() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getDatabaseReference() {
        return mDatabaseReference;
    }
}

