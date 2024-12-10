package com.example.a1_jubair_6_frontend.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a1_jubair_6_frontend.R;
import com.example.a1_jubair_6_frontend.managers.ProfileDataManager;
import com.example.a1_jubair_6_frontend.models.GroupMember;

import java.util.ArrayList;
import java.util.List;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.MemberViewHolder> {
    private final List<GroupMember> members = new ArrayList<>();
    private ProfileDataManager profileDataManager;

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        GroupMember member = members.get(position);
        holder.memberName.setText(member.getUser().getFname() + " " + member.getUser().getLname());

        String role = member.getPermissionLvlString();
        holder.memberRole.setText(role);
        holder.memberRole.setTextColor(getRoleColor(member.getPermissionLvl()));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void updateMembers(List<GroupMember> newMembers) {
        members.clear();
        members.addAll(newMembers);
        notifyDataSetChanged();
    }

    private int getRoleColor(int permissionLevel) {
        switch (permissionLevel) {
            case 4: return 0xFF8802CE; //Admin - Purple
            case 3: return 0xFF00E209; //Contributor -
            case 2: return 0xFFFF6B6B; // Owner - Red
            case 1: return 0xFF4ECDC4; // Moderator - Teal
            default: return 0xFF95A5A6; // Member - Gray
        }
    }

    static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView memberName;
        TextView memberRole;

        MemberViewHolder(View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.memberName);
            memberRole = itemView.findViewById(R.id.memberRole);
        }
    }
}
