package it.uniba.di.sms.sitterapp.Profilo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Utenti.UtenteSitter;

/**
 * FRAGMENT PROFILO PRIVATO SITTER
 */
public class PrivatoSitterFragment extends Fragment {


    /**
     * TODO -> FOTO BABYSITTER
     */
    View view;
    RatingBar ratingPrSitter;
    TextView usernamePrSit, descrPrSit, nomePrSit, cognomePrSit, emailPrSit, numeroPrSit, carPrSit, sessoPrSit, dataPrSit, tariffaPrSit, ingaggiPrSit;
    TextView nomePrSit2, cognomePrSit2, emailPrSit2, numeroPrSit2, carPrSit2, sessoPrSit2, dataPrSit2, tariffaPrSit2, ingaggiPrSit2;

    ToggleButton modificaProfilo;
    boolean edit = false;


    private OnFragmentInteractionListener mListener;

    public PrivatoSitterFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profilo_privato_sitter, container, false);

        inizializzazione();
        modificaProfilo.setOnClickListener(goEditable);

        return view;
    }


    public void inizializzazione() {
        usernamePrSit = (TextView) view.findViewById(R.id.usernamePrSitter);
        usernamePrSit.setEnabled(false);

        descrPrSit = (TextView) view.findViewById(R.id.descrizionePrSitter);
        descrPrSit.setEnabled(false);

        ratingPrSitter = (RatingBar) view.findViewById(R.id.ratingPrSitter);

        nomePrSit = (TextView) view.findViewById(R.id.nomePrSitter);
        nomePrSit2 = (TextView) view.findViewById(R.id.nomePrSitter2);
        nomePrSit2.setEnabled(false);

        cognomePrSit = (TextView) view.findViewById(R.id.cognomePrSitter);
        cognomePrSit2 = (TextView) view.findViewById(R.id.cognomePrSitter2);
        cognomePrSit2.setEnabled(false);

        emailPrSit = (TextView) view.findViewById(R.id.emailPrSitter);
        emailPrSit2 = (TextView) view.findViewById(R.id.emailPrSitter2);
        emailPrSit2.setEnabled(false);

        numeroPrSit = (TextView) view.findViewById(R.id.telefonoPrSitter);
        numeroPrSit2 = (TextView) view.findViewById(R.id.telefonoPrSitter2);
        numeroPrSit2.setEnabled(false);

        carPrSit = (TextView) view.findViewById(R.id.autoPrSitter);
        carPrSit2 = (TextView) view.findViewById(R.id.autoPrSitter2);
        carPrSit2.setEnabled(false);

        sessoPrSit = (TextView) view.findViewById(R.id.sessoPrSitter);
        sessoPrSit2 = (TextView) view.findViewById(R.id.sessoPrSitter2);
        sessoPrSit2.setEnabled(false);

        dataPrSit = (TextView) view.findViewById(R.id.nascitaPrSitter);
        dataPrSit2 = (TextView) view.findViewById(R.id.nascitaPrSitter2);
        dataPrSit2.setEnabled(false);

        tariffaPrSit = (TextView) view.findViewById(R.id.tariffaPrSitter);
        tariffaPrSit2 = (TextView) view.findViewById(R.id.tariffaPrSitter2);
        tariffaPrSit2.setEnabled(false);

        ingaggiPrSit = (TextView) view.findViewById(R.id.ingaggiPrSitter);
        ingaggiPrSit2 = (TextView) view.findViewById(R.id.ingaggiPrSitter2);

        modificaProfilo = (ToggleButton) view.findViewById(R.id.modificaProfilo);
    }

    public View.OnClickListener goEditable = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!edit){
                usernamePrSit.setEnabled(true);
                descrPrSit.setEnabled(true);
                nomePrSit2.setEnabled(true);
                cognomePrSit2.setEnabled(true);
                emailPrSit2.setEnabled(true);
                numeroPrSit2.setEnabled(true);
                carPrSit2.setEnabled(true);
                sessoPrSit2.setEnabled(true);
                dataPrSit2.setEnabled(true);
                tariffaPrSit2.setEnabled(true);

                edit = true;
            } else {
                usernamePrSit.setEnabled(false);
                descrPrSit.setEnabled(false);
                nomePrSit2.setEnabled(false);
                cognomePrSit2.setEnabled(false);
                emailPrSit2.setEnabled(false);
                numeroPrSit2.setEnabled(false);
                carPrSit2.setEnabled(false);
                sessoPrSit2.setEnabled(false);
                dataPrSit2.setEnabled(false);
                tariffaPrSit2.setEnabled(false);

                edit = false;
            }
        }
    };



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(UtenteSitter sitter);
    }
}
