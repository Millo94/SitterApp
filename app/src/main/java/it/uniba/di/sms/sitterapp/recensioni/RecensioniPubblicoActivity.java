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
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import it.uniba.di.sms.sitterapp.utils.FirebaseDb;
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
        db.collection(FirebaseDb.REVIEWS)
                .whereEqualTo(FirebaseDb.REVIEW_RECEIVER, user)
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
                                final DocumentSnapshot docSnap = iterableReview.next();
                                db.collection(FirebaseDb.USERS).document(docSnap.getString(FirebaseDb.REVIEW_SENDER))
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Recensione recensione = docSnap.toObject(Recensione.class);
                                                recensione.setSender(documentSnapshot.getString(FirebaseDb.USER_NOME_COMPLETO));
                                                recensioneList.add(recensione);
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                            }
                        }

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

