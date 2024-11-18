package com.example.tuluyanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tuluyanapp.R;
import com.example.tuluyanapp.models.ListingBHModels;

import java.util.List;

public class ListingBHAdapters extends RecyclerView.Adapter<ListingBHAdapters.ViewHolder> {

    private Context context;
    private List<ListingBHModels> houseList;

    public ListingBHAdapters(Context context, List<ListingBHModels> houseList) {
        this.context = context;
        this.houseList = houseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listingbhitems, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListingBHModels house = houseList.get(position);

        holder.textViewName.setText(house.getName());
        holder.textViewPrice.setText(house.getPrice());

        // Load the image using Glide
        Glide.with(context)
                .load(house.getImageUrl())
                .placeholder(R.drawable.house1) // Replace with a proper placeholder drawable
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return houseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName, textViewPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewListing);
            textViewName = itemView.findViewById(R.id.textViewListingName);
            textViewPrice = itemView.findViewById(R.id.textViewListingPrice);
        }
    }
}
