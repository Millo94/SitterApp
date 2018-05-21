package it.uniba.di.sms.sitterapp.Profilo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Utenti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.Utenti.UtenteSitter;

public class ProfiloPrivatoActivity extends AppCompatActivity implements PrivatoFamigliaFragment.OnFragmentInteractionListener, PrivatoSitterFragment.OnFragmentInteractionListener{

    private UtenteFamiglia famiglia;
    private UtenteSitter sitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);

        // Selezione del fragment in base al tipo dell'intent

        int type = getIntent().getIntExtra(Constants.TYPE, -1);
        if (type == Constants.TYPE_SITTER){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PrivatoSitterFragment()).commit();
        } else if(type == Constants.TYPE_FAMILY) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PrivatoFamigliaFragment()).commit();
        }
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
