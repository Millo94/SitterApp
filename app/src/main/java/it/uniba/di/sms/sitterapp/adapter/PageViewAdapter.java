package it.uniba.di.sms.sitterapp.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import it.uniba.di.sms.sitterapp.recensioni.RecensioniRicevuteFragment;
import it.uniba.di.sms.sitterapp.recensioni.RecensioniScritteFragment;


/**
 * Adapter per il tabbed layout
 */

public class PageViewAdapter extends FragmentPagerAdapter {

    private int mNumOfTabs;

    //costruttore
    public PageViewAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;

    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        // a seconda della posizione della UI, dichiaro un fragment per le due visualizzazioni differenti del tabbed
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

    //restituisce il numero di tabs presenti nel tabbed layout
    @Override
    public int getCount() {
        return mNumOfTabs;
    }


}
