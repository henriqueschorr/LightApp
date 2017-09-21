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

//        TextView mNome = (TextView) findViewById(R.id.nome);
//
//        mNome.setText("Bem vindo " + userName);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getAvailableUsers();

        mUserAdapter = new UserAdapter(availableUsers, MainActivity.this);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setAdapter(mUserAdapter);

        Button view = (Button) findViewById(R.id.view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setAdapter(mUserAdapter);





            }
        });

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
        User updateUser = new User(userName, user.getEmail());
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
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    if(userSnapshot.child("available").getValue().toString()=="true"){
                        Log.d(TAG, userSnapshot.child("userName").getValue().toString() + " - " + userSnapshot.getKey() + " - " + userSnapshot.child("available").getValue().toString());
                        User availableUser = userSnapshot.getValue(User.class);
                        availableUsers.add(availableUser);
                        mUserAdapter.notifyItemInserted(availableUsers.size() - 1);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}
