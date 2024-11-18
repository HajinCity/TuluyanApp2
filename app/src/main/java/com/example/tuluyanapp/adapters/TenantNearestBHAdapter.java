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
import com.example.tuluyanapp.models.NearestBoardingH;

import java.util.List;

public class TenantNearestBHAdapter extends RecyclerView.Adapter<TenantNearestBHAdapter.ViewHolder> {

    private Context context;
    private List<NearestBoardingH> boardingHouseList;

    public TenantNearestBHAdapter(Context context, List<NearestBoardingH> boardingHouseList) {
        this.context = context;
        this.boardingHouseList = boardingHouseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tenantnearestboardinghouse, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NearestBoardingH item = boardingHouseList.get(position);

        holder.textViewName.setText(item.getName());
        holder.textViewPrice.setText(item.getPrice());
        holder.textViewDistance.setText(item.getDistance());

        // Load the image using Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.house1) // Replace with your placeholder drawable
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return boardingHouseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName, textViewPrice, textViewDistance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewPrice = itemView.findViewById(R.id.textViewPrice);
            textViewDistance = itemView.findViewById(R.id.textViewDistance);
        }
    }
}
