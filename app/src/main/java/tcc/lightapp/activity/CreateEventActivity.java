package tcc.lightapp.activity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import tcc.lightapp.R;
import tcc.lightapp.fragment.CreateEventFragment;
import tcc.lightapp.utils.Constants;

public class CreateEventActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        String mGroupKey = args.getString(Constants.ARG_GROUP_KEY);
        String mGroupName = args.getString(Constants.ARG_GROUP_NAME);

        setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.create_event);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.create_event_container,
                CreateEventFragment.newInstance(mGroupKey, mGroupName),
                CreateEventFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
