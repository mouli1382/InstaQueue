package in.mobifirst.tagtree.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;

public class RazorPayActivity extends Activity implements PaymentResultListener {
    private static final String TAG = RazorPayActivity.class.getSimpleName();
    private Button mButton;
    private EditText mCreEditText;
    private TextView mTextView;
    private String mCredits;
    private Long mCreditsInr;

    @Inject
    protected FirebaseDatabaseManager mFirebaseDatabaseManager;

    @Inject
    protected IQSharedPreferences mIQSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setContentView(R.layout.activity_razor_pay);

        super.onCreate(savedInstanceState);
        ((IQStoreApplication)getApplication())
                .getApplicationComponent()
                .inject(this);


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
            FirebaseCrash.report(new Exception("Error in payment: " + e.getMessage()));
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
            mFirebaseDatabaseManager.getDatabaseReference().child(mIQSharedPreferences.getSting(mIQSharedPreferences.UUID_KEY)).child("credits")
                    .push().setValue(mCredits);
        } catch (Exception e) {
            Log.e(TAG, "Exception in onPaymentSuccess", e);
            FirebaseCrash.report(new Exception("Exception in razor pay onPaymentError"));
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
            FirebaseCrash.report(new Exception("Exception in razor pay onPaymentError"));
        }
    }
}

