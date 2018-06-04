package it.uniba.di.sms.sitterapp.Principale;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import it.uniba.di.sms.sitterapp.Appuntamenti.IngaggiActivity;
import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Feedback.FeedbackActivity;
import it.uniba.di.sms.sitterapp.Profilo.ProfiloPrivatoActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Recensioni.RecensioniActivity;
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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Evidenzio l'elemento selezionato del drawer
        navigationView.getMenu().findItem(getIntent().getIntExtra(SELECTED, R.id.nav_home)).setChecked(true);

        // Valorizzo il session manager
        sessionManager = new SessionManager(getApplicationContext());

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
        if(!item.isChecked()){

            // Gestisce la navigazione al click del menu
            int id = item.getItemId();
            if (id == R.id.nav_recensioni_mie) {

                //collegamento alla sezione recensioni
                Intent recensioni = new Intent(DrawerActivity.this, RecensioniActivity.class);
                startActivity(recensioni);

            } else if (id == R.id.nav_home) {

                /*//collegamento alla Home
                Intent home = new Intent(DrawerActivity.this, HomeActivity.class);
                home.putExtra(SELECTED, id);
                startActivity(home);*/
                finish();

            } else if (id == R.id.nav_engagements) {

                //collegamento alla sezione degli appuntamenti
                Intent menuIngaggi = new Intent(DrawerActivity.this, IngaggiActivity.class);
                menuIngaggi.putExtra(SELECTED, id);
                startActivity(menuIngaggi);

            } else if (id == R.id.nav_scrivi_feedback) {

                Intent scrivirecIntent = new Intent(DrawerActivity.this, FeedbackActivity.class);
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





        return false;
    }

    public void goProfile(View view) {
        Intent intent = new Intent(DrawerActivity.this, ProfiloPrivatoActivity.class);
        intent.putExtra(Constants.TYPE, sessionManager.getSessionType());
        startActivity(intent);
    }
}
