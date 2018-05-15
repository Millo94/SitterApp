package it.uniba.di.sms.sitterapp;

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

public class RegistrationActivity extends AppCompatActivity implements SitterRegistrationFragment.OnFragmentInteractionListener, FamilyRegistrationFragment.OnFragmentInteractionListener {

   private  String username,password,confPassword,name,surname,email,phone;
    private static final String register_url = "http://sitterapp.altervista.org/register.php";
    private int type;
    private StringRequest request;
    private com.android.volley.RequestQueue RequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        RequestQueue =  Volley.newRequestQueue(this);

        type = getIntent().getIntExtra(LoginActivity.TYPE, -1);

        if (type == LoginActivity.TYPE_SITTER){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SitterRegistrationFragment()).commit();
        } else if(type == LoginActivity.TYPE_FAMILY) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FamilyRegistrationFragment()).commit();
        }
    }

    @Override
    public void onFragmentInteraction(String usename, String password, String confPass, String name, String surname, String email, String phone) {

        if(type == LoginActivity.TYPE_SITTER) {
            this.username = usename;
            this.password = password;
            this.confPassword = confPass;
            this.name = name;
            this.surname = surname;
            this.email = email;
            this.phone = phone;
            onReg();
        } else {
            return;
        }
    }

    @Override
    public void onFragmentInteraction(String usename, String password, String confPass, String name, String surname, String email, String phone, String nazione) {

    }

    public void onReg(){

        request= new StringRequest(Request.Method.POST, register_url,
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
                params.put("user_name",username);
                params.put("password",password);
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
