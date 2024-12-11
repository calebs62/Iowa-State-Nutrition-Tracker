package com.example.a1_jubair_6_frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.models.Menu;

import java.util.List;

public class MenuSelectionAdapter extends RecyclerView.Adapter<MenuSelectionAdapter.MenuViewHolder> {
    private List<Menu> menuList;
    private int selectedPosition = -1;
    private OnMenuSelectedListener listener;

    public interface OnMenuSelectedListener {
        void onMenuSelected(int position, Menu menu);
    }

    public MenuSelectionAdapter(List<Menu> menuList, OnMenuSelectedListener listener) {
        this.menuList = menuList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_selection, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Menu menu = menuList.get(position);
        boolean isSelected = selectedPosition == position;

        String locationText = String.format("%s - %s",
                menu.getLocation() != null ? menu.getLocation() : "",
                menu.getMeal() != null ? menu.getMeal() : "");
        holder.locationView.setText(locationText);

        String dateText = menu.getDate() != null ? menu.getDate() : "No Date";
        holder.dateView.setText(dateText);

        holder.locationView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                isSelected ? android.R.color.white : android.R.color.black));
        holder.dateView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),
                isSelected ? android.R.color.white : android.R.color.darker_gray));

        holder.itemView.setSelected(isSelected);
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(selectedPosition);
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        final TextView locationView;
        final TextView dateView;

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            locationView = itemView.findViewById(R.id.tvLocation);
            dateView = itemView.findViewById(R.id.tvDate);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    setSelectedPosition(position);
                    listener.onMenuSelected(position, menuList.get(position));
                }
            });
        }
    }
}