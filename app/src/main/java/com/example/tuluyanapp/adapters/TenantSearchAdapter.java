package com.example.tuluyanapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuluyanapp.R;
import com.example.tuluyanapp.models.TenantSearchedModel;

import java.util.List;

public class TenantSearchAdapter extends RecyclerView.Adapter<TenantSearchAdapter.SliderViewHolder> {

    private final Context context;
    private final List<TenantSearchedModel> tenantList;

    public TenantSearchAdapter(Context context, List<TenantSearchedModel> tenantList) {
        this.context = context;
        this.tenantList = tenantList;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.slider_searched_items, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        TenantSearchedModel model = tenantList.get(position);

        // Bind data to the views
        holder.ownerName.setText(model.getOwnerName()); // Owner's full name
        holder.propertyName.setText(model.getTitle()); // Boarding house title
        holder.propertyPrice.setText("₱" + model.getPrice() + " (" + model.getSelectionOption() + ")"); // Combined price and selection option
    }

    @Override
    public int getItemCount() {
        return tenantList.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        TextView ownerName, propertyName, propertyPrice;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);

            ownerName = itemView.findViewById(R.id.ownerName);
            propertyName = itemView.findViewById(R.id.propertyName);
            propertyPrice = itemView.findViewById(R.id.propertyPrice);
        }
    }
}
