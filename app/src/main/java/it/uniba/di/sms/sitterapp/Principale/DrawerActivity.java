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
import it.uniba.di.sms.sitterapp.Profilo.ProfiloPrivatoActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;

/**
 * Questa classe contiene solo il drawer. E' una classe base. Tutte le classi legate al drawer, dovranno estendere
 * questa classe, per avere anche esse il drawer.
 */
public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected SessionManager sessionManager;

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

        // Valorizzo il session manager
        sessionManager = new SessionManager(getApplicationContext());

        ImageView profile_image = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.ProfileImageView);
        TextView profile_username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.ProfileUsernameView);

        Glide.with(this).load((sessionManager.getSessionType() == Constants.TYPE_SITTER)?sessionManager.getProfilePic():Constants.BASE_URL+"profilePicture/family.png").into(profile_image);
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_recensioni_mie) {
            // Handle the camera action
        } else if (id == R.id.nav_home) {


        } else if (id == R.id.nav_engagements) {

            //collegamento alla sezione degli appuntamenti
            Intent menuIngaggi = new Intent(DrawerActivity.this, IngaggiActivity.class);
            startActivity(menuIngaggi);

        } else if (id == R.id.nav_scrivi_feedback) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_exit) {

            // Chiama la funzione di logout
            sessionManager.logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return false;
    }

    public void goProfile(View view) {
        Intent intent = new Intent(DrawerActivity.this, ProfiloPrivatoActivity.class);
        intent.putExtra(Constants.TYPE, sessionManager.getSessionType());
        startActivity(intent);
    }
}
