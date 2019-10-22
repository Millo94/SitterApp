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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import it.uniba.di.sms.sitterapp.oggetti.Recensione;

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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrivi_recensione);

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

        if (sessionManager.getSessionType() == Constants.TYPE_SITTER) {
            user.setText(famiglia);
        } else if (sessionManager.getSessionType() == Constants.TYPE_FAMILY) {
            user.setText(sitter);
        }

    }

    View.OnClickListener inviarecListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            commento = desc.getText().toString();
            rate = rating.getRating();

            Recensione recensione = new Recensione(sessionManager.getSessionUsername(),commento, rate);

            if (rate == 0.0 || commento.isEmpty()) {
                Toast.makeText(ScriviRecensioneActivity.this, R.string.missingFields, Toast.LENGTH_SHORT).show();
            } else

                inviaDati(recensione);

        }
    };


    /**
     *
     * Metodo per l'invio di una recensione sia da parte della babysitter che da parte della famiglia.
     * /TODO Inserire un controllo che permetta di inviare la recensione sull'annuncio desiderato
     *
     */
    public void inviaDati(final Recensione recensione) {

        Map<String, Object> comment = new HashMap<>();
        comment.put("username", recensione.getUsername());
        comment.put("tipoUtente", sessionManager.getSessionType());
        comment.put("descrizione", recensione.getDescrizione());
        comment.put("rating", recensione.getRating());

        db.collection("recensione")
                .add(comment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(), R.string.recensioneEffettuata, Toast.LENGTH_LONG).show();
                        Intent backIntent = new Intent(ScriviRecensioneActivity.this, ListaIngaggiSvoltiActivity.class);
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

}
