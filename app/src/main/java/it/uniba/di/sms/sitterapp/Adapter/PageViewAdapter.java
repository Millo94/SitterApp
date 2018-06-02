package it.uniba.di.sms.sitterapp.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import it.uniba.di.sms.sitterapp.Recensioni.RecensioniRicevuteFragment;
import it.uniba.di.sms.sitterapp.Recensioni.RecensioniScritteFragment;


/**
 * Adapter per il tabbed layout
 */

public class PageViewAdapter extends FragmentPagerAdapter {

    int mNumOfTabs;

    public PageViewAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;

    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        switch (position) {
            case 0:
                fragment = new RecensioniScritteFragment();
                break;
            case 1:

                fragment = new RecensioniRicevuteFragment();
                break;

        }


        return fragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }


}
