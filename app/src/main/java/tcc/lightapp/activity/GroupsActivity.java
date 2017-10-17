package tcc.lightapp.activity;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import tcc.lightapp.R;
import tcc.lightapp.fragment.GroupsFragment;

public class GroupsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.group_container,
                GroupsFragment.newInstance(),
                GroupsFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }
}
