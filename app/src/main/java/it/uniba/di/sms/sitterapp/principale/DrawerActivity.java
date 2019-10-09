package it.uniba.di.sms.sitterapp.principale;

import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import it.uniba.di.sms.sitterapp.appuntamenti.IngaggiActivity;
import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.scriviRecensione.ListaIngaggiSvoltiActivity;
import it.uniba.di.sms.sitterapp.profilo.ProfiloPrivatoActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.recensioni.RecensioniActivity;
import it.uniba.di.sms.sitterapp.SessionManager;

/**
 * Questa classe contiene solo il drawer. E' una classe base. Tutte le classi legate al drawer, dovranno estendere
 * questa classe, per avere anche esse il drawer.
 */
public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected SessionManager sessionManager;
    private static final String SELECTED = "selected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_laterale);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer Layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Evidenzio l'elemento selezionato del drawer
        navigationView.getMenu().findItem(getIntent().getIntExtra(SELECTED, R.id.nav_home)).setChecked(true);

        // Valorizzo il session manager
        sessionManager = new SessionManager(getApplicationContext());

        //per l'immagine e il testo dell'utente
        ImageView profile_image = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ProfileImageView);
        TextView profile_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.ProfileUsernameView);

        Glide.with(this).load((sessionManager.getSessionType() == Constants.TYPE_SITTER) ? sessionManager.getProfilePic() : Constants.BASE_URL + "profilePicture/family.png").into(profile_image);
        profile_username.setText(sessionManager.getSessionUsername());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (!sessionManager.checkLogin()) {
            sessionManager.forceLogin(this);
        } else {
            if (!item.isChecked()) {

                // Gestisce la navigazione al click del menu
                int id = item.getItemId();
                if (id == R.id.nav_recensioni_mie) {

                    //collegamento alla sezione recensioni
                    Intent recensioni = new Intent(DrawerActivity.this, RecensioniActivity.class);
                    startActivity(recensioni);

                } else if (id == R.id.nav_home) {
                    //riporta alla home
                    finish();

                } else if (id == R.id.nav_engagements) {

                    //collegamento alla sezione degli appuntamenti
                    Intent menuIngaggi = new Intent(DrawerActivity.this, IngaggiActivity.class);
                    menuIngaggi.putExtra(SELECTED, id);
                    startActivity(menuIngaggi);

                } else if (id == R.id.nav_scrivi_feedback) {
                    //collegamento a scrivi recensione
                    Intent scrivirecIntent = new Intent(DrawerActivity.this, ListaIngaggiSvoltiActivity.class);
                    scrivirecIntent.putExtra(SELECTED, id);
                    startActivity(scrivirecIntent);

                } else if (id == R.id.nav_exit) {
                    // Chiama la funzione di logout
                    sessionManager.logout();
                }
                drawer.closeDrawer(GravityCompat.START);

            } else {
                drawer.closeDrawer(GravityCompat.START);
            }
        }

        return false;
    }

    //al click sul nome dell'utente si accede al profilo privato
    public void goProfile(View view) {
        Intent intent = new Intent(DrawerActivity.this, ProfiloPrivatoActivity.class);
        intent.putExtra(Constants.TYPE, sessionManager.getSessionType());
        startActivity(intent);
    }
}
