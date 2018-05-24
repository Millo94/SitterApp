package it.uniba.di.sms.sitterapp.Profilo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Oggetti.UtenteSitter;

/**
 * FRAGMENT PROFILO PUBBLICO SITTER
 *
 * TODO -> COLLEGAMENTO AL DATABASE, DA CAPIRE LA FOTO BABYSITTER E RATING BAR, QUANDO FAREMO LA CHAT COLLEGARLA AL BOTTONE
 */
public class PubblicoSitterFragment extends Fragment {

    View view;
    RatingBar ratingPuSitter;
    //QUESTE STRINGHE SONO STATICHE
    TextView usernamePuSit, descrPuSit, nomePuSit, cognomePuSit, emailPuSit, numeroPuSit, carPuSit, sessoPuSit, dataPuSit, tariffaPuSit, ingaggiPuSit;
    //QUESTE STRINGHE SONO DA COLLEGARE AL DATABASE
    TextView nomePuSit2, cognomePuSit2, emailPuSit2, numeroPuSit2, carPuSit2, sessoPuSit2, dataPuSit2, tariffaPuSit2, ingaggiPuSit2;
    //DA COLLEGARE QUANDO AVREMO AL CHAT
    Button contattaFamiglia;

    private OnFragmentInteractionListener mListener;

    public PubblicoSitterFragment() {}



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profilo_pubblico_sitter, container, false);

        inizializzazione();

        //manca il collegamento al database

        return view;
    }


    public void inizializzazione(){

        usernamePuSit = (TextView) view.findViewById(R.id.usernamePuSitter);

        descrPuSit = (TextView) view.findViewById(R.id.descrizionePuSitter);

        ratingPuSitter = (RatingBar) view.findViewById(R.id.ratingPuSitter);

        nomePuSit = (TextView) view.findViewById(R.id.nomePuSitter);
        nomePuSit2 = (TextView) view.findViewById(R.id.nomePuSitter2);

        cognomePuSit = (TextView) view.findViewById(R.id.cognomePuSitter);
        cognomePuSit2 = (TextView) view.findViewById(R.id.cognomePuSitter2);

        emailPuSit = (TextView) view.findViewById(R.id.emailPuSitter);
        emailPuSit2 = (TextView) view.findViewById(R.id.emailPuSitter2);

        numeroPuSit = (TextView) view.findViewById(R.id.telefonoPuSitter);
        numeroPuSit2 = (TextView) view.findViewById(R.id.telefonoPuSitter2);

        cognomePuSit = (TextView) view.findViewById(R.id.cognomePuSitter);
        cognomePuSit2 = (TextView) view.findViewById(R.id.cognomePuSitter2);

        carPuSit = (TextView) view.findViewById(R.id.autoPuSitter);
        carPuSit2 = (TextView) view.findViewById(R.id.autoPuSitter2);

        sessoPuSit = (TextView) view.findViewById(R.id.sessoPuSitter);
        sessoPuSit2 = (TextView) view.findViewById(R.id.sessoPuSitter2);

        dataPuSit= (TextView) view.findViewById(R.id.nascitaPuSitter);
        dataPuSit2 = (TextView) view.findViewById(R.id.nascitaPuSitter2);

        tariffaPuSit = (TextView) view.findViewById(R.id.tariffaPuSitter);
        tariffaPuSit2 = (TextView) view.findViewById(R.id.tariffaPuSitter2);

        ingaggiPuSit = (TextView) view.findViewById(R.id.ingaggiPuSitter);
        ingaggiPuSit2 = (TextView) view.findViewById(R.id.ingaggiPuSitter2);

        contattaFamiglia = (Button) view.findViewById(R.id.contattaFamiglia);
    }


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
        void onFragmentInteraction(UtenteSitter sitter);
    }
}
