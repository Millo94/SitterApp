package it.uniba.di.sms.sitterapp.Appuntamenti;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;

/**
 * Created by Feder on 21/05/2018.
 */

public class AppuntamentiFamigliaActivity extends AppCompatActivity{

    //this is the JSON Data URL
    //make sure you are using the correct ip else it will not work
    private static final String URL_APPUNTAMENTI = "http://sitterapp.altervista.org/AppuntamentiFam.php";

    //a list to store all the products
    List<Appuntamento> appuntamentiList;

    //the recyclerview
    RecyclerView recyclerView;

    //session data
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appuntamento);

        //getting the recyclerview from xml
        recyclerView = (RecyclerView) findViewById(R.id.recylcerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //initializing session
        session = new SessionManager(getApplicationContext());
        //initializing the productlist
        appuntamentiList = new ArrayList<>();

        //this method will fetch and parse json
        //to display it in recyclerview
        loadProducts();
    }

    private void loadProducts() {

        /*
        * Creating a String Request
        * The request type is GET defined by first parameter
        * The URL is defined in the second parameter
        * Then we have a Response Listener and a Error Listener
        * In response listener we will get the JSON response as a String
        * */
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_APPUNTAMENTI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray app = new JSONArray(response);
                            for (int i = 0; i < app.length(); i++) {
                                JSONObject noticeObject = app.getJSONObject(i);
                                String sitter = noticeObject.getString("usernameBabysitter");
                                String data = noticeObject.getString("data");
                                String oraInizio = noticeObject.getString("oraInizio");
                                String oraFine = noticeObject.getString("oraFine");
                                Appuntamento a = new Appuntamento(sitter, data, oraInizio, oraFine);

                                appuntamentiList.add(a);
                            }
                            AppuntamentoFamigliaAdapter adapter = new AppuntamentoFamigliaAdapter(AppuntamentiFamigliaActivity.this, appuntamentiList);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", session.getSessionUsernam());
                return params;
            }
        };
        Volley.newRequestQueue(this).add(stringRequest);

    }

}
