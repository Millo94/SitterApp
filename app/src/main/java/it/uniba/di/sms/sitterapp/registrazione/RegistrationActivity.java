package it.uniba.di.sms.sitterapp.registrazione;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    FirebaseAuth mAuth;

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

        mAuth = FirebaseAuth.getInstance();

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

        mAuth.createUserWithEmailAndPassword(famiglia.getEmail(),famiglia.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<String,Object> utente = new HashMap<>();
                            utente.put("password", famiglia.getPassword());
                            utente.put("tipoUtente", Constants.TYPE_FAMILY);
                            Map<String,Object> famigliaExtra = new HashMap<>();
                            famigliaExtra.put("NomeCompleto",famiglia.getName());
                            famigliaExtra.put("Avatar",famiglia.getAvatar());
                            famigliaExtra.put("E-mail",famiglia.getEmail());
                            famigliaExtra.put("Telefono", famiglia.getTelefono());
                            famigliaExtra.put("Nazione",famiglia.getNazione());
                            famigliaExtra.put("Citta", famiglia.getCitta());
                            famigliaExtra.put("numFigli",famiglia.getNumFigli());
                            famigliaExtra.put("Animali", famiglia.getAnimali());
                            famigliaExtra.put("online", famiglia.isOnline());
                            famigliaExtra.put("rating", famiglia.getRating());
                            utente.put("famiglia",famigliaExtra);
                            db.collection("utente")
                                    .document(task.getResult().getUser().getUid())
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
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                        }

                        // ...
                    }
                });

    }

    //volley per la registrazione di un utente sitter
    private void register(final UtenteSitter sitter) {

        mAuth.createUserWithEmailAndPassword(sitter.getEmail(),sitter.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<String,Object> utente = new HashMap<>();
                            utente.put("password",sitter.getPassword());
                            utente.put("tipoUtente",Constants.TYPE_SITTER);
                            utente.put("babysitter",sitter);
                            Map<String,Object> babysitterExtra = new HashMap<>();
                            babysitterExtra.put("NomeCompleto",sitter.getName());
                            babysitterExtra.put("Email",sitter.getEmail());
                            babysitterExtra.put("Avatar", sitter.getAvatar());
                            babysitterExtra.put("Nazione",sitter.getNazione());
                            babysitterExtra.put("Citta", sitter.getCitta());
                            babysitterExtra.put("Telefono", sitter.getTelefono());
                            babysitterExtra.put("online", sitter.isOnline());
                            babysitterExtra.put("dataNascita", sitter.getDataNascita());
                            babysitterExtra.put("Genere", sitter.getGenere());
                            babysitterExtra.put("Rating", sitter.getRating());
                            babysitterExtra.put("numLavori", sitter.getNumLavori());
                            babysitterExtra.put("Auto",sitter.getAuto());
                            babysitterExtra.put("Retribuzione", sitter.getRetribuzioneOra());
                            utente.put("babysitter",babysitterExtra);
                            db.collection("utente")
                                    .document()
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

                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                        }

                        // ...
                    }
                });
                        }



    }
