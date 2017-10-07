package tcc.lightapp.activity;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import tcc.lightapp.R;
import tcc.lightapp.adapter.TabsAdapter;
import tcc.lightapp.utils.Constants;
import tcc.lightapp.utils.SharedPrefUtil;

public class MainActivity extends BaseActivity {
    private int mTabIndex;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        setAvailability(true);

        mToolbar = setUpToolbar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupViewPagerTabs();
    }

    private void setupViewPagerTabs() {
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(new TabsAdapter(getContext(), getSupportFragmentManager()));

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);

        mTabLayout.setupWithViewPager(mViewPager);
        int cor = ContextCompat.getColor(getContext(), R.color.white);
        mTabLayout.setTabTextColors(cor, cor);

        mViewPager.setOffscreenPageLimit(2);

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTabIndex = tab.getPosition();
                new SharedPrefUtil(getApplicationContext()).saveInt(Constants.ARG_TAB_INDEX, mTabIndex);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mTabIndex = new SharedPrefUtil(getApplicationContext()).getInt(Constants.ARG_TAB_INDEX);
        mViewPager.setCurrentItem(mTabIndex);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewPager.setCurrentItem(mTabIndex);
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
            childUpdates.put("/" + Constants.ARG_USERS + "/" + user.getUid() + "/" + Constants.ARG_USER_AVAILABLE, false);
        }

        mDatabase.updateChildren(childUpdates);
    }

    public boolean onCreateOptionsMenu(Menu menu){
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
