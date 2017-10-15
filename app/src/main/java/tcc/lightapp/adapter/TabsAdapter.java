package tcc.lightapp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import tcc.lightapp.fragment.EventsFragment;
import tcc.lightapp.fragment.FriendsFragment;
import tcc.lightapp.fragment.GroupsFragment;
import tcc.lightapp.fragment.IndividualFragment;

/**
 * Created by Henrique on 17/08/2017.
 */

public class TabsAdapter extends FragmentPagerAdapter {
    private Context context;

    public TabsAdapter(Context context, FragmentManager fm) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;
        if (position == 0) {
            f = IndividualFragment.newInstance();
        } else if (position == 1){
            f = FriendsFragment.newInstance();
        } else if (position == 2) {
            f = GroupsFragment.newInstance();
        } else if (position == 3){
            f = EventsFragment.newInstance();
        }
        return f;
    }
}
