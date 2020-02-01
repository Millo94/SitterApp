package it.uniba.di.sms.sitterapp.scriviRecensione;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants.Constants;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import it.uniba.di.sms.sitterapp.oggetti.Recensione;
import it.uniba.di.sms.sitterapp.recensioni.RecensioniActivity;
import it.uniba.di.sms.sitterapp.Constants.FirebaseDb;

/**
 * Classe che gestisce la parte della scrittura delle recensioni
 */

public class ScriviRecensioneActivity extends AppCompatActivity {

    TextView user;
    EditText desc;
    Float rate = new Float(0.00);
    RatingBar rating;
    String commento = "null";
    Button scriviRec;
    SessionManager sessionManager;
    String famiglia;
    String id;
    String sitter;
    public static Intent intentReview;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrivi_recensione);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(getApplicationContext());

        famiglia = getIntent().getStringExtra("famiglia");
        id = getIntent().getStringExtra("idAnnuncio");
        sitter = getIntent().getStringExtra("sitter");


        user = (TextView) findViewById(R.id.usernameRecensione);
        desc = (EditText) findViewById(R.id.editRecensione);
        rating = (RatingBar) findViewById(R.id.ratingBarRecensione);
        rating.setEnabled(true);
        scriviRec = (Button) findViewById(R.id.inviaRecensione);
        scriviRec.setOnClickListener(inviarecListener);

        final String userID;
        if (sessionManager.getSessionType() == Constants.TYPE_SITTER) {
            userID = famiglia;
        } else {
            userID = sitter;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("utente")
                .document(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                         if(task.isSuccessful()){
                             DocumentSnapshot documentSnapshot = task.getResult();
                             user.setText(documentSnapshot.getString("NomeCompleto"));
                         }else{
                             user.setText(userID);
                         }
                    }
                });


    }

    View.OnClickListener inviarecListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            commento = desc.getText().toString();
            rate = rating.getRating();
            //cattura l'intent dell'ingaggio recensito e ne prende l'idAnnuncio da associare alla recensione
            intentReview = getIntent();
            String idAnnuncio = intentReview.getStringExtra("idAnnuncio");
            String fam = intentReview.getStringExtra("famiglia");
            String bs = intentReview.getStringExtra("sitter");
            //se manda la recensione (utente in sessione) utente tipo sitter allora recensisci famiglia, altrimenti recensisci babysitter
            String receiver = sessionManager.getSessionType() == Constants.TYPE_SITTER ? fam : bs;
            String sender = sessionManager.getSessionUid();
            Recensione recensione = new Recensione(commento, rate, idAnnuncio, receiver, sender);

            if (rate == 0.0 || commento.isEmpty()) {
                Toast.makeText(ScriviRecensioneActivity.this, R.string.missingFields, Toast.LENGTH_SHORT).show();
            } else

                inviaDati(recensione);

        }
    };


    /**
     *
     * Metodo per l'invio di una recensione sia da parte della babysitter che da parte della famiglia.
     *
     *
     */
    public void inviaDati(final Recensione recensione) {

        Map<String, Object> comment = new HashMap<>();
        comment.put("tipoUtente", sessionManager.getSessionType());
        comment.put("descrizione", recensione.getDescrizione());
        comment.put("rating", recensione.getRating());
        comment.put("idAnnuncio", recensione.getIdAnnuncio());
        comment.put("receiver", recensione.getReceiver());
        comment.put("sender", recensione.getSender());

        //aggiorna il rating dell'utente recensito
        setRatingUser(recensione.getReceiver(),recensione.getRating(),sessionManager.getSessionType());

        //invio delle recensione
        db.collection("recensione")
                .add(comment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), R.string.recensioneEffettuata, Toast.LENGTH_LONG).show();
                        Intent backIntent = new Intent(ScriviRecensioneActivity.this, RecensioniActivity.class);
                        startActivity(backIntent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), R.string.genericError, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void setRatingUser(final String uid, final Float newVote, final int TYPE) {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(FirebaseDb.REVIEWS).whereEqualTo(FirebaseDb.REVIEW_RECEIVER, uid)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    float sumRating = 0;
                    int i = 0;
                    for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                        sumRating += queryDocumentSnapshot.getDouble(FirebaseDb.REVIEW_RATING).floatValue();
                        i++;
                    }

                    final Float newRating = (sumRating + newVote) / (i + 1);

                    String attributeToUpdate;
                    if (TYPE == Constants.TYPE_FAMILY) {
                        attributeToUpdate = FirebaseDb.BABYSITTER+"."+FirebaseDb.BABYSITTER_RATING;
                    } else {
                        attributeToUpdate = FirebaseDb.FAMIGLIA+"."+FirebaseDb.FAMIGLIA_RATING;
                    }
                    db.collection(FirebaseDb.USERS).document(uid).update(attributeToUpdate, newRating);
                }
        }
                });
    }
}