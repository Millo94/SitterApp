package it.uniba.di.sms.sitterapp.profilo;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.uniba.di.sms.sitterapp.Constants.Constants;
import it.uniba.di.sms.sitterapp.oggetti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.oggetti.UtenteSitter;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.principale.DrawerActivity;

public class ProfiloPrivatoActivity extends DrawerActivity implements PrivatoFamigliaFragment.OnFragmentInteractionListener, PrivatoSitterFragment.OnFragmentInteractionListener{

    private static final String SELECTED = "selected";
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //BottomNavigationView
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().findItem(getIntent().getIntExtra(SELECTED,R.id.nav_account)).setChecked(true);

        // Selezione del fragment in base al tipo dell'intent
        int type = getIntent().getIntExtra(Constants.TYPE, -1);
        if (type == Constants.TYPE_SITTER){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.viewPager, new PrivatoSitterFragment()).commit();
        } else if(type == Constants.TYPE_FAMILY) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.viewPager, new PrivatoFamigliaFragment()).commit();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onFragmentInteraction(UtenteFamiglia famiglia) {
    }

    @Override
    public void onFragmentInteraction(UtenteSitter sitter) {

    }

}
