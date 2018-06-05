package it.uniba.di.sms.sitterapp.Appuntamenti;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
import it.uniba.di.sms.sitterapp.Principale.HomeActivity;
import it.uniba.di.sms.sitterapp.Principale.LoginActivity;
import it.uniba.di.sms.sitterapp.Profilo.ProfiloPubblicoActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Registrazione.RegistrationActivity;
import it.uniba.di.sms.sitterapp.SessionManager;


public class NoticeDetailActivity extends AppCompatActivity {

    TextView user,dataDet,start,end,desc;
    private static final String elimina = "ELIMINA";
    private String idAnnuncio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager sessionManager = new SessionManager(getApplicationContext());

        String famiglia = getIntent().getStringExtra("famiglia");
        String data = getIntent().getStringExtra("data");
        String oraInizio = getIntent().getStringExtra("oraInizio");
        String oraFine = getIntent().getStringExtra("oraFine");
        String descrizione = getIntent().getStringExtra("descrizione");
        idAnnuncio = getIntent().getStringExtra("idAnnuncio");

        if (sessionManager.getSessionType() == Constants.TYPE_SITTER){

            setContentView(R.layout.details_notice_sitter);

            user = (TextView) findViewById(R.id.usernameDettagliSit2);
            dataDet= (TextView) findViewById(R.id.dataDettagliSit2);
            start =(TextView)findViewById(R.id.oraInizioDettagliSit2);
            end = (TextView) findViewById(R.id.oraFineDettagliSit2);
            desc = (TextView) findViewById(R.id.descrizioneDettagliSit2);

            Button openProfile = (Button) findViewById(R.id.openFamilyProfile);
            openProfile.setOnClickListener(openProfileListener);
            Button candidate = (Button) findViewById(R.id.candidamiSit);
            candidate.setOnClickListener(candidateListener);

        } else if(sessionManager.getSessionType() == Constants.TYPE_FAMILY){

            setContentView(R.layout.details_notice_family);

            user = (TextView) findViewById(R.id.usernameDettagliFam2);
            dataDet= (TextView) findViewById(R.id.dataDettagliFamiglia2);
            start =(TextView)findViewById(R.id.oraInizioDettagliFamiglia2);
            end = (TextView) findViewById(R.id.oraFineDettagliFamiglia2);
            desc = (TextView) findViewById(R.id.descrizioneeDettagliFamiglia2);

            Button delete_notice = (Button) findViewById(R.id.eliminaAnnuncio);
            delete_notice.setOnClickListener(deleteNoticeListener);
            Button view_candidate = (Button) findViewById(R.id.visualCandidate);
            view_candidate.setOnClickListener(viewCandidateListener);
        }

        user.setText(famiglia);
        dataDet.setText(Constants.SQLtoDate(data));
        start.setText(oraInizio);
        end.setText(oraFine);
        desc.setText(descrizione);

    }

    View.OnClickListener openProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent detailIntent = new Intent(NoticeDetailActivity.this, ProfiloPubblicoActivity.class);
            detailIntent.putExtra(Constants.TYPE, Constants.TYPE_FAMILY);
            detailIntent.putExtra("username", user.getText().toString());
            startActivity(detailIntent);
        }
    };

    View.OnClickListener candidateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    View.OnClickListener deleteNoticeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            eliminaAnnuncio();
        }
    };

    View.OnClickListener viewCandidateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public void eliminaAnnuncio(){
        StringRequest deleteRequest = new StringRequest(Request.Method.POST, Php.INGAGGI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("response");

                    if (result.equals("true")) {
                        Toast.makeText(getApplicationContext(), R.string.deleteSuccess, Toast.LENGTH_LONG).show();
                        Intent intentback = new Intent(NoticeDetailActivity.this,IngaggiActivity.class);
                        startActivity(intentback);
                    } else if(result.equals("false")){
                        Toast.makeText(getApplicationContext(), R.string.deletefail ,Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.deletefail ,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), R.string.deletefail, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("richiesta", elimina);
                params.put("idAnnuncioElimina",idAnnuncio);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(deleteRequest);
    }

}
