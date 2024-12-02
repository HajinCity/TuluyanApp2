package com.example.tuluyanapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuluyanapp.R;
import com.example.tuluyanapp.fragments.OwnerViewsTenantApplication;
import com.example.tuluyanapp.models.TenantRequestModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TenantRequestApplicationAdapter extends RecyclerView.Adapter<TenantRequestApplicationAdapter.ViewHolder> {

    private final List<TenantRequestModel> tenantRequests;
    private final Context context;

    public TenantRequestApplicationAdapter(Context context, List<TenantRequestModel> tenantRequests) {
        this.context = context;
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

        // Set tenant request data
        holder.nameTextView.setText(request.getFirstName() + " " + request.getLastName());
        holder.statusTextView.setText(request.getStatus());

        // More options button
        holder.moreOptions.setOnClickListener(v -> showPopupMenu(v, position));
    }

    @Override
    public int getItemCount() {
        return tenantRequests.size();
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.more_options_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> handleMenuClick(item, position));
        popupMenu.show();
    }

    private boolean handleMenuClick(MenuItem item, int position) {
        TenantRequestModel request = tenantRequests.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        int itemId = item.getItemId();

        if (itemId == R.id.view_request) {
            // Navigate to OwnerViewsTenantApplication
            Intent intent = new Intent(context, OwnerViewsTenantApplication.class);
            intent.putExtra("occupantId", request.getId());
            intent.putExtra("firstName", request.getFirstName());
            intent.putExtra("lastName", request.getLastName());
            intent.putExtra("status", request.getStatus());
            intent.putExtra("boardingHouseId", request.getBoardingHouseId());
            intent.putExtra("landlordId", request.getLandlordId());
            context.startActivity(intent);
            return true;

        }else if (itemId == R.id.approve) {
            // Approve request
            db.collection("LandlordCollection").document(request.getLandlordId())
                    .collection("BoardingHouses").document(request.getBoardingHouseId())
                    .collection("Occupants").document(request.getId())
                    .update("status", "Approved")
                    .addOnSuccessListener(unused -> {
                        // Also update TenantCollection with new structure
                        db.collection("TenantCollection").document(request.getId()) // Use request.getId() for tenant ID
                                .collection("RentedBoardingHouse").document(request.getLandlordId()) // Use landlordId as the document ID
                                .update("status", "Approved")
                                .addOnSuccessListener(tenantUpdateUnused -> {
                                    Toast.makeText(context, "Request Approved", Toast.LENGTH_SHORT).show();
                                    tenantRequests.remove(position);
                                    notifyItemRemoved(position);
                                })
                                .addOnFailureListener(tenantUpdateError -> {
                                    Log.e("Error", "Failed to update TenantCollection: " + tenantUpdateError.getMessage());
                                    Toast.makeText(context, "Failed to update TenantCollection.", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to approve request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            return true;

        } else if (itemId == R.id.reject) {
            // Reject request
            db.collection("LandlordCollection").document(request.getLandlordId())
                    .collection("BoardingHouses").document(request.getBoardingHouseId())
                    .collection("Occupants").document(request.getId())
                    .update("status", "Rejected")
                    .addOnSuccessListener(unused -> {
                        // Also update TenantCollection with new structure
                        db.collection("TenantCollection").document(request.getId()) // Use request.getId() for tenant ID
                                .collection("RentedBoardingHouse").document(request.getLandlordId()) // Use landlordId as the document ID
                                .update("status", "Rejected")
                                .addOnSuccessListener(tenantUpdateUnused -> {
                                    Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show();
                                    tenantRequests.remove(position);
                                    notifyItemRemoved(position);
                                })
                                .addOnFailureListener(tenantUpdateError -> {
                                    Log.e("Error", "Failed to update TenantCollection: " + tenantUpdateError.getMessage());
                                    Toast.makeText(context, "Failed to update TenantCollection.", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Failed to reject request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            return true;
        }
        else {
            return false;
        }
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
