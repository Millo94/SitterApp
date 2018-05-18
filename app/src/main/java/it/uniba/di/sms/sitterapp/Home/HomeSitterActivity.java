package it.uniba.di.sms.sitterapp.Home;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.List;

import it.uniba.di.sms.sitterapp.MainActivity;
import it.uniba.di.sms.sitterapp.R;

public class HomeSitterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NoticeAdapter.NoticeAdapterListener {

    private static final String NOTICE_URL ="http://sitterapp.altervista.org/AnnunciFamiglie.php";
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Notice> noticeList;
    private NoticeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_laterale_sitter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_sitter);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_sitter);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noticeList = new ArrayList<>();
        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //prova caricamento di annunci
        addNotices();
    }


    private void addNotices() {
        //TODO prendere i dati dal DB

        StringRequest stringRequest = new StringRequest(Request.Method.GET, NOTICE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray notice = new JSONArray(response);
                            for(int i=0;i<notice.length();i++){
                                JSONObject noticeObject = notice.getJSONObject(i);
                                String famiglia = noticeObject.getString("usernameFamiglia");
                                String data = noticeObject.getString("data");
                                String oraInizio = noticeObject.getString("oraInizio");
                                String oraFine = noticeObject.getString("oraFine");
                                String descrizione = noticeObject.getString("descrizione");
                                Notice n = new Notice(famiglia,data,oraInizio,oraFine,descrizione);

                                noticeList.add(n);
                            }
                            mAdapter = new NoticeAdapter(HomeSitterActivity.this, noticeList, HomeSitterActivity.this);
                            recyclerView.setAdapter(mAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeSitterActivity.this,error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_sitter);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

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


    //metodo che riconosce e gestisce la selezione di ogni voce del menu laterale
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chat_sitter) {
            // Handle the camera action
        } else if (id == R.id.nav_engagements_sitter) {

        } else if (id == R.id.nav_feedback_sitter) {

        } else if (id == R.id.nav_settings_sitter) {

        } else if (id == R.id.nav_exit_sitter) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_sitter);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //questo metodo fa partire un toast dell'annuncio selezionato
    @Override
    public void onNoticeSelected(Notice notice) {
        Toast.makeText(getApplicationContext(), "Selected: "+ notice.getFamily() + ", " + notice.getDate() + ", " + notice.getDescription(), Toast.LENGTH_LONG).show();
    }
}
