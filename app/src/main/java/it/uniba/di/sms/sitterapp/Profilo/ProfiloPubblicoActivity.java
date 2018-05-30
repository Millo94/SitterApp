package it.uniba.di.sms.sitterapp.Profilo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Oggetti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.Oggetti.UtenteSitter;
import it.uniba.di.sms.sitterapp.R;

public class ProfiloPubblicoActivity extends AppCompatActivity implements PubblicoFamigliaFragment.OnFragmentInteractionListener, PubblicoSitterFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);

        // Selezione del fragment in base al tipo dell'intent

        int type = getIntent().getIntExtra(Constants.TYPE, -1);
        if (type == Constants.TYPE_SITTER){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PubblicoSitterFragment()).commit();
        } else if(type == Constants.TYPE_FAMILY) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PubblicoFamigliaFragment()).commit();
        }
    }

    @Override
    public void onFragmentInteraction(UtenteFamiglia family) {

    }

    @Override
    public void onFragmentInteraction(UtenteSitter sitter) {

    }
}
