package com.bill.cashmanager;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bill.cashmanager.adapter.MyCartAdapter;
import com.bill.cashmanager.adapter.MyOrdersAdapter;
import com.bill.cashmanager.models.MyCartModel;
import com.bill.cashmanager.models.MyOrdersModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyOdersFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseAuth auth;

    RecyclerView recyclerView;
    MyOrdersAdapter ordersAdapter;
    List<MyOrdersModel> ordersModelList;
    ProgressBar progressBar;

    View myOrdersEmpty;

    public MyOdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_my_oders,container,false);

        myOrdersEmpty = root.findViewById(R.id.my_order_empty);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        progressBar = root.findViewById(R.id.progressbar_my_orders);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView = root.findViewById(R.id.recyclerview_my_orders);
        recyclerView.setVisibility(View.GONE);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ordersModelList = new ArrayList<>();
        ordersAdapter = new MyOrdersAdapter(getActivity(),ordersModelList);
        recyclerView.setAdapter(ordersAdapter);

        db.collection("CurrentUser").document(auth.getCurrentUser().getUid())
                .collection("MyOrder").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() > 0) {
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                            String documentId = documentSnapshot.getId();
                            MyOrdersModel ordersModel = documentSnapshot.toObject(MyOrdersModel.class);

                            ordersModel.setDocumentId(documentId);
                            ordersModelList.add(ordersModel);
                            ordersAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        myOrdersEmpty.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });


        return root;
    }
}