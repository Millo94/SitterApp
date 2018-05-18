package it.uniba.di.sms.sitterapp.Profilo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Utenti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.Utenti.UtenteSitter;

public class ProfiloPubblicoActivity extends AppCompatActivity implements PrivatoFamigliaFragment.OnFragmentInteractionListener, PrivatoSitterFragment.OnFragmentInteractionListener{

    private UtenteFamiglia famiglia;
    private UtenteSitter sitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, new PrivatoSitterFragment()).commit();

    }


    @Override
    public void onFragmentInteraction(UtenteFamiglia famiglia) {
        this.famiglia = famiglia;
    }

    @Override
    public void onFragmentInteraction(UtenteSitter sitter) {
        this.sitter = sitter;
    }
}
