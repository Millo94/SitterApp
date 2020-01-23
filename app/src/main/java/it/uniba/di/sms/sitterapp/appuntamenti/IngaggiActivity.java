package it.uniba.di.sms.sitterapp.appuntamenti;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.adapter.NoticeAdapter;
import it.uniba.di.sms.sitterapp.oggetti.Notice;
import it.uniba.di.sms.sitterapp.principale.DrawerActivity;
import it.uniba.di.sms.sitterapp.principale.NewNoticeActivity;
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
     * Mostra gli ingaggi creati
     */
    private void caricaIngaggi(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.dismiss();

        CollectionReference colRef = db.collection("annuncio");

        if(sessionManager.getSessionType() == Constants.TYPE_FAMILY){
            colRef
                    .whereEqualTo("family", sessionManager.getNomeCompleto())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            Iterator<QueryDocumentSnapshot> listNotice = queryDocumentSnapshots.iterator();
                            ErrorView errorView = (ErrorView) findViewById(R.id.errorView);
                            if (!listNotice.hasNext()) {
                                errorView.setTitle(R.string.niente_annunci);
                                errorView.setVisibility(View.VISIBLE);

                            } else {

                                while (listNotice.hasNext()) {
                                    DocumentSnapshot documentSnapshot = listNotice.next();
                                    Notice notice = documentSnapshot.toObject(Notice.class);
                                    noticeList.add(notice);
                                }
                                noticeAdapter.notifyDataSetChanged();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(IngaggiActivity.this, R.string.genericError, Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            colRef
                    .whereEqualTo("candidatura." + sessionManager.getSessionUid(), sessionManager.getSessionUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            Iterator<QueryDocumentSnapshot> listNotice = queryDocumentSnapshots.iterator();
                            while(listNotice.hasNext()){
                                DocumentSnapshot documentSnapshot = listNotice.next();
                                Notice notice = documentSnapshot.toObject(Notice.class);
                                noticeList.add(notice);
                            }
                            noticeAdapter.notifyDataSetChanged();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(IngaggiActivity.this,R.string.genericError, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // al click su un annuncio visualizza i dettagli
    @Override
    public void onNoticeSelected(Notice notice) {

        DialogsNoticeDetails dialogs = DialogsNoticeDetails.newInstance(notice, sessionManager.getSessionUid());
        dialogs.hideButton();
        dialogs.show(getSupportFragmentManager(), "dialog");

    }

}
