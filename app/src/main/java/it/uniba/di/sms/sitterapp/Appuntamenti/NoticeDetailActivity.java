package it.uniba.di.sms.sitterapp.Appuntamenti;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import it.uniba.di.sms.sitterapp.R;


public class NoticeDetailActivity extends AppCompatActivity {

    TextView user,dataDet,start,end,desc;
    Button  contF,openP,candidate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_annuncio_sitter);


        String famiglia = getIntent().getStringExtra("famiglia");
        String data = getIntent().getStringExtra("data");
        String oraInizio = getIntent().getStringExtra("oraInizio");
        String oraFine = getIntent().getStringExtra("oraFine");
        String descrizione = getIntent().getStringExtra("descrizione");

        user = (TextView) findViewById(R.id.usernameDettagli2);
        user.setText(famiglia);
        dataDet= (TextView) findViewById(R.id.dataDettagli2);
        dataDet.setText(data);
        start =(TextView)findViewById(R.id.oraInizioDettagli2);
        start.setText(oraInizio);
        end = (TextView) findViewById(R.id.oraFineDettagli2);
        end.setText(oraFine);
        desc = (TextView) findViewById(R.id.descrizioneDettagli2);
        desc.setText(descrizione);
        contF = (Button) findViewById(R.id.contattaDettagli);

        openP = (Button) findViewById(R.id.visualProfiloDettagli);

        candidate = (Button) findViewById(R.id.candidami);


    }



}
