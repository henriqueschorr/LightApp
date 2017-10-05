package tcc.lightapp.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tcc.lightapp.R;
import tcc.lightapp.models.GroupRoom;

/**
 * Created by Henrique on 05/10/2017.
 */

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupsViewHolder>{
    private List<GroupRoom> groupRooms;
    private Context context;
//    private GroupOnClickListener groupOnClickListener;

    public GroupAdapter(List<GroupRoom> groupRooms, Context context) {
        this.groupRooms = groupRooms;
        this.context = context;
//        this.groupOnClickListener = groupOnClickListener;
    }

    @Override
    public int getItemCount() {
        return this.groupRooms != null ? this.groupRooms.size() : 0;
    }

    @Override
    public GroupsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_groups, viewGroup, false);
        GroupsViewHolder holder = new GroupsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final GroupsViewHolder holder, final int position) {
        GroupRoom groupRoom = groupRooms.get(position);

        holder.groupName.setText(groupRoom.groupName);

//        if (groupOnClickListener != null) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    groupOnClickListener.onClickGroup(holder.itemView, position);
//                }
//            });
//        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void addGroupRoom(GroupRoom groupRoom) {
        groupRooms.add(groupRoom);
    }

    public void removeGroupRoom(int position) {
        groupRooms.remove(position);
    }

    public List<GroupRoom> getGroupRooms() {
        return groupRooms;
    }

    public interface GroupOnClickListener {
        void onClickGroup(View view, int idx);
    }

    public static class GroupsViewHolder extends RecyclerView.ViewHolder {
        public TextView groupName;

        public GroupsViewHolder(View view) {
            super(view);
            groupName = (TextView) view.findViewById(R.id.group_name);
        }
    }
}
