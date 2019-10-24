package it.uniba.di.sms.sitterapp.appuntamenti;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.CollectionReference;
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

import it.uniba.di.sms.sitterapp.adapter.NoticeAdapter;
import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.oggetti.Notice;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.principale.DrawerActivity;
import it.uniba.di.sms.sitterapp.principale.HomeActivity;
import it.uniba.di.sms.sitterapp.principale.NewNoticeActivity;
import it.uniba.di.sms.sitterapp.R;
import tr.xip.errorview.ErrorView;

public class IngaggiActivity extends DrawerActivity implements NoticeAdapter.NoticeAdapterListener {

    //Items ingaggi
    private List<Notice> noticeList;
    private NoticeAdapter noticeAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //FAB
        FloatingActionButton addNotice = (FloatingActionButton) findViewById(R.id.addNotice);
        if (sessionManager.getSessionType() == Constants.TYPE_FAMILY && addNotice.getVisibility() == View.GONE) {
            addNotice.show();
            addNotice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(IngaggiActivity.this, NewNoticeActivity.class);
                    startActivity(intent);
                }
            });
        } else if (sessionManager.getSessionType() == Constants.TYPE_SITTER && addNotice.getVisibility() == View.VISIBLE) {
            addNotice.hide();
        }

        //caricamento degli annunci
        noticeList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(IngaggiActivity.this, noticeList, IngaggiActivity.this);

        //RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerHome);
        recyclerView.setAdapter(noticeAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    @Override
    protected void onStart() {
        super.onStart();
        noticeList.clear();
        caricaIngaggi();
    }


    /**
     * Mostra gli ingaggi creati da una famiglia
     * //TODO aggiungere la possibilità per la babysitter.
     */
    private void caricaIngaggi(){

        CollectionReference colRef = db.collection("annuncio");

        colRef
                .whereEqualTo("family", sessionManager.getSessionUsername())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        Iterator<QueryDocumentSnapshot> iterableCandidature = queryDocumentSnapshots.iterator();
                        while(iterableCandidature.hasNext()){
                            DocumentSnapshot documentSnapshot = iterableCandidature.next();
                            Notice notice = documentSnapshot.toObject(Notice.class);
                            noticeList.add(notice);
                        }
                        noticeAdapter.notifyDataSetChanged();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO Sostituire "Errore" con la stringa di errore di riferimento.
                        //Toast.makeText(HomeActivity.this, "Errore", Toast.LENGTH_SHORT).show();
                    }
                });

    }

   /* //Caricamento degli annunci dal database
    private void loadNotices() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.dismiss();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Php.INGAGGI,
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
                                    Notice n = new Notice(idAnnuncio, famiglia, data, oraInizio, oraFine, descrizione);

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
                Toast.makeText(IngaggiActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("operation", "load");
                params.put("username", sessionManager.getSessionUsername());
                params.put("tipoutente", String.valueOf(sessionManager.getSessionType()));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);
    }
*/
    // al click su un annuncio visualizza i dettagli
    @Override
    public void onNoticeSelected(Notice notice) {

        DialogsNoticeDetails dialogs = DialogsNoticeDetails.newInstance(notice);
        dialogs.hideButton();
        dialogs.show(getSupportFragmentManager(), "dialog");

    }

}
