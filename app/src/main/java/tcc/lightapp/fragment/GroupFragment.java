package tcc.lightapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import tcc.lightapp.R;
import tcc.lightapp.activity.ChatActivity;
import tcc.lightapp.adapter.GroupAdapter;
import tcc.lightapp.fragment.dialog.CreateGroupDialog;
import tcc.lightapp.models.GroupRoom;
import tcc.lightapp.models.User;
import tcc.lightapp.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupFragment extends Fragment {
    private View mFragmentView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private List<GroupRoom> groupRooms = new ArrayList<GroupRoom>();
    private GroupAdapter mGroupAdapter;
    protected RecyclerView mRecyclerView;

    public static GroupFragment newInstance() {
        GroupFragment fragment = new GroupFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_group, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        mGroupAdapter = new GroupAdapter(groupRooms, mFragmentView.getContext(), onClickGroup());
        mRecyclerView = (RecyclerView) mFragmentView.findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mFragmentView.getContext()));
        mRecyclerView.setAdapter(mGroupAdapter);

        getGroupRooms();

        setClickListener();

        return mFragmentView;
//        TODO: Create onClick event
    }

    public void setClickListener() {
        FloatingActionButton createGroupButton = (FloatingActionButton) mFragmentView.findViewById(R.id.create_group);

        createGroupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CreateGroupDialog.showDialog(getFragmentManager());
            }
        });
    }

    public void getGroupRooms() {
        DatabaseReference databaseReference = mDatabase.child(Constants.ARG_GROUPS).getRef();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupRooms = mGroupAdapter.getGroupRooms();
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    GroupRoom groupRoom = groupSnapshot.getValue(GroupRoom.class);
                    if(!groupRooms.contains(groupRoom)) {
                        mGroupAdapter.addGroupRoom(groupRoom);
                        mGroupAdapter.notifyItemInserted(groupRooms.indexOf(groupRoom));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private GroupAdapter.GroupOnClickListener onClickGroup() {
        return new GroupAdapter.GroupOnClickListener() {
            @Override
            public void onClickGroup(View view, int idx) {
                GroupRoom groupRoom = groupRooms.get(idx);
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra(Constants.ARG_GROUP_NAME, groupRoom.groupName);
                intent.putExtra(Constants.ARG_GROUP_KEY, groupRoom.groupKey);
                intent.putExtra(Constants.ARG_INDIVIDUAL, false);
//                intent.putExtra(Constants.ARG_FIREBASE_TOKEN, user.firebaseToken);
                startActivity(intent);
            }
        };
    }
}
