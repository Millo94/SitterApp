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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants.Constants;
import it.uniba.di.sms.sitterapp.principale.LoginActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.oggetti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.oggetti.UtenteSitter;
import it.uniba.di.sms.sitterapp.Constants.FirebaseDb;

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
                            utente.put(FirebaseDb.USER_AVATAR, famiglia.getAvatar());
                            utente.put(FirebaseDb.USER_NOME_COMPLETO,famiglia.getName());
                            utente.put(FirebaseDb.USER_PASSWORD, famiglia.getPassword());
                            utente.put(FirebaseDb.USER_EMAIL,famiglia.getEmail());
                            utente.put(FirebaseDb.USER_TELEFONO, famiglia.getTelefono());
                            utente.put(FirebaseDb.USER_NAZIONE,famiglia.getNazione());
                            utente.put(FirebaseDb.USER_CITTA, famiglia.getCitta());
                            utente.put(FirebaseDb.USER_ONLINE, famiglia.isOnline());
                            utente.put(FirebaseDb.USER_DESCRIZIONE, famiglia.getDescrizione());
                            utente.put(FirebaseDb.USER_TIPOUTENTE, Constants.TYPE_FAMILY);
                            Map<String,Object> famigliaExtra = new HashMap<>();
                            famigliaExtra.put(FirebaseDb.FAMIGLIA_NUMFIGLI,famiglia.getNumFigli());
                            famigliaExtra.put(FirebaseDb.FAMIGLIA_ANIMALI, famiglia.getAnimali());
                            famigliaExtra.put(FirebaseDb.FAMIGLIA_RATING, famiglia.getRating());
                            utente.put(FirebaseDb.FAMIGLIA,famigliaExtra);

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            db.collection(FirebaseDb.USERS)
                                    .document(firebaseUser.getUid())
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

        mAuth.createUserWithEmailAndPassword(sitter.getEmail().toLowerCase(), sitter.getPassword())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Map<String,Object> utente = new HashMap<>();
                            utente.put(FirebaseDb.USER_PASSWORD,sitter.getPassword());
                            utente.put(FirebaseDb.USER_NOME_COMPLETO,sitter.getName());
                            utente.put(FirebaseDb.USER_EMAIL,sitter.getEmail());
                            utente.put(FirebaseDb.USER_AVATAR, sitter.getAvatar());
                            utente.put(FirebaseDb.USER_NAZIONE,sitter.getNazione());
                            utente.put(FirebaseDb.USER_CITTA, sitter.getCitta());
                            utente.put(FirebaseDb.USER_TELEFONO, sitter.getTelefono());
                            utente.put(FirebaseDb.USER_ONLINE, sitter.isOnline());
                            utente.put(FirebaseDb.USER_DESCRIZIONE, sitter.getDescrizione());
                            utente.put(FirebaseDb.USER_TIPOUTENTE,Constants.TYPE_SITTER);
                            Map<String,Object> babysitterExtra = new HashMap<>();
                            babysitterExtra.put(FirebaseDb.BABYSITTER_DATANASCITA, sitter.getDataNascita());
                            babysitterExtra.put(FirebaseDb.BABYSITTER_GENERE, sitter.getGenere());
                            babysitterExtra.put(FirebaseDb.BABYSITTER_RATING, sitter.getRating());
                            babysitterExtra.put(FirebaseDb.BABYSITTER_NUMLAVORI, sitter.getNumLavori());
                            babysitterExtra.put(FirebaseDb.BABYSITTER_AUTO,sitter.getAuto());
                            babysitterExtra.put(FirebaseDb.BABYSITTER_RETRIBUZIONE, sitter.getRetribuzioneOra());
                            babysitterExtra.put(FirebaseDb.BABYSITTER_DISPONIBILITA, new ArrayList<Long>());
                            utente.put(FirebaseDb.BABYSITTER,babysitterExtra);

                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            db.collection(FirebaseDb.USERS)
                                    .document(firebaseUser.getUid())
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
