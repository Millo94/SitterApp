package it.uniba.di.sms.sitterapp.Appuntamenti;

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
import it.uniba.di.sms.sitterapp.Principale.DrawerActivity;
import it.uniba.di.sms.sitterapp.R;

public class IngaggiActivity extends DrawerActivity implements NoticeAdapter.NoticeAdapterListener{

    // URL da dove prende gli appuntamenti
    private static final String URL_INGAGGI = Constants.BASE_URL + "ingaggi.php";

    // Vista
    private RecyclerView recyclerView;

    //Items ingaggi
    private List<Notice> noticeList;
    private Queue<Notice> remainingNoticeList;
    private NoticeAdapter noticeAdapter;

    // we will be loading 15 items per page or per load
    // you can change this to fit your specifications.
    // When you change this, there will be no need to update your php page,
    // as php will be ordered what to load and limit by android java
    public static final int LOAD_LIMIT = 5;

    // we need this variable to lock and unlock loading more
    // e.g we should not load more when volley is already loading,
    // loading will be activated when volley completes loading
    private boolean itShouldLoadMore = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerHome);

        noticeList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(IngaggiActivity.this, noticeList, IngaggiActivity.this);
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

    /**
     * Caricamento degli annunci (per la home delle babysitter)
     */
    private void loadNotices() {

        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_INGAGGI,
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
                                descrizione = (descrizione.length() > 100) ? descrizione.substring(0, 100) + "..." : descrizione;
                                Notice n = new Notice(famiglia, data, oraInizio, oraFine, descrizione);

                                if (i < LOAD_LIMIT) {
                                    noticeList.add(n);
                                } else {
                                    remainingNoticeList.add(n);
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
                Toast.makeText(IngaggiActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", sessionManager.getSessionUsername());
                params.put("tipoutente", String.valueOf(sessionManager.getSessionType()));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }

    /**
     * Caricamento incrementale degli annunci
     */
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
            noticeAdapter.notifyDataSetChanged();
        }
        progressWheel.setVisibility(View.GONE);
    }

    /**
     * @param notice al click su un annuncio visualizza i dettagli
     */
    @Override
    public void onNoticeSelected(Notice notice) {

        if(sessionManager.getSessionType() == Constants.TYPE_SITTER) {
            Intent detailIntent = new Intent(IngaggiActivity.this, NoticeDetailActivity.class);
            detailIntent.putExtra("famiglia", notice.getFamily());
            detailIntent.putExtra("data", notice.getDate());
            detailIntent.putExtra("oraInizio", notice.getStart_time());
            detailIntent.putExtra("oraFine", notice.getEnd_time());
            detailIntent.putExtra("descrizione", notice.getDescription());
            startActivity(detailIntent);
        } else {
            Toast.makeText(IngaggiActivity.this,"Ancora da implementare", Toast.LENGTH_SHORT).show();
        }
    }
}
