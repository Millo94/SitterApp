package it.uniba.di.sms.sitterapp.Profilo;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Utenti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.Utenti.UtenteSitter;

public class ProfiloPubblicoActivity extends AppCompatActivity implements PubblicoFamigliaFragment.OnFragmentInteractionListener, PubblicoSitterFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);
    }

    @Override
    public void onFragmentInteraction(UtenteFamiglia family) {

    }

    @Override
    public void onFragmentInteraction(UtenteSitter sitter) {

    }
}
