package it.uniba.di.sms.sitterapp.registrazione;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.principale.LoginActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.oggetti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.oggetti.UtenteSitter;

public class RegistrationActivity extends AppCompatActivity implements SitterRegistrationFragment.OnFragmentInteractionListener, FamilyRegistrationFragment.OnFragmentInteractionListener {

    private static final String TAG = "RegistrationActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);
        /*
         * L'intent contiene un valore intero "type" che indica alla activity se il
         * fragment da visualizzare è quello della registrazione della famiglia, o quello della
         * registrazione della babysitter
         */
        int type = getIntent().getIntExtra(Constants.TYPE, -1);

        if (type == Constants.TYPE_SITTER) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.viewPager, new SitterRegistrationFragment()).commit();
        } else if (type == Constants.TYPE_FAMILY) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.viewPager, new FamilyRegistrationFragment()).commit();
        }


    }

    // Interaction con il fragment Sitter
    @Override
    public void onFragmentInteraction(UtenteFamiglia famiglia) {
        register(famiglia);
    }

    // Interaction con il fragment Family
    @Override
    public void onFragmentInteraction(UtenteSitter sitter) {
        register(sitter);
    }

    //volley per la registrazione di un utente famiglia
    private void register(final UtenteFamiglia famiglia) {
        Map<String,Object> utente = new HashMap<>();
        utente.put("password",famiglia.getPassword());
        utente.put("tipoUtente", Constants.TYPE_FAMILY);
        Map<String,Object> famigliaExtra = new HashMap<>();
        famigliaExtra.put("nome",famiglia.getNome());
        famigliaExtra.put("cognome", famiglia.getCognome());
        famigliaExtra.put("email",famiglia.getEmail());
        famigliaExtra.put("numero", famiglia.getNumero());
        famigliaExtra.put("nazione",famiglia.getNazione());
        famigliaExtra.put("cap", famiglia.getCap());
        famigliaExtra.put("numFigli",famiglia.getNumFigli());
        famigliaExtra.put("animali", famiglia.getAnimali());
        utente.put("famiglia",famigliaExtra);
        db.collection("utente")
                .document(famiglia.getUsername())
                .set(utente)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), R.string.registrationSuccess, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // termina tutte le activity sopra quella chiamata
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getApplicationContext(), R.string.registrationFail, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //volley per la registrazione di un utente sitter
    private void register(final UtenteSitter sitter) {
        Map<String,Object> utente = new HashMap<>();
        utente.put("password",sitter.getPassword());
        utente.put("tipoUtente",Constants.TYPE_SITTER);
        utente.put("babysitter",sitter);
        Map<String,Object> babysitterExtra = new HashMap<>();
        babysitterExtra.put("nome",sitter.getNome());
        babysitterExtra.put("cognome", sitter.getCognome());
        babysitterExtra.put("email",sitter.getEmail());
        babysitterExtra.put("numero", sitter.getNumero());
        babysitterExtra.put("nazione",sitter.getNazione());
        babysitterExtra.put("cap", sitter.getCap());
        babysitterExtra.put("auto",sitter.getAuto());
        babysitterExtra.put("foto", sitter.getFoto());
        babysitterExtra.put("genere", sitter.getGenere());
        babysitterExtra.put("numLavori", sitter.getNumLavori());
        babysitterExtra.put("rating", sitter.getRating());
        babysitterExtra.put("dataNascita", sitter.getDataNascita());
        utente.put("babysitter",babysitterExtra);
        db.collection("utente")
                .document(sitter.getUsername())
                .set(utente)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), R.string.registrationSuccess, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // termina tutte le activity sopra quella chiamata
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getApplicationContext(), R.string.registrationFail, Toast.LENGTH_SHORT).show();
                    }
                });
    }


}