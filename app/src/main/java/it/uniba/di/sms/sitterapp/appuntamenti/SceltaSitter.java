package it.uniba.di.sms.sitterapp.appuntamenti;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.uniba.di.sms.sitterapp.adapter.SitterAdapter;
import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.oggetti.Notice;
import it.uniba.di.sms.sitterapp.oggetti.UtenteSitter;
import it.uniba.di.sms.sitterapp.principale.DrawerActivity;
import it.uniba.di.sms.sitterapp.profilo.ProfiloPubblicoActivity;
import it.uniba.di.sms.sitterapp.R;

/**
 * Sezione per le candidature: assegnare un lavoro a una baby sitter
 */
public class SceltaSitter extends DrawerActivity
        implements SitterAdapter.ContactsSitterAdapterListener {

    private List<UtenteSitter> sitterList;
    private SitterAdapter sitterAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idAnnuncio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // caricamento degli annunci nella recyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerHome);

        sitterList = new ArrayList<>();
        sitterAdapter = new SitterAdapter(SceltaSitter.this, sitterList, SceltaSitter.this);


        recyclerView.setAdapter(sitterAdapter);

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
                        DocumentSnapshot docRef = task.getResult();
                        Notice notice =  docRef.toObject(Notice.class);
                        Map<String, Object> listSitter = notice.getCandidatura();
                        for(String u : listSitter.keySet()){
                            UtenteSitter uS = new UtenteSitter(u,(String) listSitter.get(u));
                            sitterList.add(uS);
                        }
                        sitterAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SceltaSitter.this, "", Toast.LENGTH_SHORT).show();
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
                        detailIntent.putExtra("username", sitter.getUsername());
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
                                        chooseSitter(sitter.getUsername());
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


    public void chooseSitter(final String username){

        DocumentReference docRef = db.collection("annuncio")
                .document(idAnnuncio);

        docRef.update("sitter",username)
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
