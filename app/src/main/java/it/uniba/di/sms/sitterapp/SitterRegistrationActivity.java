package it.uniba.di.sms.sitterapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

public class SitterRegistrationActivity extends AppCompatActivity implements SitterRegistrationFragment.OnFragmentInteractionListener {

   private  String username,password,confPassword,name,surname,email,phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitter_registration);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new SitterRegistrationFragment()).commit();
    }

    @Override
    public void onFragmentInteraction(String usename, String password, String confPass, String name, String surname, String email, String phone) {
        this. username = usename;
        this.password = password;
        this.confPassword = confPass;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        onReg();
    }

    public void onReg(){
        String type = "register";
        Backgroundworker backgroundworker = new Backgroundworker(this);
        backgroundworker.execute(type,username,password);
    }


}
