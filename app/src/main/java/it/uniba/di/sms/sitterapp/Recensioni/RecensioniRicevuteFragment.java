package it.uniba.di.sms.sitterapp.Recensioni;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import it.uniba.di.sms.sitterapp.Adapter.RecensioniAdapter;
import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Oggetti.Recensione;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;


public class RecensioniRicevuteFragment extends Fragment implements RecensioniAdapter.RecensioniAdapterListener {

    // Vista
    private RecyclerView recyclerView;

    //Items ingaggi
    private List<Recensione> recensioneList;
    private Queue<Recensione> remainingRecensioneList;
    private RecensioniAdapter recensioneAdapter;


    // we will be loading 15 items per page or per load
    // you can change this to fit your specifications.
    // When you change this, there will be no need to update your php page,
    // as php will be ordered what to load and limit by android java
    public static final int LOAD_LIMIT = 5;

    // we need this variable to lock and unlock loading more
    // e.g we should not load more when volley is already loading,
    // loading will be activated when volley completes loading
    private boolean itShouldLoadMore = true;

    private SessionManager sessionManager;


    public RecensioniRicevuteFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.content_home, container, false);

        sessionManager = new SessionManager(getActivity().getApplicationContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerHome);

        recensioneList = new ArrayList<>();
        remainingRecensioneList = new LinkedList<>();
        recensioneAdapter = new RecensioniAdapter(recensioneList);


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(recensioneAdapter);


        //caricamento di annunci
        loadNotices();

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

                            loadMore(view);
                        }
                    }
                }
            }
        });


        return view;
    }

    /**
     * Caricamento degli annunci (per la home delle babysitter)
     */
    private void loadNotices() {

        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        //todo -> inserire php giusto
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Php.RECENSIONI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        itShouldLoadMore = true;
                        try {
                            JSONArray recensione = new JSONArray(response);

                            for (int i = 0; i < recensione.length() - 1; i++) {

                                JSONObject recensioneObject = recensione.getJSONObject(i);
                                String username;

                                if (sessionManager.getSessionType() == Constants.TYPE_SITTER) {
                                    username = recensioneObject.getString("famiglia");
                                } else {
                                    username = recensioneObject.getString("babysitter");
                                }


                                String descrizione = recensioneObject.getString("commento");
                                Float rating = (float) recensioneObject.getDouble("rating");

                                Recensione r = new Recensione(username, descrizione, rating);

                                if (i < LOAD_LIMIT) {
                                    recensioneList.add(r);
                                } else {
                                    remainingRecensioneList.add(r);
                                }
                            }


                            recensioneAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", sessionManager.getSessionUsername());
                params.put("tipoUtente", String.valueOf(sessionManager.getSessionType()));
                params.put("operation", "received");
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    /**
     * Caricamento incrementale degli annunci
     */
    private void loadMore(View view) {

        itShouldLoadMore = false; // lock this until volley completes processing

        // progressWheel is just a loading spinner, please see the content_main.xml
        final ProgressWheel progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel_home);
        progressWheel.setVisibility(View.VISIBLE);

        itShouldLoadMore = true;

        if (!remainingRecensioneList.isEmpty()) {
            //todo aggiungere un ritardo (asynctask) per visualizzare la ruota figa di caricamento
            final int remainingRecensioneListSize = remainingRecensioneList.size();
            for (int i = 0; i < remainingRecensioneListSize; ++i) {
                if (i < LOAD_LIMIT) {
                    recensioneList.add(remainingRecensioneList.remove());
                }
            }
            recensioneAdapter.notifyDataSetChanged();
        }
        progressWheel.setVisibility(View.GONE);
    }


    @Override
    public void onRecensioniSelected(Recensione recensione) {
        Toast.makeText(getContext(), "da implementare", Toast.LENGTH_SHORT).show();
    }
}



