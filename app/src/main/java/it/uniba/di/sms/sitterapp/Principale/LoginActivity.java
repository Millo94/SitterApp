package it.uniba.di.sms.sitterapp.Principale;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Registrazione.RegistrationActivity;
import it.uniba.di.sms.sitterapp.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEt, passwordEt;
    private TextView nuovoAccount;
    private StringRequest request;
    private RequestQueue RequestQueue;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Creazione del nuovo manager di sessione
        session = new SessionManager(getApplicationContext());

        usernameEt = (EditText) findViewById(R.id.lblUsername);
        passwordEt = (EditText) findViewById(R.id.lblPassword);
        nuovoAccount = (TextView) findViewById(R.id.creaAccount);
        nuovoAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence registrazione[] = new CharSequence[]{getString(R.string.nuovaFamiglia), getString(R.string.newSitter)};

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
                        switch (which) {
                            case 0:
                                intent.putExtra("type", Constants.TYPE_FAMILY);
                                break;
                            case 1:
                                intent.putExtra("type", Constants.TYPE_SITTER);
                                break;
                            default:
                                break;
                        }

                        startActivity(intent);
                    }
                });


                AlertDialog alert = builder.create();
                alert.show();
                Button btn = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                btn.setTextColor(getResources().getColor(R.color.colorPrimaryDark));


            }
        });

        RequestQueue = Volley.newRequestQueue(this);
    }

    public void onLogin(View view) {
        request = new StringRequest(Request.Method.POST, Php.LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String result = jsonObject.getString("login");

                            if (result.equals("true")) {

                                Toast.makeText(getApplicationContext(), R.string.loginSuccess, Toast.LENGTH_LONG).show();
                                session.createLoginSession(usernameEt.getText().toString(), jsonObject.getInt("tipoUtente"));

                                if (jsonObject.getString("tipoUtente").equals(String.valueOf(Constants.TYPE_SITTER))) {

                                    session.setProfilePic(jsonObject.getString("pathFoto"));
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.putExtra(Constants.TYPE, Constants.TYPE_SITTER);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();

                                } else if (jsonObject.getString("tipoUtente").equals(String.valueOf(Constants.TYPE_FAMILY))) {

                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.putExtra(Constants.TYPE, Constants.TYPE_FAMILY);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }

                            } else if (result.equals("false")) {

                                Toast.makeText(getApplicationContext(), R.string.loginerror, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, R.string.loginerror, Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", usernameEt.getText().toString());
                params.put("password", passwordEt.getText().toString());
                return params;
            }
        };
        RequestQueue.add(request);
    }


}
