package com.bill.cashmanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bill.cashmanager.R;
import com.bill.cashmanager.models.MyOrdersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {

    Context context;
    List<MyOrdersModel> ordersModelList;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    public MyOrdersAdapter(Context context, List<MyOrdersModel> ordersModelList) {
        this.context = context;
        this.ordersModelList = ordersModelList;
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.my_orders_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(ordersModelList.get(position).getProductName());
        holder.price.setText(ordersModelList.get(position).getProductPrice());
        holder.date.setText(ordersModelList.get(position).getCurrentDate());
        holder.time.setText(ordersModelList.get(position).getCurrentTime());
        holder.quantity.setText(ordersModelList.get(position).getTotalQuantity());
        holder.totalPrice.setText(String.valueOf(ordersModelList.get(position).getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return ordersModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,price,date,time,quantity,totalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.product_name_oders);
            price = itemView.findViewById(R.id.product_price_oders);
            date = itemView.findViewById(R.id.current_date_oders);
            time = itemView.findViewById(R.id.current_time_oders);
            quantity = itemView.findViewById(R.id.total_quantity_oders);
            totalPrice = itemView.findViewById(R.id.total_price_oders);
        }
    }
}
