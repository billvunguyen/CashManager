package com.bill.cashmanager.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bill.cashmanager.BuildConfig;
import com.bill.cashmanager.R;
import com.bill.cashmanager.models.MyCartModel;
import com.bill.cashmanager.ui.home.HomeFragment;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PayPalButton;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PaymentMethodActivity extends AppCompatActivity {

    private static final String YOUR_CLIENT_ID = "AaUAd_sQwBl-pobhdnlSQeotko5NcUDt9oRhLz6ir8mcphaVIGjDL613-MuuH5yeIO3rA-R1mKEIp8-R";

    PayPalButton paypalButton;
    TextView tv_totalBill;
    Button btn_payment_stripe;
    int totalBill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        List<MyCartModel> list = (ArrayList<MyCartModel>) getIntent().getSerializableExtra("itemList");

        paypalButton = findViewById(R.id.payPalButton);
        tv_totalBill = findViewById(R.id.tv_totalBill_payment);
        btn_payment_stripe = findViewById(R.id.btn_payment_stripe);
        btn_payment_stripe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),PaymentStripeActivity.class);
                intent.putExtra("itemList", (Serializable) list);
                intent.putExtra("totalPayment",totalBill);
                startActivity(intent);

            }
        });


        totalAmount();

        // Paypal method
        CheckoutConfig config = new CheckoutConfig(
                getApplication(),
                YOUR_CLIENT_ID,
                Environment.SANDBOX,
                String.format("%s://paypalpay", "com.bill.cashmanager"),
                CurrencyCode.EUR,
                UserAction.PAY_NOW,
                new SettingsConfig(
                        true,
                        false
                )
        );
        PayPalCheckout.setConfig(config);

        paypalButton.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NotNull CreateOrderActions createOrderActions) {
                        ArrayList purchaseUnits = new ArrayList<>();
                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.EUR)
                                                        .value(String.valueOf(totalBill))
                                                        .build()
                                        )
                                        .build()
                        );
                        Order order = new Order(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );
                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                new OnApprove() {
                    @Override
                    public void onApprove(@NotNull Approval approval) {
                        approval.getOrderActions().capture(new OnCaptureComplete() {
                            @Override
                            public void onCaptureComplete(@NotNull CaptureOrderResult result) {
                                Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                                Toast.makeText(PaymentMethodActivity.this, "Succes", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), PlacedOrderActivity.class);
                                intent.putExtra("itemList", (Serializable) list);
                                startActivity(intent);
                            }
                        });
                    }
                }
        );
    }

    public void totalAmount() {
        Intent intent = getIntent();
        totalBill = intent.getIntExtra("totalPayment",0);
        tv_totalBill.setText("Total Bill: " + totalBill+"€");
    }
}