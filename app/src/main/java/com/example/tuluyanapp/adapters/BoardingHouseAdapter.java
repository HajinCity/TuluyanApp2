package com.example.tuluyanapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tuluyanapp.R;
import com.example.tuluyanapp.fragments.EditBoardingHouseActivity;
import com.example.tuluyanapp.models.BoardingHouse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BoardingHouseAdapter extends RecyclerView.Adapter<BoardingHouseAdapter.ViewHolder> {

    private Context context;
    private List<BoardingHouse> boardingHouseList;

    public BoardingHouseAdapter(Context context, List<BoardingHouse> boardingHouseList) {
        this.context = context;
        this.boardingHouseList = boardingHouseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.items_posted_boarding_house, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BoardingHouse boardingHouse = boardingHouseList.get(position);

        // Set the static image and name
        holder.imageBoardingHouse.setImageResource(R.drawable.house1);
        holder.textBoardingHouseName.setText(boardingHouse.getTitle());

        // Navigate to EditBoardingHouseActivity
        holder.editIcon.setOnClickListener(v -> {
            String landlordId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Fetch landlordId
            Intent intent = new Intent(context, EditBoardingHouseActivity.class);
            intent.putExtra("landlordId", landlordId); // Pass landlordId
            intent.putExtra("boardingHouseId", boardingHouse.getId()); // Pass boardingHouseId
            context.startActivity(intent);
        });

        // Handle delete logic
        holder.deleteIcon.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Confirmation")
                    .setMessage("Are you sure you want to delete this boarding house?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String landlordUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        db.collection("LandlordCollection")
                                .document(landlordUID)
                                .collection("BoardingHouses")
                                .document(boardingHouse.getId())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    boardingHouseList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, boardingHouseList.size());
                                    Toast.makeText(context, "Boarding house deleted successfully", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to delete boarding house", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return boardingHouseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageBoardingHouse, editIcon, deleteIcon;
        TextView textBoardingHouseName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageBoardingHouse = itemView.findViewById(R.id.imageBoardingHouse);
            textBoardingHouseName = itemView.findViewById(R.id.textBoardingHouseName);
            editIcon = itemView.findViewById(R.id.editIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }
    }
}
