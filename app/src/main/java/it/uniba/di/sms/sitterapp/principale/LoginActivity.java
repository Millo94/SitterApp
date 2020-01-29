package it.uniba.di.sms.sitterapp.principale;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import it.uniba.di.sms.sitterapp.registrazione.RegistrationActivity;


/**
 * Activity per il login
 */
public class LoginActivity extends AppCompatActivity {

    private EditText emailEt, passwordEt;
    private TextView nuovoAccount,nuovoAccountFB;
    private StringRequest request;
    private RequestQueue RequestQueue;
    private SessionManager session;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Creazione del nuovo manager di sessione
        session = new SessionManager(getApplicationContext());

        emailEt = (EditText) findViewById(R.id.lblEmail);
        passwordEt = (EditText) findViewById(R.id.lblPassword);
        nuovoAccount = (TextView) findViewById(R.id.creaAccount);
        mAuth = FirebaseAuth.getInstance();


        //aller dialog per la scelta del tipo del nuovo utente
        nuovoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence registrazione[] = new CharSequence[]{getString(R.string.nuovaFamiglia), getString(R.string.nuovaSitter)};

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                builder.setTitle(R.string.registrazione);
                builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setItems(registrazione, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                        intent.putExtra("type", which);
                        startActivity(intent);
                    }
                });

                builder.show();


            }
        });



    }

    public void onLogin(View view) {

        mAuth.signInWithEmailAndPassword(emailEt.getText().toString().trim().toLowerCase(), passwordEt.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            final FirebaseUser currentUser = mAuth.getCurrentUser();
                            db.collection("utente")
                                    .document(currentUser.getUid())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                final Integer tipoUtente = documentSnapshot.getLong("tipoUtente").intValue();
                                                Toast.makeText(getApplicationContext(), R.string.loginSuccess, Toast.LENGTH_LONG).show();
                                                session.createLoginSession(emailEt.getText().toString(), currentUser.getUid(), tipoUtente, documentSnapshot.getString("NomeCompleto"));
                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                intent.putExtra(Constants.TYPE, tipoUtente);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                Toast.makeText(LoginActivity.this, R.string.genericError, Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }else{
                            Toast.makeText(getApplicationContext(), R.string.loginerror, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
