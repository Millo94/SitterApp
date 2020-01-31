package it.uniba.di.sms.sitterapp.appuntamenti;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.adapter.SitterAdapter;
import it.uniba.di.sms.sitterapp.oggetti.Notice;
import it.uniba.di.sms.sitterapp.oggetti.UtenteSitter;
import it.uniba.di.sms.sitterapp.principale.DrawerActivity;
import it.uniba.di.sms.sitterapp.profilo.ProfiloPubblicoActivity;
import tr.xip.errorview.ErrorView;

/**
 * Sezione per le candidature: assegnare un lavoro a una baby sitter
 */
public class SceltaSitter extends DrawerActivity
        implements SitterAdapter.ContactsSitterAdapterListener {

    private static final String SELECTED = "selected";
    private List<UtenteSitter> sitterList;
    private SitterAdapter sitterAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idAnnuncio;
    private BottomNavigationView bottomNavigationView;
    private ErrorView errorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // caricamento degli annunci nella recyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerHome);

        //error view in caso non ci siano babysitter candidate
        errorView = (ErrorView) findViewById(R.id.errorView);
        errorView.setTitle(R.string.niente_sitter);

        sitterList = new ArrayList<>();
        sitterAdapter = new SitterAdapter(SceltaSitter.this, sitterList, SceltaSitter.this);


        recyclerView.setAdapter(sitterAdapter);

        //BottomNavigationView
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().findItem(getIntent().getIntExtra(SELECTED,R.id.nav_engagements)).setChecked(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        idAnnuncio = getIntent().getStringExtra("idAnnuncio");

        loadSitter();

    }


    /**
     * Metodo per caricare la lista delle babysitter candidate all'annuncio di competenza
     */
    private void loadSitter(){
        db.collection("annuncio")
                .document(idAnnuncio)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        final DocumentSnapshot docRef = task.getResult();
                        Notice notice =  docRef.toObject(Notice.class);
                        Map<String, Object> listSitter = notice.getCandidatura();
                        if(listSitter.isEmpty()){
                            errorView.setVisibility(View.VISIBLE);
                        }else{
                            errorView.setVisibility(View.INVISIBLE);
                            for(String u : listSitter.keySet()) {
                            FirebaseFirestore db2 = FirebaseFirestore.getInstance();
                            db2.collection("utente")
                                    .document(listSitter.get(u).toString())
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                            UtenteSitter uS = new UtenteSitter(
                                                    documentSnapshot.getId(),
                                                    documentSnapshot.getString("NomeCompleto"),
                                                    documentSnapshot.getString("Avatar"),
                                                    documentSnapshot.getBoolean("online"));
                                            sitterList.add(uS);
                                            sitterAdapter.notifyDataSetChanged();
                                        }
                                    });
                            //sitterList.add(new UtenteSitter(u,u,"gs://sitterapp-223aa.appspot.com/img/user_img/de00fa77-e3a6-4fe8-a4bb-f7bda5e4102e",true));
                        }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SceltaSitter.this, R.string.genericError, Toast.LENGTH_SHORT).show();
                    }
                });
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
                        Intent detailIntent = new Intent(SceltaSitter.this, ProfiloPubblicoActivity.class);
                        detailIntent.putExtra(Constants.TYPE, Constants.TYPE_SITTER);
                        detailIntent.putExtra("uid", sitter.getId());
                        startActivity(detailIntent);
                        break;
                    //assegna incarico
                    case 1:
                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SceltaSitter.this);


                        builder.setTitle(R.string.assegnaIncarico)
                                .setMessage(R.string.confermaIncarico)
                                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        chooseSitter(sitter.getId());
                                    }
                                })
                                .create()
                                .show();
                        break;

                    default:
                        break;
                }

            }
        });
        builder.show();
    }


    public void chooseSitter(final String userID){

        DocumentReference docRef = db.collection("annuncio")
                .document(idAnnuncio);

        docRef.update("sitter",userID)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SceltaSitter.this, "lavoro assegnato", Toast.LENGTH_SHORT).show();
                        Intent intentback = new Intent(SceltaSitter.this, IngaggiActivity.class);
                        startActivity(intentback);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SceltaSitter.this, "", Toast.LENGTH_SHORT).show();
                    }
                });


    }

}
