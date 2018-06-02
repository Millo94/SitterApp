package it.uniba.di.sms.sitterapp.Recensioni;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import it.uniba.di.sms.sitterapp.R;


public class RecensioniRicevuteFragment extends Fragment {


    public RecensioniRicevuteFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recensioni_ricevute, container, false);


        return view;
    }


}



