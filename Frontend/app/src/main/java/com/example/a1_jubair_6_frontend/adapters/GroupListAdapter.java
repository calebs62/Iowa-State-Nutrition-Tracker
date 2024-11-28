package com.example.a1_jubair_6_frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1_jubair_6_frontend.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder> {

    private final List<JSONObject> groups = new ArrayList<>();
    private final OnGroupJoinClickListener listener;

    public interface OnGroupJoinClickListener {
        void onGroupJoinClicked(int groupId);
        void showPlanDetails(JSONObject plan);
    }

    public GroupListAdapter(OnGroupJoinClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        try {
            JSONObject group = groups.get(position);
            String groupName = group.optString("groupName", "Unnamed Group");
            holder.groupName.setText(groupName);

            // Get member count
            JSONArray members = group.getJSONArray("members");
            holder.memberCount.setText(holder.itemView.getContext()
                    .getString(R.string.member_count, members.length()));

            // Set plan name if available
            if (!group.isNull("plan")) {
                JSONObject plan = group.getJSONObject("plan");
                holder.planName.setText(plan.getString("name"));
                holder.planName.setVisibility(View.VISIBLE);

                holder.infoButton.setVisibility(View.VISIBLE);
                holder.infoButton.setOnClickListener(v -> listener.showPlanDetails(plan));
            } else {
                holder.planName.setVisibility(View.GONE);
                holder.infoButton.setVisibility(View.GONE);
            }

            // Set plan name if available
            if (!group.isNull("plan")) {
                JSONObject plan = group.getJSONObject("plan");
                holder.planName.setText(plan.getString("name"));
                holder.planName.setVisibility(View.VISIBLE);
            } else {
                holder.planName.setVisibility(View.GONE);
            }

            holder.itemView.setVisibility(groupName != null && !groupName.isEmpty() ?
                    View.VISIBLE : View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    groupName != null && !groupName.isEmpty() ?
                            ViewGroup.LayoutParams.WRAP_CONTENT : 0));

            holder.joinButton.setOnClickListener(v -> {
                try {
                    listener.onGroupJoinClicked(group.getInt("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void updateGroups(JSONArray newGroups) {
        groups.clear();
        for (int i = 0; i < newGroups.length(); i++) {
            try {
                groups.add(newGroups.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;
        TextView memberCount;
        TextView planName;
        MaterialButton joinButton;
        MaterialButton infoButton;

        GroupViewHolder(View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.groupName);
            memberCount = itemView.findViewById(R.id.memberCount);
            planName = itemView.findViewById(R.id.planName);
            joinButton = itemView.findViewById(R.id.joinButton);
            infoButton = itemView.findViewById(R.id.infoButton);
        }
    }
}