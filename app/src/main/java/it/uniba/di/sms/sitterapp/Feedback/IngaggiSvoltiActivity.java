package it.uniba.di.sms.sitterapp.Feedback;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

import it.uniba.di.sms.sitterapp.Adapter.NoticeAdapter;
import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Oggetti.Notice;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.Principale.DrawerActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import tr.xip.errorview.ErrorView;

public class IngaggiSvoltiActivity extends DrawerActivity implements NoticeAdapter.NoticeAdapterListener {

    protected SessionManager sessionManager;

    //Items ingaggi
    private List<Notice> noticeList;
    private Queue<Notice> remainingNoticeList;
    private NoticeAdapter noticeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerHome);

        //recyclerView e adapter
        noticeList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(IngaggiSvoltiActivity.this, noticeList, IngaggiSvoltiActivity.this);
        remainingNoticeList = new LinkedList<>();

        recyclerView.setAdapter(noticeAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //caricamento di annunci
        loadNotices();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    // Caricamento degli ingaggi assegnati ad una baby sitter o
    // degli ingaggi che una famiglia ha pubblicato e assegnato a una babysitter.
    // se nella lista non è presenta alcun ingaggio, comparirà un messaggio di errore (ErrorView)

    private void loadNotices() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Php.UTENZE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        try {
                            JSONArray notice = new JSONArray(response);

                            if (notice.length() == 0) {
                                ErrorView errorView = (ErrorView) findViewById(R.id.errorView);
                                errorView.setSubtitle(R.string.niente_annunci);
                                errorView.setVisibility(View.VISIBLE);
                            } else {
                                for (int i = 0; i < notice.length(); i++) {

                                    JSONObject noticeObject = notice.getJSONObject(i);
                                    String idAnnuncio = noticeObject.getString("idAnnuncio");
                                    String famiglia = noticeObject.getString("usernameFamiglia");
                                    String data = Constants.SQLtoDate(noticeObject.getString("data"));
                                    String oraInizio = noticeObject.getString("oraInizio");
                                    String oraFine = noticeObject.getString("oraFine");
                                    String descrizione = noticeObject.getString("descrizione");
                                    String sitter = noticeObject.getString("babysitter");
                                    Notice n = new Notice(idAnnuncio, famiglia, data, oraInizio, oraFine, descrizione, sitter);

                                    noticeList.add(n);
                                }
                            }
                            noticeAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(IngaggiSvoltiActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", sessionManager.getSessionUsername());
                params.put("type", String.valueOf(sessionManager.getSessionType()));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    // al click su un annuncio si visualizza i dettagli
    @Override
    public void onNoticeSelected(Notice notice) {

        Intent familyRev = new Intent(IngaggiSvoltiActivity.this, ScriviRecensioneActivity.class);
        familyRev.putExtra("famiglia", notice.getFamily());
        familyRev.putExtra("idAnnuncio", notice.getIdAnnuncio());
        familyRev.putExtra("sitter", notice.getSitter());
        startActivity(familyRev);
    }
}
