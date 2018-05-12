package it.uniba.di.sms.sitterapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements SitterRegistrationFragment.OnFragmentInteractionListener {


    public void onFragmentInteraction(Uri uri) {
        return;
    }

    SitterRegistrationFragment sitterFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new SitterRegistrationFragment()).commit();
        //sitterFrag = (SitterRegistrationFragment) getFragmentManager().findFragmentById(R.id.container);
        /*FragmentManager fragMenager = getFragmentManager();
        FragmentTransaction fragTransaction = fragMenager.beginTransaction();
        SitterRegistrationFragment fragSitter = (SitterRegistrationFragment) findViewById(R.id.);*/

    }
}
