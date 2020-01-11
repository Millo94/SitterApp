package it.uniba.di.sms.sitterapp.principale;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;

/**
 * Activity per caricare un nuovo annuncio
 */
public class NewNoticeActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    EditText descrizione, data, oraInizio, oraFine;
    Button post;
    SessionManager sessionManager;
    private static final String posta = "POST";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                    sendNotice();
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
        data.setText(String.format("%02d-%02d-%04d", dayOfMonth, month + 1, year));

    }


    /**
     * Metodo per l'inserimento di un ingaggio (Notice) da parte dell'utente Famiglia
     */
    private void sendNotice(){

        Map<String, Object> annuncio = new HashMap<>();
        annuncio.put("idAnnuncio", UUID.randomUUID().toString());
        annuncio.put("family", sessionManager.getSessionUsername());
        annuncio.put("date", data.getText().toString().trim());
        annuncio.put("start_time", oraInizio.getText().toString().trim());
        annuncio.put("end_time", oraFine.getText().toString().trim());
        annuncio.put("description", descrizione.getText().toString());
        annuncio.put("sitter", "");
        annuncio.put("candidatura", new HashMap<String,String>());
        annuncio.put("conferma", false);

        db.collection("annuncio")
                .document(annuncio.get("idAnnuncio").toString())
                .set(annuncio)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(NewNoticeActivity.this, R.string.annuncioPubblicato, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewNoticeActivity.this, R.string.genericError, Toast.LENGTH_SHORT).show();
                    }
                });


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
