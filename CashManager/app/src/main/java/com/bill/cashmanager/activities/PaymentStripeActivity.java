package com.bill.cashmanager.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bill.cashmanager.models.MyCartModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.StripeIntent;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;
import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.bill.cashmanager.R;
import com.stripe.android.view.CardInputWidget;

public class PaymentStripeActivity extends AppCompatActivity {

//    private static final String BACKEND_URL = "http://10.0.2.2:4242/";
    private static final String BACKEND_URL = "https://cashmanager-stripe-backend.herokuapp.com/";

    private OkHttpClient httpClient = new OkHttpClient();
    private String paymentIntentClientSecret;
    private Stripe stripe;

    int totalBill;
    List<MyCartModel> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_stripe);
        totalAmount();
        list = (ArrayList<MyCartModel>) getIntent().getSerializableExtra("itemList");
        // Configure the SDK with your Stripe pulishable key so it can make request to Stripe
        stripe = new Stripe(
                getApplicationContext(),
                Objects.requireNonNull("pk_test_51K6HVQLQqDobSi5XOaXijXoijRYAApH2NV64iIR4QMY0R52UHt0lhDZBXkAdpLQruvEVZ3B0nPb1E67QrKkVuDQZ00oUpGYAQp")
        );
        startCheckout();
    }

    private void startCheckout() {
        // Create a PaymentIntent by calling the server's endpoint.
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        int amount = totalBill*100; // 100 = 1EUR
        Map<String,Object> payMap = new HashMap<>();
        Map<String,Object> itemMap = new HashMap<>();
        List<Map<String,Object>> itemList = new ArrayList<>();
        payMap.put("currency","eur");
        itemMap.put("id","photo_subscription");
        itemMap.put("amount",amount);
        itemList.add(itemMap);
        payMap.put("items",itemList);
        String json = new Gson().toJson(payMap);

        RequestBody body = RequestBody.create(json,mediaType);
        Request request = new Request.Builder()
                .url(BACKEND_URL + "create-payment-intent")
                .post(body)
                .build();
        httpClient.newCall(request)
                .enqueue(new PayCallback(this));

        //Hook up the pay button to the card widget and stripe instance
        Button payButton = findViewById(R.id.payButton);
        payButton.setOnClickListener((View view) -> {
            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();
            if (params != null) {
                ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                        .createWithPaymentMethodCreateParams(params,paymentIntentClientSecret);
                stripe.confirmPayment(this,confirmParams);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        stripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));
    }

    private static final class PayCallback implements Callback {
        @NonNull private final WeakReference<PaymentStripeActivity> activityRef;
        PayCallback(@NonNull PaymentStripeActivity activity){
            activityRef = new WeakReference<>(activity);
        }
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e){
            final PaymentStripeActivity activity = activityRef.get();
            if (activity == null){
                return;
            }
            activity.runOnUiThread(() ->
                    Toast.makeText(activity, "Error" + e.toString(), Toast.LENGTH_SHORT).show());
        };

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            final PaymentStripeActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            if (!response.isSuccessful()) {
                activity.runOnUiThread(() ->
                        Toast.makeText(activity, "Error" + response.toString(), Toast.LENGTH_SHORT).show());
            } else {
                activity.onPaymentSuccess(response);
            }

        }
    }

    private void onPaymentSuccess(@NonNull final Response response) throws IOException {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String,String>>(){}.getType();
        Map<String,String> responseMap = gson.fromJson(
                Objects.requireNonNull(response.body()).string(),
                type
        );
        paymentIntentClientSecret = responseMap.get("clientSecret");
    }

    private final class PaymentResultCallback implements ApiResultCallback<PaymentIntentResult> {
        @NonNull private final WeakReference<PaymentStripeActivity> activityRef;
        PaymentResultCallback(@NonNull PaymentStripeActivity activity) {
            activityRef = new WeakReference<>(activity);
        }
        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final PaymentStripeActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            PaymentIntent paymentIntent = result.getIntent();
            PaymentIntent.Status status = paymentIntent.getStatus();
            if (status == PaymentIntent.Status.Succeeded) {
                // Payment completed successfully
//                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                activity.displayAlert(
//                        "Payment completed",
//                        gson.toJson(paymentIntent)
//                );
                paymentSucces();

            } else if (status == PaymentIntent.Status.RequiresPaymentMethod) {
                // Payment failed = allow retrying using a different payment method
                activity.displayAlert(
                        "Payment failed",
                        Objects.requireNonNull(paymentIntent.getLastPaymentError()).getMessage()
                );
            }
        }

        @Override
        public void onError(@NonNull Exception e) {
            final PaymentStripeActivity activity = activityRef.get();
            if (activity == null) {
                return;
            }
            // Payment request failed - allow retrying using the same payment method
            activity.displayAlert("Error", e.toString());
        }
    }

    private void displayAlert(@NonNull String title, @Nullable String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message);
        builder.setPositiveButton("Ok", null);
        builder.create().show();
    }

    private void paymentSucces(){
        Toast.makeText(PaymentStripeActivity.this, "Succes", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), PlacedOrderActivity.class);
        intent.putExtra("itemList", (Serializable) list);
        startActivity(intent);
    }

    public void totalAmount() {
        Intent intent = getIntent();
        totalBill = intent.getIntExtra("totalPayment",0);
    }

}