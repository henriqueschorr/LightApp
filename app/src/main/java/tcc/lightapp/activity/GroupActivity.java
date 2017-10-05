package tcc.lightapp.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import tcc.lightapp.R;
import tcc.lightapp.fragment.GroupFragment;
import tcc.lightapp.fragment.IndividualFragment;

public class GroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.group_container,
                GroupFragment.newInstance(),
                GroupFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }
}
