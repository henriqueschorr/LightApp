package tcc.lightapp.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tcc.lightapp.R;
import tcc.lightapp.fragment.ReportFragment;
import tcc.lightapp.utils.Constants;

public class ReportActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        ReportFragment fragment = new ReportFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.report_container,
                fragment,
                ReportFragment.class.getSimpleName());
        fragment.setArguments(args);
        fragmentTransaction.commit();
    }

    //TODO: FIX BACK FUNCTION
}
