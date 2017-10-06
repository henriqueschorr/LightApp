package tcc.lightapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tcc.lightapp.R;
import tcc.lightapp.activity.ChatActivity;
import tcc.lightapp.activity.MainActivity;
import tcc.lightapp.adapter.UserAdapter;
import tcc.lightapp.models.User;
import tcc.lightapp.utils.Constants;


public class IndividualFragment extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private static final String TAG = "Login";
    private List<User> availableUsers = new ArrayList<User>();
    protected RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;
    private View mFragmentView;

    public static IndividualFragment newInstance() {
        IndividualFragment fragment = new IndividualFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mFragmentView = inflater.inflate(R.layout.fragment_individual, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        mUserAdapter = new UserAdapter(availableUsers, mFragmentView.getContext(), onClickUser());
        mRecyclerView = (RecyclerView) mFragmentView.findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mFragmentView.getContext()));
        mRecyclerView.setAdapter(mUserAdapter);

        getAvailableUsers();
        return mFragmentView;
    }

    public void getAvailableUsers() {
        DatabaseReference databaseReference = mDatabase.child(Constants.ARG_USERS).getRef();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                availableUsers = mUserAdapter.getUsers();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (userSnapshot.child(Constants.ARG_USER_AVAILABLE).getValue().toString() == "true") {
                        Log.d(TAG, userSnapshot.child(Constants.ARG_USER_NAME).getValue().toString() + " - " + userSnapshot.getKey() + " - " + userSnapshot.child(Constants.ARG_USER_AVAILABLE).getValue().toString());
                        User availableUser = userSnapshot.getValue(User.class);
                        if (!availableUsers.contains(availableUser) && !availableUser.authID.equals(user.getUid())) {
                            mUserAdapter.addUser(availableUser);
                            mUserAdapter.notifyItemInserted(availableUsers.indexOf(availableUser));
                        }
                    } else {
                        User availableUser = userSnapshot.getValue(User.class);
                        if (availableUsers.contains(availableUser)) {
                            mUserAdapter.removeUser(availableUsers.indexOf(availableUser));
                            mUserAdapter.notifyItemRemoved(availableUsers.indexOf(availableUser));
                        }
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
                User user = availableUsers.get(idx);
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra(Constants.ARG_RECEIVER_NAME, user.userName);
                intent.putExtra(Constants.ARG_RECEIVER_EMAIL, user.email);
                intent.putExtra(Constants.ARG_RECEIVER_UID, user.authID);
                intent.putExtra(Constants.ARG_FIREBASE_TOKEN, user.firebaseToken);
                intent.putExtra(Constants.ARG_INDIVIDUAL, true);
                startActivity(intent);
            }
        };
    }
}
