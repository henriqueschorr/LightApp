package tcc.lightapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tcc.lightapp.R;
import tcc.lightapp.adapter.UserAdapter;
import tcc.lightapp.fcm.MyFirebaseInstanceIDService;
import tcc.lightapp.models.User;
import tcc.lightapp.utils.Constants;

public class MainActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private static final String TAG = "Login";
    private List<User> availableUsers = new ArrayList<User>();
    protected RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        setAvailability(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserAdapter = new UserAdapter(availableUsers, MainActivity.this, onClickUser());
        mRecyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mUserAdapter);

        getAvailableUsers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        setAvailability(false);
        mAuth.signOut();
        finish();
        return true;
    }

    public void setAvailability(boolean available) {
        Map<String, Object> childUpdates = new HashMap<>();

        if (available) {
            childUpdates.put("/" + Constants.ARG_USERS + "/" + user.getUid() + "/" + Constants.ARG_USER_AVAILABLE, true);
        } else {
            childUpdates.put("/" + Constants.ARG_USERS + "/" + user.getUid() + "/"  + Constants.ARG_USER_AVAILABLE, false);
        }

        mDatabase.updateChildren(childUpdates);
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
                        if (!availableUsers.contains(availableUser) && !availableUser.getAuthID().equals(user.getUid())) {
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
                intent.putExtra(Constants.ARG_RECEIVER, user.getEmail());
                intent.putExtra(Constants.ARG_RECEIVER_UID, user.getAuthID());
                intent.putExtra(Constants.ARG_FIREBASE_TOKEN, user.getFirebaseToken());
                startActivity(intent);
            }
        };
    }


}
