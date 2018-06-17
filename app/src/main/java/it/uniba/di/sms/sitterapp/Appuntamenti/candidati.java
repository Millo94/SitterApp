package it.uniba.di.sms.sitterapp.Appuntamenti;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import it.uniba.di.sms.sitterapp.Adapter.SitterAdapter;
import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Oggetti.UtenteSitter;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.Principale.DrawerActivity;
import it.uniba.di.sms.sitterapp.Profilo.ProfiloPubblicoActivity;
import it.uniba.di.sms.sitterapp.R;

/**
 * Sezione per le candidature: assegnare un lavoro a una baby sitter
 */
public class candidati extends DrawerActivity
        implements SitterAdapter.ContactsSitterAdapterListener {


    private List<UtenteSitter> sitterList;
    private SitterAdapter sitterAdapter;
    private final static String assegna = "ASSEGNA";
    private final static String visualizza = "VISUALIZZA";


    protected boolean itShouldLoadMore = true;

    private String idAnnuncio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // caricamento degli annunci nella recyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerHome);

        sitterList = new ArrayList<>();
        sitterAdapter = new SitterAdapter(candidati.this, sitterList, candidati.this);


        recyclerView.setAdapter(sitterAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        idAnnuncio = getIntent().getStringExtra("idAnnuncio");


        loadSitter();

    }


    //Volley per recuperare i dati dal database
    private void loadSitter() {

        itShouldLoadMore = false;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Php.CANDIDAMI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        itShouldLoadMore = true;

                        try {
                            JSONArray sitter = new JSONArray(response);
                            for (int i = 0; i < sitter.length(); i++) {
                                JSONObject sitterObject = sitter.getJSONObject(i);
                                String username = sitterObject.getString("username");
                                String foto = sitterObject.getString("pathfoto");
                                UtenteSitter s = new UtenteSitter(username, foto);
                                sitterList.add(s);
                            }
                            sitterAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(candidati.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("richiesta", visualizza);
                params.put("idAnnuncio", idAnnuncio);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
        progressDialog.hide();
    }

    //quando si clicca su una baby sitter si apre un Dialog che consente alla famiglia
    //di visualizzare il profilo della sitter o assegnarle un incarico
    @Override
    public void onSitterSelected(final UtenteSitter sitter) {

        final CharSequence servizi[] = new CharSequence[]{getString(R.string.visualizzaProfiloSitter), getString(R.string.assegnaIncarico)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.sceltaAzione);
        builder.setItems(servizi, new DialogInterface.OnClickListener() {
            /**
             * Questo metodo serve per eseguire un azione tra viasualizza il profilo e assegna incarico
             * @param dialog
             * @param which
             */
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {

                    //visualizza profilo
                    case 0:
                        Intent detailIntent = new Intent(candidati.this, ProfiloPubblicoActivity.class);
                        detailIntent.putExtra(Constants.TYPE, Constants.TYPE_SITTER);
                        detailIntent.putExtra("username", sitter.getUsername());
                        startActivity(detailIntent);
                        break;
                    //assegna incarico
                    case 1:
                        assegnaIncarico(sitter.getUsername(), idAnnuncio);
                        break;

                    default:
                        break;
                }

            }
        });
        builder.show();
    }

    //Volley per assegnare un incarico alla sitter
    public void assegnaIncarico(final String username, final String idAnnuncio) {
        StringRequest assegnaRequest = new StringRequest(Request.Method.POST, Php.CANDIDAMI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.optString("response");

                    if (result.equals("true")) {
                        Toast.makeText(candidati.this, "lavoro assegnato", Toast.LENGTH_SHORT).show();
                        Intent intentback = new Intent(candidati.this, IngaggiActivity.class);
                        startActivity(intentback);
                    } else if (result.equals("false")) {
                        Toast.makeText(candidati.this, "errore", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(candidati.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("richiesta", assegna);
                params.put("username", username);
                params.put("idAnnuncio", idAnnuncio);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(assegnaRequest);
    }
}
