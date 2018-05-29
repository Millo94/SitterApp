package it.uniba.di.sms.sitterapp.Appuntamenti;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import it.uniba.di.sms.sitterapp.R;


public class NoticeDetailActivity extends AppCompatActivity {

    TextView user,dataDet,start,end,desc;
    Button  openProfile,candidate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_notice_sitter);


        String famiglia = getIntent().getStringExtra("famiglia");
        String data = getIntent().getStringExtra("data");
        String oraInizio = getIntent().getStringExtra("oraInizio");
        String oraFine = getIntent().getStringExtra("oraFine");
        String descrizione = getIntent().getStringExtra("descrizione");

        user = (TextView) findViewById(R.id.usernameDettagliSit2);
        user.setText(famiglia);
        dataDet= (TextView) findViewById(R.id.dataDettagliSit2);
        dataDet.setText(data);
        start =(TextView)findViewById(R.id.oraInizioDettagliSit2);
        start.setText(oraInizio);
        end = (TextView) findViewById(R.id.oraFineDettagliSit2);
        end.setText(oraFine);
        desc = (TextView) findViewById(R.id.descrizioneDettagliSit2);
        desc.setText(descrizione);

        openProfile = (Button) findViewById(R.id.visualProfiloDettagliSit);

        candidate = (Button) findViewById(R.id.candidamiSit);


    }

}
