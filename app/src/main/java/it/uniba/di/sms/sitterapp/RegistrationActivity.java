package it.uniba.di.sms.sitterapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class RegistrationActivity extends AppCompatActivity implements SitterRegistrationFragment.OnFragmentInteractionListener, FamilyRegistrationFragment.OnFragmentInteractionListener {

   private  String username,password,confPassword,name,surname,email,phone;
   private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        type = getIntent().getIntExtra(LoginActivity.TYPE, -1);

        if (type == LoginActivity.TYPE_SITTER){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SitterRegistrationFragment()).commit();
        } else if(type == LoginActivity.TYPE_FAMILY) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new FamilyRegistrationFragment()).commit();
        }
    }

    @Override
    public void onFragmentInteraction(String usename, String password, String confPass, String name, String surname, String email, String phone) {

        if(type == LoginActivity.TYPE_SITTER) {
            this.username = usename;
            this.password = password;
            this.confPassword = confPass;
            this.name = name;
            this.surname = surname;
            this.email = email;
            this.phone = phone;
            onReg();
        } else {
            return;
        }
    }

    @Override
    public void onFragmentInteraction(String usename, String password, String confPass, String name, String surname, String email, String phone, String nazione) {

    }

    public void onReg(){
        String type = "register";
        Backgroundworker backgroundworker = new Backgroundworker(this);
        backgroundworker.execute(type,username,password);
    }






}
