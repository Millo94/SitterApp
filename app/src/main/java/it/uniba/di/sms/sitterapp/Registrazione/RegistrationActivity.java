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

import it.uniba.di.sms.sitterapp.LoginActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Utenti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.Utenti.UtenteSitter;

public class RegistrationActivity extends AppCompatActivity implements SitterRegistrationFragment.OnFragmentInteractionListener, FamilyRegistrationFragment.OnFragmentInteractionListener {

    /**
     * Due classi: una famiglia con gli attributi di famiglia, e una sitter.
     * Due funzioni on fragment interaction, che compilano le due classi
     * in onReg mettere parametro di tipo che indica quale tipo registrare, dividere le due strad e da un if
     * o uno switch
     */

    /**
     * Tipo di registrazione (0=family, 1=sitter)
     */
    private int type;

    /**
     * private  String username,password,confPassword,name,surname,email,phone;
     *
     * Questo elenco di parametri è sostituito da due oggetti di tipo famiglia e sitter,
     * che saranno riempiti da oggetti restituiti dalle registrazioni
     */
    private UtenteFamiglia famiglia;
    private UtenteSitter sitter;

    private static final String register_url = "http://sitterapp.altervista.org/register.php";
    private StringRequest request;
    private com.android.volley.RequestQueue RequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        RequestQueue =  Volley.newRequestQueue(this);

        /**
         * L'intent contiene un valore intero "type" che indica alla activity se il
         * fragment da visualizzare è quello della registrazione della famiglia, o quello della
         * registrazione della babysitter
         */
        type = getIntent().getIntExtra(LoginActivity.TYPE, -1);

        if (type == LoginActivity.TYPE_SITTER){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SitterRegistrationFragment()).commit();
        } else if(type == LoginActivity.TYPE_FAMILY) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FamilyRegistrationFragment()).commit();
        }
    }


    // Interaction con il fragment Sitter
    @Override
    public void onFragmentInteraction(UtenteFamiglia famiglia) {
        type = LoginActivity.TYPE_SITTER;
        this.famiglia = famiglia;
    }

    @Override
    public void onFragmentInteraction(UtenteSitter sitter) {
        type = LoginActivity.TYPE_FAMILY;
        this.sitter = sitter;
    }



    public void onReg(){

        request = new StringRequest(Request.Method.POST, register_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.names().get(0).equals("succes"))
                            {
                                Toast.makeText(getApplicationContext(),"SUCCES" + jsonObject.getString("succes"),Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),"ERROR" ,Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e ){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegistrationActivity.this,"Error", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                /*params.put("user_name",username);
                params.put("password",password);*/
                return params;
            }
        };

        RequestQueue.add(request);

        /**
         String type = "register";
         Backgroundworker backgroundworker = new Backgroundworker(this);
         backgroundworker.execute(type,username,password);
         */
    }



}