package com.example.a1_jubair_6_frontend.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.models.Menu;

import java.text.SimpleDateFormat;
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
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        Menu menu = menuList.get(position);

        String displayText = String.format("%s - %s",
                menu.getLocation(),
                menu.getDate() != null ? menu.getDate() : "No Date");

        holder.textView.setText(displayText);

        holder.textView.setBackgroundColor(selectedPosition == position ?
                holder.itemView.getContext().getColor(R.color.Iowa_State_Red) :
                Color.TRANSPARENT);

        holder.textView.setTextColor(selectedPosition == position ?
                Color.WHITE :
                Color.BLACK);
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
        TextView textView;

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = (TextView) itemView;
            textView.setPadding(32, 24, 32, 24);

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