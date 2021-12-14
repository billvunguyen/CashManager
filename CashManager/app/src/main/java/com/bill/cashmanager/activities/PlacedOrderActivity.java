package com.bill.cashmanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bill.cashmanager.MainActivity;
import com.bill.cashmanager.R;
import com.bill.cashmanager.models.MyCartModel;
import com.bill.cashmanager.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlacedOrderActivity extends AppCompatActivity {

    Button btn_back_home;

    FirebaseAuth auth;
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placed_order);

        btn_back_home = findViewById(R.id.btn_back_home);
        btn_back_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PlacedOrderActivity.this, MainActivity.class));
            }
        });

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        List<MyCartModel> list = (ArrayList<MyCartModel>) getIntent().getSerializableExtra("itemList");

        if (list != null && list.size() > 0) {
            for (MyCartModel myCartModel : list) {

                final HashMap<String,Object> cartMap = new HashMap<>();

                cartMap.put("productName",myCartModel.getProductName());
                cartMap.put("productPrice",myCartModel.getProductPrice());
                cartMap.put("currentDate",myCartModel.getCurrentDate());
                cartMap.put("currentTime",myCartModel.getCurrentTime());
                cartMap.put("totalQuantity",myCartModel.getTotalQuantity());
                cartMap.put("totalPrice",myCartModel.getTotalPrice());

                firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                        .collection("MyOrder").add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        Toast.makeText(PlacedOrderActivity.this, "Your order has been placed", Toast.LENGTH_SHORT).show();
                        // delete cart after payment
                        firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                                .collection("AddToCart")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                                String documentId = documentSnapshot.getId();
                                                Log.d("test delete",documentId);
                                                firestore.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                                                        .collection("AddToCart")
                                                        .document(documentId)
                                                        .delete();
                                            }
                                        }
                                    }
                                });
                    }
                });
            }
        }
    }
}