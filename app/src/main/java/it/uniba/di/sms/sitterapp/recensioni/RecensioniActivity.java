package it.uniba.di.sms.sitterapp.recensioni;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import it.uniba.di.sms.sitterapp.adapter.PageViewAdapter;
import it.uniba.di.sms.sitterapp.R;

/**
 * Classe per le recensioni legate all'utente
 */
public class RecensioniActivity extends AppCompatActivity {

    ViewPager viewPager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recensioni);


        //Tabbed Layout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
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


}
