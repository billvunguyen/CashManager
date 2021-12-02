package com.bill.cashmanager.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.bill.cashmanager.R;
import com.bill.cashmanager.adapter.ViewAllAdapter;
import com.bill.cashmanager.models.ViewAllModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ViewAllActivity extends AppCompatActivity {

    FirebaseFirestore firestore;
    RecyclerView recyclerView;
    ViewAllAdapter viewAllAdapter;
    List<ViewAllModel> viewAllModelListActivity;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firestore = FirebaseFirestore.getInstance();
        String type = getIntent().getStringExtra("type");
        recyclerView = findViewById(R.id.view_all_rec);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        viewAllModelListActivity = new ArrayList<>();
        viewAllAdapter = new ViewAllAdapter(this,viewAllModelListActivity);
        recyclerView.setAdapter(viewAllAdapter);

        // Getting Fruits
        if(type != null && type.equalsIgnoreCase("fruit")) {
            firestore.collection("AllProducts").whereEqualTo("type", "fruit").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                        ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                        viewAllModelListActivity.add(viewAllModel);
                        viewAllAdapter.notifyDataSetChanged();
                    }

                }
            });
        }

            // Getting Vegetable
            if(type != null && type.equalsIgnoreCase("vegetable")){
                firestore.collection("AllProducts").whereEqualTo("type","vegetable").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for(DocumentSnapshot documentSnapshot:task.getResult().getDocuments()){
                            ViewAllModel viewAllModel = documentSnapshot.toObject(ViewAllModel.class);
                            viewAllModelListActivity.add(viewAllModel);
                            viewAllAdapter.notifyDataSetChanged();
                        }

                    }
                });

        }
    }
}