package it.uniba.di.sms.sitterapp;

import android.content.Intent;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity{
    EditText usernameEt,passwordEt;
    public static final int TYPE_FAMILY = 0;
    public static final int TYPE_SITTER = 1;
    public static final String TYPE = "type";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEt = (EditText) findViewById(R.id.lblUsername);
        passwordEt = (EditText) findViewById(R.id.lblPassword);
    }

    public void onLogin(View view){
        String username = usernameEt.getText().toString();
        String password = passwordEt.getText().toString();
        String type = "login";
        Backgroundworker backgroundworker = new Backgroundworker(this);
        backgroundworker.execute(type,username,password);
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
