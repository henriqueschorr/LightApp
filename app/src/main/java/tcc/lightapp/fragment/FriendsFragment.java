package tcc.lightapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import tcc.lightapp.activity.ChatActivity;
import tcc.lightapp.adapter.UserAdapter;
import tcc.lightapp.fragment.dialog.AddFriendDialog;
import tcc.lightapp.models.User;
import tcc.lightapp.utils.Constants;

public class FriendsFragment extends Fragment {
    private View mFragmentView;
    protected RecyclerView mRecyclerView;
    private UserAdapter mFriendAdapter;
    private ProgressBar mProgressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    private List<User> friends = new ArrayList<User>();

    private static final String TAG = "FriendList";

    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_friends, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        mProgressBar = (ProgressBar) mFragmentView.findViewById(R.id.progress_bar);

        mFriendAdapter = new UserAdapter(friends, mFragmentView.getContext(), onClickUser());
        mRecyclerView = (RecyclerView) mFragmentView.findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mFragmentView.getContext()));
        mRecyclerView.setAdapter(mFriendAdapter);

        getFriends();

        setClickListener();

        return mFragmentView;
    }

    public void getFriends() {
        final DatabaseReference userDatabase = mDatabase.child(Constants.ARG_USERS);
        DatabaseReference friendDatabase = userDatabase.child(user.getUid()).child(Constants.ARG_FRIENDS);

        mProgressBar.setVisibility(View.VISIBLE);

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
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void setClickListener() {
        FloatingActionButton addFriendButton = (FloatingActionButton) mFragmentView.findViewById(R.id.add_friend);

        addFriendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AddFriendDialog.showDialog(getFragmentManager());
            }
        });
    }

    private UserAdapter.UserOnClickListener onClickUser() {
        return new UserAdapter.UserOnClickListener() {
            @Override
            public void onClickUser(View view, int idx) {
                User user = friends.get(idx);
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra(Constants.ARG_RECEIVER_NAME, user.userName);
                intent.putExtra(Constants.ARG_RECEIVER_EMAIL, user.email);
                intent.putExtra(Constants.ARG_RECEIVER_UID, user.authID);
                intent.putExtra(Constants.ARG_FIREBASE_TOKEN, user.firebaseToken);
                intent.putExtra(Constants.ARG_INDIVIDUAL, true);
                intent.putExtra(Constants.ARG_FRIEND, true);
                startActivity(intent);
            }
        };
    }
}
