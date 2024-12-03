package com.example.tuluyanapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuluyanapp.R;
import com.example.tuluyanapp.fragments.TenantViewsBoardingHouse;
import com.example.tuluyanapp.models.TenantSearchedModel;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private final Context context;
    private List<TenantSearchedModel> searchResults;

    public SearchResultAdapter(Context context, List<TenantSearchedModel> searchResults) {
        this.context = context;
        this.searchResults = searchResults;
    }

    public void updateData(List<TenantSearchedModel> newResults) {
        this.searchResults = newResults;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TenantSearchedModel result = searchResults.get(position);

        holder.title.setText(result.getTitle());
        holder.price.setText(String.format("PHP %s/  - %s", result.getPrice(), result.getSelectionOption()));
        holder.owner.setText(result.getOwnerName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TenantViewsBoardingHouse.class);
            intent.putExtra("BOARDING_HOUSE_ID", result.getBoardingHouseId());
            intent.putExtra("ownerName", result.getOwnerName());
            intent.putExtra("paymentOption", result.getSelectionOption());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, owner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTitle);
            price = itemView.findViewById(R.id.itemPrice);
            owner = itemView.findViewById(R.id.itemOwner);
        }
    }
}
