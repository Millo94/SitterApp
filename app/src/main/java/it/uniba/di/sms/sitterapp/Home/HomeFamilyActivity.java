package it.uniba.di.sms.sitterapp.Home;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Utenti.UtenteSitter;

import static it.uniba.di.sms.sitterapp.R.id.drawer_layout_famiglia;

public class HomeFamilyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ContactSitterAdapter.ContactsSitterAdapterListener {

    private static final String SITTER_URL ="http://sitterapp.altervista.org/AnnunciSitter.php";
    private static final String TAG = HomeFamilyActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<UtenteSitter> sitterList;
    private ContactSitterAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_laterale_famiglia);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(drawer_layout_famiglia);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_famiglia);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        sitterList = new ArrayList<>();

        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //prova caricamento di annunci
        addNotices();


    }

    private void addNotices() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, SITTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray sitter = new JSONArray(response);
                            for(int i=0;i<sitter.length();i++){
                                JSONObject sitterObject = sitter.getJSONObject(i);
                                String username = sitterObject.getString("username");
                                String nLavori = sitterObject.getString("numerolavori");
                                String foto = sitterObject.getString("pathfoto");
                                UtenteSitter s = new UtenteSitter(username,nLavori,foto);

                                sitterList.add(s);
                            }
                            mAdapter = new ContactSitterAdapter(HomeFamilyActivity.this, sitterList, HomeFamilyActivity.this);
                            recyclerView.setAdapter(mAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeFamilyActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(drawer_layout_famiglia);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /*
    menù delle opzioni
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    /*
    bottone delle opzioni
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    /*
    metodo che riconosce e gestisce la selezione di ogni voce del menu laterale
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chat_famiglia) {
            // Handle the camera action
        } else if (id == R.id.nav_engagements_famiglia) {

        } else if (id == R.id.nav_feedback_famiglia) {

        } else if (id == R.id.nav_settings_famiglia) {

        } else if (id == R.id.nav_exit_famiglia) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_famiglia);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    questo metodo fa partire un toast dell'annuncio selezionato
     */
    @Override
    public void onSitterSelected(UtenteSitter sitter) {
        Toast.makeText(getApplicationContext(), "Selected: " + sitter.getUsername() + ", " + sitter.getNumeroLavori() + ", " + sitter.getFoto(), Toast.LENGTH_LONG).show();

    }
}
