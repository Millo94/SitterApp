package it.uniba.di.sms.sitterapp.profilo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.oggetti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.oggetti.UtenteSitter;
import it.uniba.di.sms.sitterapp.R;

public class ProfiloPrivatoActivity extends AppCompatActivity implements PrivatoFamigliaFragment.OnFragmentInteractionListener, PrivatoSitterFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_layout);

        // Selezione del fragment in base al tipo dell'intent
        int type = getIntent().getIntExtra(Constants.TYPE, -1);
        if (type == Constants.TYPE_SITTER){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.viewPager, new PrivatoSitterFragment()).commit();
        } else if(type == Constants.TYPE_FAMILY) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.viewPager, new PrivatoFamigliaFragment()).commit();

        }
    }

    @Override
    public void onFragmentInteraction(UtenteFamiglia famiglia) {
    }

    @Override
    public void onFragmentInteraction(UtenteSitter sitter) {

    }

}
