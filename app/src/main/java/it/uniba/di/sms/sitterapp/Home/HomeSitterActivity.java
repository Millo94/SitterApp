package it.uniba.di.sms.sitterapp.Home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.drm.DrmStore;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pnikosis.materialishprogress.ProgressWheel;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

import it.uniba.di.sms.sitterapp.Appuntamenti.AppuntamentiSitterActivity;
import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.LoginActivity;
import it.uniba.di.sms.sitterapp.MainActivity;
import it.uniba.di.sms.sitterapp.Profilo.ProfiloPrivatoActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;

public class HomeSitterActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NoticeAdapter.NoticeAdapterListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Notice> noticeList;
    private Queue<Notice> remainingNoticeList;
    private NoticeAdapter mAdapter;
    private SessionManager sessionManager;

    // we will be loading 15 items per page or per load
    // you can change this to fit your specifications.
    // When you change this, there will be no need to update your php page,
    // as php will be ordered what to load and limit by android java
    public static final int LOAD_LIMIT = 5;

    private String NOTICE_URL = Constants.BASE_URL + "AnnunciFamiglie.php";

    // we need this variable to lock and unlock loading more
    // e.g we should not load more when volley is already loading,
    // loading will be activated when volley completes loading
    private boolean itShouldLoadMore = true;

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
        mAdapter = new NoticeAdapter(HomeSitterActivity.this, noticeList, HomeSitterActivity.this);
        remainingNoticeList = new LinkedList<>();

        // Valorizzo il session manager
        sessionManager = new SessionManager(getApplicationContext());

        ImageView profile_image = (ImageView) findViewById(R.id.ProfileImageView);
        TextView profile_username = (TextView) findViewById(R.id.ProfileUsernameView);

        //todo aggiungere caricamento del nome_profilo e immagine_profilo nell'header del drawer
        //Glide.with(this).load(sessionManager.getSessionProfilePic()).into(profile_image);
        //profile_username.setText(sessionManager.getSessionUsername());


        recyclerView.setAdapter(mAdapter);

        // white background notification bar
        whiteNotificationBar(recyclerView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //caricamento di annunci
        firstLoadNotices();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        // remember "!" is the same as "== false"
                        // here we are now allowed to load more, but we need to be careful
                        // we must check if itShouldLoadMore variable is true [unlocked]
                        if (itShouldLoadMore) {

                            loadMore();
                        }
                    }
                }
            }
        });
    }

    private void firstLoadNotices() {

        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, NOTICE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        itShouldLoadMore = true;
                        try {
                            JSONArray notice = new JSONArray(response);
                            for (int i = 0; i < notice.length(); i++) {

                                JSONObject noticeObject = notice.getJSONObject(i);
                                String famiglia = noticeObject.getString("usernameFamiglia");
                                String data = noticeObject.getString("data");
                                String oraInizio = noticeObject.getString("oraInizio");
                                String oraFine = noticeObject.getString("oraFine");
                                String descrizione = noticeObject.getString("descrizione");
                                descrizione = (descrizione.length()>100)?descrizione.substring(0,100)+"...":descrizione;
                                Notice n = new Notice(famiglia, data, oraInizio, oraFine, descrizione);

                                if (i < LOAD_LIMIT) {
                                    noticeList.add(n);
                                } else {
                                    remainingNoticeList.add(n);
                                }
                            }

                            mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(HomeSitterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void loadMore() {

        itShouldLoadMore = false; // lock this until volley completes processing

        // progressWheel is just a loading spinner, please see the content_main.xml
        final ProgressWheel progressWheel = (ProgressWheel) this.findViewById(R.id.progress_wheel);
        progressWheel.setVisibility(View.VISIBLE);

        itShouldLoadMore = true;

        if (!remainingNoticeList.isEmpty()) {
            //todo aggiungere un ritardo (asynctask) per visualizzare la ruota figa di caricamento
            final int remainingNoticeListSize = remainingNoticeList.size();
            for (int i = 0; i < remainingNoticeListSize; ++i) {
                if (i < LOAD_LIMIT) {
                    noticeList.add(remainingNoticeList.remove());
                }
            }
            mAdapter.notifyDataSetChanged();
        }

        progressWheel.setVisibility(View.GONE);
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
            //collegamento alla sezione degli appuntamenti

            Intent menuIngaggi = new Intent(HomeSitterActivity.this, AppuntamentiSitterActivity.class);
            menuIngaggi.putExtra(Constants.TYPE, Constants.TYPE_SITTER);
            startActivity(menuIngaggi);

        } else if (id == R.id.nav_feedback_sitter) {

        } else if (id == R.id.nav_settings_sitter) {

        } else if (id == R.id.nav_exit_sitter) {

            // Chiama la funzione di logout
            sessionManager.logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_sitter);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //questo metodo fa partire un toast dell'annuncio selezionato
    @Override
    public void onNoticeSelected(Notice notice) {
        Toast.makeText(getApplicationContext(), "Selected: " + notice.getFamily() + ", " + notice.getDate() + ", " + notice.getDescription(), Toast.LENGTH_LONG).show();
    }


    public void goProfile(View view) {
        Intent intent = new Intent(HomeSitterActivity.this, ProfiloPrivatoActivity.class);
        intent.putExtra(Constants.TYPE, Constants.TYPE_SITTER);
        startActivity(intent);
    }
}