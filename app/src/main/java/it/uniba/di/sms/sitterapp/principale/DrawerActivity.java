package it.uniba.di.sms.sitterapp.principale;

import android.content.Intent;
import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;

import androidx.viewpager.widget.ViewPager;
import it.uniba.di.sms.sitterapp.appuntamenti.IngaggiActivity;
import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.chat.ChatActivity;
import it.uniba.di.sms.sitterapp.profilo.ProfiloPrivatoActivity;
import it.uniba.di.sms.sitterapp.scriviRecensione.IngaggiDaRecensireActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.recensioni.RecensioniActivity;
import it.uniba.di.sms.sitterapp.SessionManager;

/**
 * Questa classe contiene solo il drawer. E' una classe base. Tutte le classi legate al drawer, dovranno estendere
 * questa classe, per avere anche esse il drawer.
 */
public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

    protected SessionManager sessionManager;
    private static final String SELECTED = "selected";
    BottomNavigationView bottomNavigationView;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_home);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //TODO controlla le cose commentate
        /*// Drawer Layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();*/

        //BottomNavigationView
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().findItem(getIntent().getIntExtra(SELECTED,R.id.nav_home)).setChecked(true);
        //TODO RIMUOVI CODICE COMMENTATO
        //NavigationView
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        // Evidenzio l'elemento selezionato del drawer
        //navigationView.getMenu().findItem(getIntent().getIntExtra(SELECTED, R.id.nav_home)).setChecked(true);

        // Valorizzo il session manager
        sessionManager = new SessionManager(getApplicationContext());
        //TODO RIMUOVI CODICE COMMENTATO
        //per l'immagine e il testo dell'utente
        //ImageView profile_image = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ProfileImageView);
        //TextView profile_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.ProfileUsernameView);

        //Glide.with(this).load((sessionManager.getSessionType() == Constants.TYPE_SITTER) ? sessionManager.getProfilePic() : Constants.BASE_URL + "profilePicture/family.png").into(profile_image);
        //profile_username.setText(sessionManager.getSessionUsername());
    }

/*
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
*/


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (!sessionManager.checkLogin()) {
            sessionManager.forceLogin(this);
        } else {
            if (!item.isChecked()) {

                // Gestisce la navigazione al click del menu
                int id = item.getItemId();
                //TODO RIMUOVERE CODICE COMMENTATO
                /*if (id == R.id.nav_recensioni_mie) {
                    //collegamento alla sezione recensioni
                    Intent recensioni = new Intent(DrawerActivity.this, RecensioniActivity.class);
                    startActivity(recensioni);

                } else*/ if (id == R.id.nav_home) {
                    //Activity principale
                    Intent home = new Intent(DrawerActivity.this, HomeActivity.class);
                    startActivity(home);

                }else if (id == R.id.nav_recensioni) {
                    //collegamento alla sezione recensioni
                    //Activity in cui si vedono le recensioni fatte/ricevute
                    Intent recensioni = new Intent(DrawerActivity.this, RecensioniActivity.class);
                    startActivity(recensioni);

                }else if (id == R.id.nav_chat) {
                    //collegamento alla sezione recensioni
                    //chat con babysitter/famiglia
                    Intent chat = new Intent(DrawerActivity.this, ChatActivity.class);
                    startActivity(chat);

                } else if (id == R.id.nav_engagements) {

                    //collegamento alla sezione degli appuntamenti
                    //lista degli annunci fatti (nel caso della famiglia) assegnati (nel caso della babysitter)
                    Intent menuIngaggi = new Intent(DrawerActivity.this, IngaggiActivity.class);
                    menuIngaggi.putExtra(SELECTED, id);
                    startActivity(menuIngaggi);

                } else if (id == R.id.nav_scrivi_feedback) {
                    //TODO da rimuovere, bisogna aggiungere un bottone nell'activity recensioni per crearne una nuova
                    //collegamento a scrivi recensione
                    Intent scrivirecIntent = new Intent(DrawerActivity.this, IngaggiDaRecensireActivity.class);
                    scrivirecIntent.putExtra(SELECTED, id);
                    startActivity(scrivirecIntent);

                } else if (id == R.id.nav_account) {
                    //collegamento a profilo privato
                    //porta alle informazioni sul proprio profilo
                    Intent intent = new Intent(DrawerActivity.this, ProfiloPrivatoActivity.class);
                    intent.putExtra(Constants.TYPE, sessionManager.getSessionType());
                    startActivity(intent);

                }
                //TODO RIMUOVI CODICE COMMENTATO

                /* else if (id == R.id.nav_exit) {
                    // Chiama la funzione di logout
                    sessionManager.logout();
                }
                //drawer.closeDrawer(GravityCompat.START);

            } else {
                //drawer.closeDrawer(GravityCompat.START);
            */
            }
        }

        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //al click sul nome dell'utente si accede al profilo privato
    public void goProfile(View view) {
        Intent intent = new Intent(DrawerActivity.this, ProfiloPrivatoActivity.class);
        intent.putExtra(Constants.TYPE, sessionManager.getSessionType());
        startActivity(intent);
    }
}
