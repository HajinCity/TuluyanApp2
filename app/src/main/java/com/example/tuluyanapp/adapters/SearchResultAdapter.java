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

        // Set property title
        holder.title.setText(result.getTitle());

        // Set price and option in one line
        holder.price.setText(String.format("PHP %s/  - %s", result.getPrice(), result.getSelectionOption()));

        // Set owner name
        holder.owner.setText(result.getOwnerName());
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, owner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.itemTitle); // Property title
            price = itemView.findViewById(R.id.itemPrice); // Price and option combined
            owner = itemView.findViewById(R.id.itemOwner); // Owner's name
        }
    }
}
