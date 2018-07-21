package uscworkshops.akashdeep.com.tneandroidapp.views;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import uscworkshops.akashdeep.com.tneandroidapp.fragments.FavoritesFragment;
import uscworkshops.akashdeep.com.tneandroidapp.fragments.SearchFragment;

/**
 * Created by akash on 4/14/2018.
 */

public class MainActivityViewPager extends FragmentStatePagerAdapter {
    public MainActivityViewPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f;
        switch (position)
        {
            case 0:
                    f=   SearchFragment.newInstance();
                    break;
            case 1:
                    f=   FavoritesFragment.newInstance();
                    break;
            default:
                    f = null;
        }
        return  f;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence title;
        switch (position)
        {
            case 0:
                title=   "Search";
                break;
            case 1:
                title = "Favorites";
                break;
            default:
                title = null;
        }
        return  title;
    }
}
