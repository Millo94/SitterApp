package it.uniba.di.sms.sitterapp.scriviRecensione;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import it.uniba.di.sms.sitterapp.adapter.NoticeAdapter;
import it.uniba.di.sms.sitterapp.oggetti.Notice;
import it.uniba.di.sms.sitterapp.principale.DrawerActivity;
import it.uniba.di.sms.sitterapp.utils.FirebaseDb;
import tr.xip.errorview.ErrorView;

public class IngaggiDaRecensireActivity extends DrawerActivity implements NoticeAdapter.NoticeAdapterListener {

    protected SessionManager sessionManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final String SELECTED = "selected";
    //Items ingaggi
    private List<Notice> noticeList;
    private Queue<Notice> remainingNoticeList;
    private NoticeAdapter noticeAdapter;
    private ErrorView errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerHome);

        //recyclerView e adapter
        noticeList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(IngaggiDaRecensireActivity.this, noticeList, IngaggiDaRecensireActivity.this);
        remainingNoticeList = new LinkedList<>();

        //BottomNavigationView
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().findItem(getIntent().getIntExtra(SELECTED,R.id.nav_recensioni)).setChecked(true);

        recyclerView.setAdapter(noticeAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //errorview in caso non ci siano annunci da recensire
        errorView = (ErrorView) findViewById(R.id.errorView);
        errorView.setSubtitle(R.string.niente_annunci);
        //caricamento di annunci
        caricaNotices();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

    }

    /**
     * Caricamento degli ingaggi assegnati ad una baby sitter o
     * degli ingaggi che una famiglia ha pubblicato e assegnato a una babysitter.
     * se nella lista non è presenta alcun ingaggio, comparirà un messaggio di errore (ErrorView)
     */


    private void caricaNotices() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        /**recupera gli annunci eseguiti */

        db.collection(FirebaseDb.ENGAGES)
                .whereEqualTo(sessionManager.getSessionType() == Constants.TYPE_SITTER ? FirebaseDb.ENGAGES_SITTER : FirebaseDb.ENGAGES_FAMIGLIA, sessionManager.getSessionUid())
                //aggiunto controllo per recuperare solo annunci con conferma
                .whereEqualTo(FirebaseDb.ENGAGES_CONFERMA, true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                errorView.setVisibility(View.VISIBLE);
                            } else {
                                //visualizza gli ingaggi effettutati
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document.getData();
                                    final Notice notice = document.toObject(Notice.class);
                                    db.collection(FirebaseDb.REVIEWS)
                                            .whereEqualTo(FirebaseDb.REVIEW_IDANNUNCIO,notice.getIdAnnuncio())
                                            .whereEqualTo(FirebaseDb.REVIEW_SENDER,sessionManager.getSessionUid())
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot querySnapshot) {
                                                    List<DocumentSnapshot> reviewList = querySnapshot.getDocuments();
                                                    if(reviewList.isEmpty()){
                                                        if (noticeAdapter.annuncioScaduto(notice) == true) {
                                                            errorView.setVisibility(View.INVISIBLE);
                                                            noticeList.add(notice);
                                                            noticeAdapter.notifyDataSetChanged();
                                                        }
                                                    }else{
                                                        errorView.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            });
                                    //mostra solo gli ingaggi già completati (ovvero scaduti) per recensirli

                                }

                            }
                        } else {

                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.genericError, Toast.LENGTH_SHORT).show();
                    }
                });


    }

    // al click su un annuncio si visualizza i dettagli
    @Override
    public void onNoticeSelected(Notice notice) {

        Intent familyRev = new Intent(IngaggiDaRecensireActivity.this, ScriviRecensioneActivity.class);
        familyRev.putExtra("famiglia", notice.getFamily());
        familyRev.putExtra("idAnnuncio", notice.getIdAnnuncio());
        familyRev.putExtra("sitter", notice.getSitter());
        startActivity(familyRev);
    }
}
