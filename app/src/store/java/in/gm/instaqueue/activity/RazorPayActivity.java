package in.gm.instaqueue.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import in.gm.instaqueue.R;
import in.gm.instaqueue.firebase.FirebaseManager;
import in.gm.instaqueue.prefs.SharedPrefs;

public class RazorPayActivity extends Activity implements PaymentResultListener {
    private static final String TAG = RazorPayActivity.class.getSimpleName();
    private Button mButton;
    private EditText mCreEditText;
    private TextView mTextView;
    private String mCredits;
    private Long mCreditsInr;

    private FirebaseUser mFirebaseUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_razor_pay);

        super.onCreate(savedInstanceState);
        // Payment button created by you in XML layout
        mButton = (Button) findViewById(R.id.btn_pay);

        mTextView = (TextView) findViewById(R.id.creditsTextView);
        mCredits = getIntent().getExtras().getString("message");

        mCreditsInr = Long.parseLong(mCredits) * 100;



        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment();
            }
        });
    }

    public void startPayment() {
        /**
         * You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = this;

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "MobiFirst");
            options.put("description", "Buy credits");
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://rzp-mobile.s3.amazonaws.com/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", mCreditsInr);

            JSONObject preFill = new JSONObject();
            preFill.put("email", "gnm444@gmail.com");
            preFill.put("contact", "9177901022");

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
            SharedPrefs sharePref = new SharedPrefs(getApplicationContext());
            FirebaseDatabase.getInstance().getReference().child(sharePref.getSting(SharedPrefs.UUID_KEY)).child("credits")
                    .push().setValue(mCredits);
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
        }
    }

    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    @SuppressWarnings("unused")
    @Override
    public void onPaymentError(int code, String response) {
        try {
            Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentError", e);
        }
    }
}

