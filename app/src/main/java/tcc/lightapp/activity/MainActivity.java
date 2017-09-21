package tcc.lightapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
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
import tcc.lightapp.domain.User;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private String userName;
    private static final String TAG = "Login";
    private List<User> availableUsers = new ArrayList<User>();

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

        TextView mNome = (TextView) findViewById(R.id.nome);

        mNome.setText("Bem vindo " + userName);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getAvailableUsers();
    }

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
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
