package uscworkshops.akashdeep.com.tneandroidapp.views;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import uscworkshops.akashdeep.com.tneandroidapp.fragments.FavoritesFragment;
import uscworkshops.akashdeep.com.tneandroidapp.fragments.MapsFragment;
import uscworkshops.akashdeep.com.tneandroidapp.fragments.PhotosFragment;
import uscworkshops.akashdeep.com.tneandroidapp.fragments.PlaceInfoFragment;
import uscworkshops.akashdeep.com.tneandroidapp.fragments.ReviewsFragment;

/**
 * Created by akash on 4/20/2018.
 */

public class PlaceInfoViewPager extends FragmentStatePagerAdapter {
    public PlaceInfoViewPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f;
        switch (position)
        {
            case 0:
                f=   new PlaceInfoFragment();
                break;
            case 1:
                f=   PhotosFragment.newInstance();
                break;
            case 2:
                f = MapsFragment.newInstance();
                break;
            case 3:
                f = ReviewsFragment.newInstance();
                break;
            default:
                f = null;
        }
        return  f;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence title;
        switch (position)
        {
            case 0:
                title=   "Info";
                break;
            case 1:
                title = "Photos";
                break;
            case 2:
                title = "Map";
                break;
            case 3:
                title = "Reviews";
                break;
            default:
                title = null;
        }
        return  title;
    }
}
