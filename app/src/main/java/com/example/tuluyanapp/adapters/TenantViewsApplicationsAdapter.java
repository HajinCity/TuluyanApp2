package com.example.tuluyanapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuluyanapp.R;
import com.example.tuluyanapp.models.TenantViewApplicationModel;

import java.util.List;

public class TenantViewsApplicationsAdapter extends RecyclerView.Adapter<TenantViewsApplicationsAdapter.ViewHolder> {

    private final List<TenantViewApplicationModel> applicationsList;

    public TenantViewsApplicationsAdapter(List<TenantViewApplicationModel> applicationsList) {
        this.applicationsList = applicationsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tenan_view_application, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TenantViewApplicationModel application = applicationsList.get(position);

        holder.apartmentName.setText(application.getApartmentName());
        holder.price.setText(application.getPrice());
        holder.status.setText(application.getStatus());
    }

    @Override
    public int getItemCount() {
        return applicationsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView apartmentName, price, status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            apartmentName = itemView.findViewById(R.id.apartmentName);
            price = itemView.findViewById(R.id.price);
            status = itemView.findViewById(R.id.status);
        }
    }
}
