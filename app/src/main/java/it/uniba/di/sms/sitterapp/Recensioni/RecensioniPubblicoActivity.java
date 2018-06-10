package it.uniba.di.sms.sitterapp.Recensioni;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.DefaultItemAnimator;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Adapter.RecensioniAdapter;
import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Oggetti.Recensione;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.Principale.DrawerActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import tr.xip.errorview.ErrorView;

/**
 * Recensioni Pubblico Activity: un utente vede le recensioni di un altro utente
 */

public class RecensioniPubblicoActivity extends AppCompatActivity {


    SessionManager sessionManager;
    private RecyclerView recycler;
    private RecensioniAdapter adapter;
    private List<Recensione> recensioneList;
    boolean itShouldLoadMore = false;
    String user;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = getIntent().getStringExtra("username");
        recycler = (RecyclerView) findViewById(R.id.recyclerHome);

        recensioneList = new ArrayList<>();
        adapter = new RecensioniAdapter(recensioneList);
        recycler.setAdapter(adapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(mLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        sessionManager = new SessionManager(getApplicationContext());
        loadNotices();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadNotices() {

        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Php.RECENSIONI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        itShouldLoadMore = true;
                        try {
                            JSONArray recensione = new JSONArray(response);

                            if (recensione.length() == 0) {
                                ErrorView errorView = (ErrorView) findViewById(R.id.errorView);
                                errorView.setSubtitle(R.string.niente_recensioni);
                                errorView.setVisibility(View.VISIBLE);
                            } else {

                                for (int i = 0; i < recensione.length(); i++) {

                                    JSONObject recensioneObject = recensione.getJSONObject(i);
                                    String username;

                                    if (sessionManager.getSessionType() == Constants.TYPE_SITTER) {
                                        username = recensioneObject.getString("babysitter");
                                    } else {
                                        username = recensioneObject.getString("famiglia");
                                    }


                                    String descrizione = recensioneObject.getString("commento");
                                    Float rating = (float) recensioneObject.getDouble("rating");

                                    Recensione r = new Recensione(username, descrizione, rating);

                                    recensioneList.add(r);
                                }
                            }


                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", user);

                if (sessionManager.getSessionType() == Constants.TYPE_SITTER) {
                    params.put("tipoUtente", String.valueOf(Constants.TYPE_FAMILY));
                } else {
                    params.put("tipoUtente", String.valueOf(Constants.TYPE_SITTER));
                }

                params.put("operation", "received");
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
}

