package tcc.lightapp.activity;


import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

import tcc.lightapp.R;
import tcc.lightapp.adapter.TabsAdapter;
import tcc.lightapp.utils.Constants;
import tcc.lightapp.utils.SharedPrefUtil;

public class MainActivity extends BaseActivity {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private int mTabIndex;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        setAvailability(true);
        setToken();

        setUpToolbar();
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

        setTabIcons();

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
        int id = item.getItemId();
        if (id == android.R.id.home) {
            setAvailability(false);
            mAuth.signOut();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setAvailability(boolean available) {
        if (available) {
            mDatabase.child(Constants.ARG_USERS).child(user.getUid()).child(Constants.ARG_USER_AVAILABLE).setValue(true);
        } else {
            mDatabase.child(Constants.ARG_USERS).child(user.getUid()).child(Constants.ARG_USER_AVAILABLE).setValue(false);
        }
    }

    public void setToken(){
        mDatabase.child(Constants.ARG_USERS).child(user.getUid()).child(Constants.ARG_FIREBASE_TOKEN).setValue(FirebaseInstanceId.getInstance().getToken());
    }

    public void setTabIcons(){
        int[] tabIcons = {
                R.drawable.ic_person_white_24dp,
                R.drawable.ic_favorite_white_24dp,
                R.drawable.ic_group_white_24dp,
                R.drawable.ic_assignment_white_24dp
        };

        for(int i=0; i<mTabLayout.getTabCount(); i++) {
            mTabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }
    }
}
