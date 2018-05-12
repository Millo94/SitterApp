package it.uniba.di.sms.sitterapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    EditText usernameEt,passwordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEt = (EditText) findViewById(R.id.lblUsername);
        passwordEt = (EditText) findViewById(R.id.lblPassword);
    }

    public void onLoign(View view){
        String username = usernameEt.getText().toString();
        String password = passwordEt.getText().toString();
        String type = "login";
        Backgroundworker backgroundworker = new Backgroundworker(this);
        backgroundworker.execute(type,username,password);
    }

    public void createAccount(View view){

    }

}
