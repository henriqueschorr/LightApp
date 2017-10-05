package tcc.lightapp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import tcc.lightapp.fragment.GroupFragment;
import tcc.lightapp.fragment.IndividualFragment;

/**
 * Created by Henrique on 17/08/2017.
 */

public class TabsAdapter extends FragmentPagerAdapter {
    private Context context;

    public TabsAdapter(Context context, FragmentManager fm){
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount(){
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position){
        if (position == 0){
//            return  context.getString(R.string.classicos);
            return "Individual";
        } else if (position == 1){
//            return  context.getString(R.string.esportivos);
            return "Grupo";
        }
//        return context.getString(R.string.luxo);
        return "Event";
    }

    @Override
    public Fragment getItem(int position){
        Fragment f = null;
        if (position == 0){
            f = IndividualFragment.newInstance();
        } else if (position == 1){
            f = GroupFragment.newInstance();
        } else {
            f = IndividualFragment.newInstance();
        }
        return f;
    }
}
