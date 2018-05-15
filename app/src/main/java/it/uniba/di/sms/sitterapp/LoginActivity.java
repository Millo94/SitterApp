package it.uniba.di.sms.sitterapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity{
    private EditText usernameEt,passwordEt;
    public static final int TYPE_FAMILY = 0;
    public static final int TYPE_SITTER = 1;
    public static final String TYPE = "type";
    private static final String login_url = "http://sitterapp.altervista.org/login.php";
    private StringRequest request;
    private RequestQueue RequestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEt = (EditText) findViewById(R.id.lblUsername);
        passwordEt = (EditText) findViewById(R.id.lblPassword);
        RequestQueue =  Volley.newRequestQueue(this);
    }

    public void onLogin(View view){
       request= new StringRequest(Request.Method.POST, login_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.names().get(0).equals("succes"))
                            {
                                Toast.makeText(getApplicationContext(),"SUCCES" + jsonObject.getString("succes"),Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
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
                Toast.makeText(LoginActivity.this,"Error", Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("user_name",usernameEt.getText().toString());
                params.put("password",passwordEt.getText().toString());
                return params;
            }
        };
        RequestQueue.add(request);
        /**
        String type = "login";
        Backgroundworker backgroundworker = new Backgroundworker(this);
        backgroundworker.execute(type,username,password);
        */
    }

    public void createAccountSitter(View view){
        Intent createAccountIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
        createAccountIntent.putExtra(TYPE, TYPE_SITTER);
        startActivity(createAccountIntent);
    }

    public void createAccountFamily(View view) {
        Intent createAccountIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
        createAccountIntent.putExtra(TYPE, TYPE_FAMILY);
        startActivity(createAccountIntent);
    }
}
