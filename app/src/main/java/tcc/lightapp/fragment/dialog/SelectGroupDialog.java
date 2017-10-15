package tcc.lightapp.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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
import tcc.lightapp.adapter.GroupsAdapter;
import tcc.lightapp.fragment.GroupsFragment;
import tcc.lightapp.models.Event;
import tcc.lightapp.models.GroupRoom;
import tcc.lightapp.models.User;
import tcc.lightapp.utils.Constants;

/**
 * Created by Henrique on 15/10/2017.
 */

public class SelectGroupDialog extends DialogFragment {
    private View mView;
    protected RecyclerView mRecyclerView;
    private GroupsAdapter mGroupAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private String mEventName;
    private String mEventLocation;
    private String mEventDate;
    private String mEventTime;

    private List<GroupRoom> groupRooms = new ArrayList<GroupRoom>();
    private String groupSelected;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Bundle mArgs = getArguments();

        mEventName = mArgs.getString(Constants.ARG_EVENT_NAME);
        mEventLocation = mArgs.getString(Constants.ARG_EVENT_LOCATION);
        mEventDate = mArgs.getString(Constants.ARG_EVENT_DATE);
        mEventTime = mArgs.getString(Constants.ARG_EVENT_TIME);

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
         LayoutInflater inflater = LayoutInflater.from(getActivity());
        mView = (View) inflater.inflate(R.layout.dialog_select_group, null);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        mGroupAdapter = new GroupsAdapter(groupRooms, mView.getContext(), onClickGroup());
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));
        mRecyclerView.setAdapter(mGroupAdapter);

        getGroupRooms();

//        setClickListener();

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.select_group)
                .setView(mView)
                .setPositiveButton(R.string.action_confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
//                                for(User friend : friendsSelected) {
                                Event event = new Event(groupSelected, user.getUid(), user.getDisplayName(), user.getEmail(), mEventName, mEventLocation, mEventDate, mEventTime);
                                mDatabase.child(Constants.ARG_EVENTS).child(groupSelected).setValue(event);
                                getGroupMembers();
//                                }
                                dialog.dismiss();
                                getActivity().finish();
                            }
                        }
                ).setNegativeButton(R.string.action_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).create();
    }

    public void getGroupRooms() {
        DatabaseReference databaseReference = mDatabase.child(Constants.ARG_GROUPS).getRef();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupRooms = mGroupAdapter.getGroupRooms();
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    GroupRoom groupRoom = groupSnapshot.getValue(GroupRoom.class);
                    if (!groupRooms.contains(groupRoom)) {
                        if ((groupRoom.isPrivate && groupRoom.membersUid.containsKey(user.getUid())) || !groupRoom.isPrivate) {
                            mGroupAdapter.addGroupRoom(groupRoom);
                            mGroupAdapter.notifyItemInserted(groupRooms.indexOf(groupRoom));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError onClickGroup) {

            }
        });
    }

    private GroupsAdapter.GroupOnClickListener onClickGroup() {
        return new GroupsAdapter.GroupOnClickListener() {
            @Override
            public void onClickGroup(View view, int idx) {
                GroupRoom groupRoom = groupRooms.get(idx);
                groupSelected = groupRoom.groupKey;
            }
        };
    }

    public void getGroupMembers() {
        DatabaseReference groupsDatabase = mDatabase.child(Constants.ARG_GROUPS);
        final DatabaseReference EventInvitedDatabase = mDatabase.child(Constants.ARG_EVENTS).child(groupSelected).child(Constants.ARG_INVITED_USERS);

        groupsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    GroupRoom groupRoom = groupSnapshot.getValue(GroupRoom.class);
                    for (String memberKey : groupRoom.membersUid.keySet()) {
                        EventInvitedDatabase.child(memberKey).setValue(groupRoom.membersUid.get(memberKey));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
