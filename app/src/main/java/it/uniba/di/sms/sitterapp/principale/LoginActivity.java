package it.uniba.di.sms.sitterapp.principale;

import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.oggetti.UtenteSitter;
import it.uniba.di.sms.sitterapp.registrazione.RegistrationActivity;
import it.uniba.di.sms.sitterapp.SessionManager;


/**
 * Activity per il login
 */
public class LoginActivity extends AppCompatActivity {

    private EditText usernameEt, passwordEt;
    private TextView nuovoAccount,nuovoAccountFB;
    private StringRequest request;
    private RequestQueue RequestQueue;
    private SessionManager session;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Creazione del nuovo manager di sessione
        session = new SessionManager(getApplicationContext());

        usernameEt = (EditText) findViewById(R.id.lblUsername);
        passwordEt = (EditText) findViewById(R.id.lblPassword);
        nuovoAccount = (TextView) findViewById(R.id.creaAccount);


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

        final DocumentReference docRef = db.collection("utente").document(usernameEt.getText().toString());

        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if(passwordEt.getText().toString().equals(document.getString("password"))){
                                    final Integer tipoUtente = document.getLong("tipoUtente").intValue();
                                    Toast.makeText(getApplicationContext(), R.string.loginSuccess, Toast.LENGTH_LONG).show();
                                    session.createLoginSession(usernameEt.getText().toString(),  tipoUtente);
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.putExtra(Constants.TYPE, tipoUtente);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), R.string.loginerror, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //TODO cambiare messaggio di errore
                            Toast.makeText(LoginActivity.this, R.string.loginerror, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }



}
