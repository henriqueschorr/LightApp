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
import tcc.lightapp.adapter.UserAdapter;
import tcc.lightapp.models.User;
import tcc.lightapp.utils.Constants;

/**
 * Created by Henrique on 07/10/2017.
 */

public class AddMemberDialog extends DialogFragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private UserAdapter mFriendAdapter;
    protected RecyclerView mRecyclerView;
    private List<User> friends = new ArrayList<User>();
    private List<User> friendsSelected = new ArrayList<User>();
    private String groupKey;
    private static final String TAG = "AddMember";
    private View mView;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        Bundle mArgs = getArguments();
        groupKey = mArgs.getString(Constants.ARG_GROUP_KEY);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        mView = (View) inflater.inflate(R.layout.dialog_add_member, null);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        mFriendAdapter = new UserAdapter(friends, mView.getContext(), onClickUser());
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mView.getContext()));
        mRecyclerView.setAdapter(mFriendAdapter);

        getFriends();

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_member)
                .setView(mView)
                .setPositiveButton(R.string.action_confirm,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                for(User friend : friendsSelected) {
                                    mDatabase.child(Constants.ARG_GROUPS).child(groupKey).child(Constants.ARG_GROUP_MEMBER).child(friend.authID).setValue(Constants.ARG_DEFAULT_VALUE);
                                }
                                dialog.dismiss();
                            }
                        }
                ).setNegativeButton(R.string.action_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).create();
    }

    public void getFriends() {
        final DatabaseReference userDatabase = mDatabase.child(Constants.ARG_USERS);
        DatabaseReference friendDatabase = userDatabase.child(user.getUid()).child(Constants.ARG_FRIENDS);

        friendDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friends = mFriendAdapter.getUsers();
                for (DataSnapshot friendSnapshot : dataSnapshot.getChildren()) {
                    String friendUid = friendSnapshot.getKey();
                    String[] friendData = friendSnapshot.getValue().toString().split("_");
                    String friendName = friendData[0];
                    String friendEmail = friendData[1];

                    User userFriend = new User(friendName, friendEmail, friendUid);
                    if (!friends.contains(userFriend)) {
                        mFriendAdapter.addUser(userFriend);
                        mFriendAdapter.notifyItemInserted(friends.indexOf(userFriend));

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private UserAdapter.UserOnClickListener onClickUser() {
        return new UserAdapter.UserOnClickListener() {
            @Override
            public void onClickUser(View view, int idx) {
                User friend = friends.get(idx);
                friendsSelected.add(friend);
            }
        };
    }

}
