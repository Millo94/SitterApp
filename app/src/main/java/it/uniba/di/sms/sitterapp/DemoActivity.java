package it.uniba.di.sms.sitterapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import it.uniba.di.sms.sitterapp.principale.HomeActivity;

public class DemoActivity extends AppCompatActivity {

    SessionManager sessionManager;
    Button demoSitter;
    Button demoFamiglia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        sessionManager = new SessionManager(getApplicationContext());

        //intent alla demo se  un utente non è loggato
        if (sessionManager.checkLogin()) {
            Intent intent = new Intent(DemoActivity.this, HomeActivity.class);
            intent.putExtra(Constants.TYPE, sessionManager.getSessionType());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        //apretura demo sitter
        demoSitter = (Button) findViewById(R.id.demoBabysitter);
        demoSitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoActivity.this, HomeActivity.class);
                intent.putExtra(Constants.TYPE, Constants.TYPE_SITTER);
                startActivity(intent);
            }
        });

        //apertura demo famiglia
        demoFamiglia = (Button) findViewById(R.id.demoFamiglia);
        demoFamiglia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoActivity.this, HomeActivity.class);
                intent.putExtra(Constants.TYPE, Constants.TYPE_FAMILY);
                startActivity(intent);
            }
        });
    }
}
