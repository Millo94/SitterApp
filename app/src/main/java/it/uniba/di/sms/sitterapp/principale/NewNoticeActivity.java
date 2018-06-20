package it.uniba.di.sms.sitterapp.principale;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;

/**
 * Activity per caricare un nuovo annuncio
 */
public class NewNoticeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText descrizione, data, oraInizio, oraFine;
    Button post;
    RequestQueue requestQueue;
    SessionManager sessionManager;
    private static final String posta = "POST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notice);

        // Inizializzo gli oggetti
        descrizione = (EditText) findViewById(R.id.descrizioneNewNotice);
        data = (EditText) findViewById(R.id.dataNewNotice);
        oraInizio = (EditText) findViewById(R.id.oraInizioNewNotice);
        oraFine = (EditText) findViewById(R.id.oraFineNewNotice);
        post = (Button) findViewById(R.id.confermaNewNotice);
        sessionManager = new SessionManager(getApplicationContext());
        requestQueue = Volley.newRequestQueue(NewNoticeActivity.this);

        // DATE PICKER
        // Creazione del Date Picker
        Calendar c = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(NewNoticeActivity.this,this, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        // Visualizzazione del date picker al click del campo
        data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        // TIME PICKER ora inizio
        oraInizio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog timePicker = new TimePickerDialog(NewNoticeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        oraInizio.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);
                timePicker.setTitle(R.string.selezionaOra);
                timePicker.show();
            }
        });

        // TIME PICKER ora fine
        oraFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog timePicker = new TimePickerDialog(NewNoticeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        oraFine.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    }
                }, hour, minute, true);
                timePicker.setTitle(R.string.selezionaOra);
                timePicker.show();
            }
        });

        //controllo se l'utente ha compilato tutti i campi
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isEmpty()) {
                    post();
                    finish();
                } else {
                    Toast.makeText(NewNoticeActivity.this, R.string.missingFields, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // DATE PICKER
    // Creazione della stringa dalla data scelta con il DatePicker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        data.setText(
                new StringBuilder()
                        .append(dayOfMonth).append("-")
                        .append(month + 1).append("-")
                        .append(year));

    }

    //volley per caricare un nuovo annuncio
    private void post(){

        StringRequest request = new StringRequest(Request.Method.POST, Php.ANNUNCI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("post");

                    if(result.equals("true")){
                        Toast.makeText(NewNoticeActivity.this, R.string.annuncioPubblicato, Toast.LENGTH_SHORT).show();
                    } else if(result.equals("false")){
                        Toast.makeText(NewNoticeActivity.this, "falsoooo", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NewNoticeActivity.this, R.string.genericError, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("richiesta",posta);
                params.put("username", sessionManager.getSessionUsername());
                params.put("descrizione", descrizione.getText().toString().trim());
                params.put("data", Constants.dateToSQL(data.getText().toString()));
                params.put("oraInizio", oraInizio.getText().toString().trim());
                params.put("oraFine", oraFine.getText().toString().trim());
                return params;
            }
        };

        requestQueue.add(request);
    }

    //Funzione di controllo sui campi vuoti
    private boolean isEmpty (){
        boolean result = false;
        ArrayList<EditText> campi = new ArrayList<>();
        campi.add(descrizione);
        campi.add(data);
        campi.add(oraInizio);
        campi.add(oraFine);

        for(EditText campo: campi){
            if(campo.getText().toString().equals(""))
                result = true;
        }
        return result;
    }
}
