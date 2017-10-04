package tcc.lightapp.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import tcc.lightapp.R;
import tcc.lightapp.fragment.IndividualFragment;
import tcc.lightapp.utils.Constants;

public class IndividualActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        setAvailability(true);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,
                IndividualFragment.newInstance(),
                IndividualFragment.class.getSimpleName());
        fragmentTransaction.commit();

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
}
