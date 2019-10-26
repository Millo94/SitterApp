package it.uniba.di.sms.sitterapp.recensioni;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import it.uniba.di.sms.sitterapp.adapter.PageViewAdapter;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.principale.DrawerActivity;
import it.uniba.di.sms.sitterapp.scriviRecensione.IngaggiDaRecensireActivity;

/**
 * Classe per le recensioni legate all'utente
 */
public class RecensioniActivity extends DrawerActivity {

    ViewPager viewPager;
    private static final String SELECTED = "selected";
    BottomNavigationView bottomNavigationView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //BottomNavigationView
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().findItem(getIntent().getIntExtra(SELECTED,R.id.nav_recensioni)).setChecked(true);
        float dimension = getResources().getDimensionPixelSize(R.dimen.appbar_size);


        //Tabbed Layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTranslationY(56);
        tabLayout.animate().translationY(dimension).alpha(1).setDuration(10*100).start();
        tabLayout.addTab(tabLayout.newTab().setText("Recensioni Scritte"));
        tabLayout.addTab(tabLayout.newTab().setText("Recensioni Ricevute"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.pager);

        PageViewAdapter adapter = new PageViewAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                getSupportFragmentManager().beginTransaction().addToBackStack(null).commit();

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recensioni_action_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_write_review:
                //collegamento a scrivi recensione
                Intent scrivirecIntent = new Intent(RecensioniActivity.this, IngaggiDaRecensireActivity.class);
                startActivity(scrivirecIntent);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
