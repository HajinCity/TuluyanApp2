package com.example.tuluyanapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuluyanapp.R;
import com.example.tuluyanapp.models.TenantRequestModel;

import java.util.List;

public class TenantRequestApplicationAdapter extends RecyclerView.Adapter<TenantRequestApplicationAdapter.ViewHolder> {

    private final List<TenantRequestModel> tenantRequests;

    public TenantRequestApplicationAdapter(List<TenantRequestModel> tenantRequests) {
        this.tenantRequests = tenantRequests;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_messnotif, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TenantRequestModel request = tenantRequests.get(position);
        holder.nameTextView.setText(request.getFirstName() + " " + request.getLastName());
        holder.statusTextView.setText(request.getStatus());
    }

    @Override
    public int getItemCount() {
        return tenantRequests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, statusTextView;
        ImageView moreOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            moreOptions = itemView.findViewById(R.id.moreOptions);
        }
    }
}
