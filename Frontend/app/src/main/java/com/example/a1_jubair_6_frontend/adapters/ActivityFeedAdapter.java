package com.example.a1_jubair_6_frontend.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.models.ActivityFeedItem;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ActivityFeedAdapter extends RecyclerView.Adapter<ActivityFeedAdapter.ViewHolder> {
    private List<ActivityFeedItem> allActivityItems = new ArrayList<>();
    private List<ActivityFeedItem> filteredActivityItems = new ArrayList<>();
    private Context context;
    private boolean showFood = true;
    private boolean showGroups = true;
    private boolean showAchievements = true;
    private boolean showGoals = true;

    public ActivityFeedAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_feed, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityFeedItem item = filteredActivityItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return filteredActivityItems.size();
    }

    public void addItem(ActivityFeedItem item) {
        allActivityItems.add(0, item);
        if (shouldShowItem(item)) {
            filteredActivityItems.add(0, item);
            notifyItemInserted(0);
        }
    }

    public void setItems(List<ActivityFeedItem> items) {
        allActivityItems = new ArrayList<>(items);
        applyFilters(showFood, showGroups, showAchievements, showGoals);
    }

    public void applyFilters(boolean showFood, boolean showGroups,
                             boolean showAchievements, boolean showGoals) {
        this.showFood = showFood;
        this.showGroups = showGroups;
        this.showAchievements = showAchievements;
        this.showGoals = showGoals;

        filteredActivityItems = allActivityItems.stream()
                .filter(this::shouldShowItem)
                .collect(Collectors.toList());

        notifyDataSetChanged();
    }

    private boolean shouldShowItem(ActivityFeedItem item) {
        switch (item.getType()) {
            case FOOD_EATEN:
                return showFood;
            case GROUP_UPDATE:
                return showGroups;
            case ACHIEVEMENT:
                return showAchievements;
            case GOAL_UPDATE:
                return showGoals;
            default:
                return true;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView activityTypeIcon;
        TextView activityTypeLabel;
        TextView timestampText;
        TextView activityMessage;
        TextView activityDetails;
        MaterialCardView detailsCard;

        ViewHolder(View itemView) {
            super(itemView);
            activityTypeIcon = itemView.findViewById(R.id.activityTypeIcon);
            activityTypeLabel = itemView.findViewById(R.id.activityTypeLabel);
            timestampText = itemView.findViewById(R.id.timestampText);
            activityMessage = itemView.findViewById(R.id.activityMessage);
            activityDetails = itemView.findViewById(R.id.activityDetails);
            detailsCard = itemView.findViewById(R.id.detailsCard);
        }

        void bind(ActivityFeedItem item) {
            int iconRes;
            String typeLabel;
            switch (item.getType()) {
                case FOOD_EATEN:
                    iconRes = R.drawable.ic_food;
                    typeLabel = "Food Activity";
                    break;
                case GROUP_UPDATE:
                    iconRes = R.drawable.ic_group;
                    typeLabel = "Group Update";
                    break;
                case ACHIEVEMENT:
                    iconRes = R.drawable.ic_achievement;
                    typeLabel = "Achievement";
                    break;
                case GOAL_UPDATE:
                    iconRes = R.drawable.goals_icon;
                    typeLabel = "Goal Update";
                    break;
                default:
                    iconRes = R.drawable.ic_default;
                    typeLabel = "Activity";
            }

            activityTypeIcon.setImageResource(iconRes);
            activityTypeLabel.setText(typeLabel);
            timestampText.setText(item.getTimestamp().toString());
            activityMessage.setText(item.getMessage());

            if (item.getAdditionalData() != null && !item.getAdditionalData().isEmpty()) {
                activityDetails.setText(item.getAdditionalData());
                detailsCard.setVisibility(View.VISIBLE);
            } else {
                detailsCard.setVisibility(View.GONE);
            }
        }
    }
}
