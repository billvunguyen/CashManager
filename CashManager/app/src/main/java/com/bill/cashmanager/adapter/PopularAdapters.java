package com.bill.cashmanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bill.cashmanager.R;
import com.bill.cashmanager.activities.ViewAllActivity;
import com.bill.cashmanager.models.PopularModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class PopularAdapters extends RecyclerView.Adapter<PopularAdapters.ViewHolder> {

    private Context context;
    private List<PopularModel> popularModelsList;

    public PopularAdapters(Context context, List<PopularModel> popularModelsList) {
        this.context = context;
        this.popularModelsList = popularModelsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.popular_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(popularModelsList.get(position).getImg_url()).into(holder.popImg);
        holder.name.setText(popularModelsList.get(position).getName());        holder.name.setText(popularModelsList.get(position).getName());
        holder.rating.setText(popularModelsList.get(position).getRating());
        holder.description.setText(popularModelsList.get(position).getDescription());
        holder.discount.setText(popularModelsList.get(position).getDiscount());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewAllActivity.class);
                intent.putExtra("type",popularModelsList.get(position).getType());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return popularModelsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView popImg;
        TextView name,description,rating,discount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            popImg = itemView.findViewById(R.id.pop_img);
            name = itemView.findViewById(R.id.pop_name);
            description = itemView.findViewById(R.id.pop_des);
            discount = itemView.findViewById(R.id.pop_discount);
            rating = itemView.findViewById(R.id.pop_rating);
        }
    }
}
