package it.uniba.di.sms.sitterapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import it.uniba.di.sms.sitterapp.Appuntamenti.AppuntamentiFamigliaActivity;

public class MainActivity extends AppCompatActivity{

    private SessionManager session;
    private TextView usernameET;
    private Button logoutbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(getApplicationContext());

        if(session.checkLogin()){
            finish();
        }

        usernameET = (TextView) findViewById(R.id.usernameSession);
        usernameET.setText(session.getSessionUsername());

        logoutbtn = (Button) findViewById(R.id.logoutbtn);
        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AppuntamentiFamigliaActivity.class);
                startActivity(intent);
                //session.logout();
            }
        });
    }
}
