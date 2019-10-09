package it.uniba.di.sms.sitterapp.scriviRecensione;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
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
import it.uniba.di.sms.sitterapp.SessionManager;
import tr.xip.errorview.ErrorView;

/**
 * Classe che gestisce la parte della scrittura delle recensioni
 */

public class ScriviRecensioneActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    TextView user;
    EditText desc;
    double rate = (double) 0.00;
    RatingBar rating;
    String commento = "null";
    Button scriviRec;
    SessionManager sessionManager;
    String famiglia;
    String id;
    String sitter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrivi_recensione);

        requestQueue = Volley.newRequestQueue(this);

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
            rate = (double) rating.getRating();

            if (rate == 0.0 || commento.isEmpty()) {
                Toast.makeText(ScriviRecensioneActivity.this, R.string.missingFields, Toast.LENGTH_SHORT).show();
            } else
                inviadati(commento, rate, famiglia);
        }
    };

    //volley per inviare i dati della recensione appena scritta
    public void inviadati(final String commento, final double rating, final String user) {

        StringRequest commentoRequest = new StringRequest(Request.Method.POST, Php.RECENSIONI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("write");

                    if (result.length() == 0) {
                        ErrorView errorView = (ErrorView) findViewById(R.id.errorView);
                        errorView.setSubtitle(R.string.niente_scrivi_recensioni);
                        errorView.setVisibility(View.VISIBLE);
                    } else {

                        if (result.equals("true")) {
                            Toast.makeText(getApplicationContext(), R.string.recensioneEffettuata, Toast.LENGTH_LONG).show();
                            Intent backIntent = new Intent(ScriviRecensioneActivity.this, ListaIngaggiSvoltiActivity.class);
                            startActivity(backIntent);
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.genericError, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), R.string.genericError, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("operation", "write");
                if (sessionManager.getSessionType() == Constants.TYPE_SITTER) {
                    params.put("idAnnuncio", id);
                    params.put("usernameFamiglia", user);
                    params.put("usernameSitter", sessionManager.getSessionUsername());
                    params.put("commento", commento);
                    params.put("rating", Double.valueOf(rating).toString());
                    params.put("tipoUtente", String.valueOf(sessionManager.getSessionType()));
                } else if (sessionManager.getSessionType() == Constants.TYPE_FAMILY) {
                    params.put("idAnnuncio", id);
                    params.put("usernameFamiglia", sessionManager.getSessionUsername());
                    params.put("usernameSitter", sitter);
                    params.put("commento", commento);
                    params.put("rating", Double.valueOf(rating).toString());
                    params.put("tipoUtente", String.valueOf(sessionManager.getSessionType()));
                }
                return params;
            }
        };

        requestQueue.add(commentoRequest);
    }
}
