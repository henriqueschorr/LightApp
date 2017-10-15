package tcc.lightapp.activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import tcc.lightapp.R;
import tcc.lightapp.fragment.EventsFragment;

public class EventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.event_container,
                EventsFragment.newInstance(),
                EventsFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }
}
