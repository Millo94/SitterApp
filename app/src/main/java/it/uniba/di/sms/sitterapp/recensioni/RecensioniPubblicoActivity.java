package it.uniba.di.sms.sitterapp.recensioni;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.recyclerview.widget.DefaultItemAnimator;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.uniba.di.sms.sitterapp.adapter.RecensioniRicevuteAdapter;
import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.oggetti.Recensione;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import tr.xip.errorview.ErrorView;

/**
 * Recensioni Pubblico Activity: un utente vede le recensioni di un altro utente
 */

public class RecensioniPubblicoActivity extends AppCompatActivity {


    SessionManager sessionManager;
    private RecyclerView recycler;
    private RecensioniRicevuteAdapter adapter;
    private List<Recensione> recensioneList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ErrorView errorView;

    String user;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        user = getIntent().getStringExtra("uid");
        recycler = (RecyclerView) findViewById(R.id.recyclerHome);

        recensioneList = new ArrayList<>();
        adapter = new RecensioniRicevuteAdapter(recensioneList);
        recycler.setAdapter(adapter);

        errorView = (ErrorView) findViewById(R.id.errorViewContent);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycler.setLayoutManager(mLayoutManager);
        recycler.setItemAnimator(new DefaultItemAnimator());
        sessionManager = new SessionManager(getApplicationContext());
        loadFeedback();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFeedback(){
        db.collection("recensione")
                .whereEqualTo("receiver", user)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots.isEmpty()) {

                            errorView.setTitle(R.string.niente_recensioni_ricevute);
                            errorView.setVisibility(View.VISIBLE);

                        } else {
                            errorView.setVisibility(View.GONE);
                            Iterator<QueryDocumentSnapshot> iterableReview = queryDocumentSnapshots.iterator();
                            while(iterableReview.hasNext()){
                                DocumentSnapshot documentSnapshot = iterableReview.next();
                                Recensione recensione = documentSnapshot.toObject(Recensione.class);
                                recensioneList.add(recensione);
                            }

                            adapter.notifyDataSetChanged();
                        }

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO Sostituire "Errore" con la stringa di errore di riferimento.
                        //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //volley per il caricamento delle recensioni
    private void load() {


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.dismiss();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Php.RECENSIONI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();

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
                                        username = recensioneObject.getString("sender");
                                    } else {
                                        username = recensioneObject.getString("famiglia");
                                    }

                                    String sender = "";
                                    String receiver = "";
                                    String idAnnuncio = "";
                                    String descrizione = recensioneObject.getString("commento");
                                    Float rating = (float) recensioneObject.getDouble("rating");

                                    Recensione r = new Recensione(descrizione, rating, idAnnuncio, sender, receiver);

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

