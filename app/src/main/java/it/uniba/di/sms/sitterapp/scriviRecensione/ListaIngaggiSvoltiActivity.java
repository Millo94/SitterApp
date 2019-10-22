package it.uniba.di.sms.sitterapp.scriviRecensione;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import it.uniba.di.sms.sitterapp.adapter.NoticeAdapter;
import it.uniba.di.sms.sitterapp.oggetti.Notice;
import it.uniba.di.sms.sitterapp.principale.DrawerActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import tr.xip.errorview.ErrorView;

public class ListaIngaggiSvoltiActivity extends DrawerActivity implements NoticeAdapter.NoticeAdapterListener {

    protected SessionManager sessionManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
        noticeAdapter = new NoticeAdapter(ListaIngaggiSvoltiActivity.this, noticeList, ListaIngaggiSvoltiActivity.this);
        remainingNoticeList = new LinkedList<>();

        recyclerView.setAdapter(noticeAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

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

    private void caricaNotices(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        db.collection("candidatura")
                .whereEqualTo("username", sessionManager.getSessionUsername())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            if (task.getResult().isEmpty()) {
                                ErrorView errorView = (ErrorView) findViewById(R.id.errorView);
                                errorView.setSubtitle(R.string.niente_annunci);
                                errorView.setVisibility(View.VISIBLE);
                            }else{

                                String username;

                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    document.getData();

                                    String id = document.getString("id");
                                    String famiglia = document.getString("famiglia");
                                    String date = document.getString("data");
                                    String start = document.getString("start");
                                    String end = document.getString("end");
                                    String description = document.getString("descrizione");
                                    String sitter = document.getString("String");

                                    Notice candidatura = new Notice (id,famiglia,date,start,end,description,sitter);

                                    noticeList.add(candidatura);

                                }
                            }

                            noticeAdapter.notifyDataSetChanged();

                        }else{

                            Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO sostiture "Errore" con la stringa di riferimento
                        Toast.makeText(getApplicationContext(), "Errore", Toast.LENGTH_SHORT).show();
                    }
                });




    }

    // al click su un annuncio si visualizza i dettagli
    @Override
    public void onNoticeSelected(Notice notice) {

        Intent familyRev = new Intent(ListaIngaggiSvoltiActivity.this, ScriviRecensioneActivity.class);
        familyRev.putExtra("famiglia", notice.getFamily());
        familyRev.putExtra("idAnnuncio", notice.getIdAnnuncio());
        familyRev.putExtra("sitter", notice.getSitter());
        startActivity(familyRev);
    }
}
