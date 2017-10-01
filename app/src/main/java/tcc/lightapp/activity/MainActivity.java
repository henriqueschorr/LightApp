package tcc.lightapp.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import tcc.lightapp.adapter.UserAdapter;
import tcc.lightapp.domain.User;

public class MainActivity extends BaseActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String userName;
    private static final String TAG = "Login";
    private List<User> availableUsers = new ArrayList<User>();
    protected RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;

    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        if(args != null) {
            userName = args.getString("nome");
        } else {
            userName = user.getDisplayName();
        }

        setAvailability(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getAvailableUsers();

        mUserAdapter = new UserAdapter(availableUsers, MainActivity.this);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mUserAdapter);


    }

//    private UserAdapter.UserOnClickListener onClickUser(){
//        return new UserAdapter.UserOnClickListener(){
//            @Override
//            public void onClickUser(View view, int idx){
//                User u = availableUsers.get(idx);
////                Intent intent = new Intent(getContext(), UserActivity.class);
////                intent.putExtra("carro", Parcels.wrap(c));
////                startActivity(intent);
//
//            }
//        };
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        setAvailability(false);
        mAuth.signOut();
        finish();
        return true;
    }

    public void setAvailability(boolean available){
        User updateUser = new User(userName, user.getEmail(), user.getUid());
        if(available){
            updateUser.setAvailable();
        } else {
            updateUser.setUnavailable();
        }
        updateUserDatabase(updateUser);
    }

    public void updateUserDatabase(User updateUser){
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + user.getUid(), updateUser);

        mDatabase.updateChildren(childUpdates);
    }

    public void getAvailableUsers(){
        DatabaseReference databaseReference = mDatabase.child("users").getRef();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                availableUsers = mUserAdapter.getUsers();
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    if(userSnapshot.child("available").getValue().toString()=="true"){
                        Log.d(TAG, userSnapshot.child("userName").getValue().toString() + " - " + userSnapshot.getKey() + " - " + userSnapshot.child("available").getValue().toString());
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



}
