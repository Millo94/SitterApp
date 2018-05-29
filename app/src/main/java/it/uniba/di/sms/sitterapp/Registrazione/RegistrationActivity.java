package it.uniba.di.sms.sitterapp.Registrazione;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import it.uniba.di.sms.sitterapp.Principale.LoginActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Oggetti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.Oggetti.UtenteSitter;

public class RegistrationActivity extends AppCompatActivity implements SitterRegistrationFragment.OnFragmentInteractionListener, FamilyRegistrationFragment.OnFragmentInteractionListener {

    /**
     * RequestQueue della registrazione
     */
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);

        /**
         * requestQueue valorizzato
         */
        requestQueue = Volley.newRequestQueue(this);

        /**
         * L'intent contiene un valore intero "type" che indica alla activity se il
         * fragment da visualizzare è quello della registrazione della famiglia, o quello della
         * registrazione della babysitter
         */
        int type = getIntent().getIntExtra(Constants.TYPE, -1);

        if (type == Constants.TYPE_SITTER){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SitterRegistrationFragment()).commit();
        } else if(type == Constants.TYPE_FAMILY) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FamilyRegistrationFragment()).commit();
        }


    }

    // Interaction con il fragment Sitter
    @Override
    public void onFragmentInteraction(UtenteFamiglia famiglia) { register(famiglia); }

    // Interaction con il fragment Family
    @Override
    public void onFragmentInteraction(UtenteSitter sitter) {
        register(sitter);
    }

    private void register(final UtenteFamiglia famiglia){

        StringRequest registrationRequest = new StringRequest(Request.Method.POST, Php.REGISTRAZIONE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("response");

                    if (result.equals("true")) {
                        Toast.makeText(getApplicationContext(), R.string.registrationSuccess, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent( RegistrationActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // termina tutte le activity sopra quella chiamata
                        startActivity(intent);
                    } else if(result.equals("username")){
                        Toast.makeText(getApplicationContext(), R.string.usedUsername ,Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.registrationFail ,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), R.string.registrationFail, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type", String.valueOf(Constants.TYPE_FAMILY));
                params.put("username", famiglia.getUsername());
                params.put("password", famiglia.getPassword());
                params.put("email", famiglia.getEmail());
                params.put("nome", famiglia.getNome());
                params.put("cognome", famiglia.getCognome());
                params.put("telefono", famiglia.getNumero());
                params.put("nazione", famiglia.getNazione());
                params.put("cap", famiglia.getCap());
                params.put("numFigli", famiglia.getNumFigli());
                params.put("animali", famiglia.getAnimali());
                return params;
            }
        };

        requestQueue.add(registrationRequest);

    }

    private void register(final UtenteSitter sitter){

        StringRequest registrationRequest = new StringRequest(Request.Method.POST, Php.REGISTRAZIONE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("response");

                    if(result.equals("true")){
                        Toast.makeText(getApplicationContext(), R.string.registrationSuccess ,Toast.LENGTH_LONG).show();
                        Intent intent = new Intent( RegistrationActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // termina tutte le activity sopra quella chiamata
                        startActivity(intent);
                    } else if(result.equals("username")){
                        Toast.makeText(getApplicationContext(), R.string.usedUsername ,Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.registrationFail ,Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), R.string.registrationFail ,Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("type", String.valueOf(Constants.TYPE_SITTER));
                params.put("username", sitter.getUsername());
                params.put("password", sitter.getPassword());
                params.put("email", sitter.getEmail());
                params.put("nome", sitter.getNome());
                params.put("cognome", sitter.getCognome());
                params.put("telefono", sitter.getNumero());
                params.put("dataNascita", sitter.getDataNascita());
                params.put("genere", sitter.getGenere());
                params.put("nazione", sitter.getNazione());
                params.put("cap", sitter.getCap());
                params.put("auto", sitter.getAuto());
                return params;
            }
        };

        requestQueue.add(registrationRequest);
    }


}