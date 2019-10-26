package it.uniba.di.sms.sitterapp.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import it.uniba.di.sms.sitterapp.appuntamenti.IngaggiDaConfermareFragment;
import it.uniba.di.sms.sitterapp.appuntamenti.IngaggiDaEseguireFragment;
import it.uniba.di.sms.sitterapp.appuntamenti.IngaggiEseguitiFragment;


/**
 * Adapter per il tabbed layout
 */

public class IngaggiTabAdapter extends FragmentPagerAdapter {

    private int mNumOfTabs;

    //costruttore
    public IngaggiTabAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;

    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;

        // a seconda della posizione della UI, dichiaro un fragment per le due visualizzazioni differenti del tabbed
        switch (position) {
            case 0:
                fragment = new IngaggiDaConfermareFragment();
                break;
            case 1:
                fragment = new IngaggiDaEseguireFragment();
                break;
            case 2:
                fragment = new IngaggiEseguitiFragment();
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
