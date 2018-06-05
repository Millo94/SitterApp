package it.uniba.di.sms.sitterapp.Feedback;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import it.uniba.di.sms.sitterapp.Principale.LoginActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Registrazione.RegistrationActivity;
import it.uniba.di.sms.sitterapp.SessionManager;

public class ReviewpageActivity extends AppCompatActivity {

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
        requestQueue = Volley.newRequestQueue(this);

        sessionManager = new SessionManager(getApplicationContext());
        famiglia = getIntent().getStringExtra("famiglia");
        id = getIntent().getStringExtra("idAnnuncio");
        sitter = getIntent().getStringExtra("sitter");
        setContentView(R.layout.scrivi_recensione);
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
            rate = (double) Float.valueOf(rating.getRating());

            if (rate == 0.0 || commento.isEmpty()) {
                Toast.makeText(it.uniba.di.sms.sitterapp.Feedback.ReviewpageActivity.this, "Riempire i campi", Toast.LENGTH_SHORT).show();
            } else
                inviadati(commento, rate, famiglia);
        }
    };

    public void inviadati(final String commento, final double rating, final String user) {

        StringRequest commentoRequest = new StringRequest(Request.Method.POST, Php.RECENSIONE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("response");

                    if (result.equals("true")) {
                        Toast.makeText(getApplicationContext(), "RECENSIONE EFFETTUATA CORRETTAMENTE", Toast.LENGTH_LONG).show();
                        Intent backIntent = new Intent(ReviewpageActivity.this, FeedbackActivity.class);
                        startActivity(backIntent);
                    } else {
                        Toast.makeText(getApplicationContext(), "ERRORE", Toast.LENGTH_SHORT).show();
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
                params.put("idAnnuncio", id);
                params.put("usernameFamiglia", user);
                params.put("usernameSitter", sessionManager.getSessionUsername());
                params.put("commento", commento);
                params.put("rating", Double.valueOf(rating).toString());
                params.put("type", String.valueOf(sessionManager.getSessionType()));
                return params;
            }
        };
        requestQueue.add(commentoRequest);

    }
}
