package tcc.lightapp.activity;

import android.os.StrictMode;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import tcc.lightapp.R;
import tcc.lightapp.fragment.PatientsFragment;
import tcc.lightapp.fragment.dialog.AddPatientDialog;

public class MainMedicActivity extends BaseActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_medic);

        mAuth = FirebaseAuth.getInstance();

        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.patients_container,
                PatientsFragment.newInstance(),
                PatientsFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_medic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int item = menuItem.getItemId();
        if (item == R.id.action_add_patient) {
            AddPatientDialog.showDialog(getSupportFragmentManager());
            return true;
        } else if (item == android.R.id.home) {
            mAuth.signOut();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
