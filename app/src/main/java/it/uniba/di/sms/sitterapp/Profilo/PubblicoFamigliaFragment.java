package it.uniba.di.sms.sitterapp.Profilo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Utenti.UtenteFamiglia;

/**
 * FRAGMENT PROFILO PUBBLICO FAMIGLIA
 *
 * TODO: COLLEGAMENTO CON IL DATABASE, QUANDO FAREMO LA CHAT COLLEGARE IL BOTTONE PER CONTATTARE LA BABYSITTER E DA CAPIRE IL RATINGBAR
 *
 */
public class PubblicoFamigliaFragment extends Fragment {

    View view;
    //STRINGHE STATICHE DA NON TOCCARE
    TextView usernamePuFam, descrPuFam, nomePuFam, cognomePuFam, emailPuFam, numeroPuFam, nazionePuFam, provinciaPuFam, cittaPuFam, viaPuFam, civicoPuFam, numFigliPuFam, animaliPuFam;
    //STRINGHE DA COLLEGARE AL DATABASE
    TextView nomePuFam2, cognomePuFam2, emailPuFam2, numeroPuFam2, nazionePuFam2, provinciaPuFam2, cittaPuFam2, viaPuFam2, civicoPuFam2, numFigliPuFam2, animaliPuFam2;
    //DA COLLEGARE ALLA CHAT
    Button contattaSitter;
    //DA CAPIRE
    RatingBar ratingPuFam;


    private OnFragmentInteractionListener mListener;

    public PubblicoFamigliaFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profilo_pubblico_family, container, false);

        inizializzazione();

        //manca il collegamento al database

        return view;
    }

    public void inizializzazione() {

        usernamePuFam = (TextView) view.findViewById(R.id.usernamePuFamiglia);

        descrPuFam = (TextView) view.findViewById(R.id.descrizionePuFamiglia);

        ratingPuFam = (RatingBar) view.findViewById(R.id.ratingPuFamiglia);

        nomePuFam = (TextView) view.findViewById(R.id.nomePuFamiglia);
        nomePuFam2 = (TextView) view.findViewById(R.id.nomePuFamiglia2);

        cognomePuFam = (TextView) view.findViewById(R.id.cognomePuFamiglia);
        cognomePuFam2 = (TextView) view.findViewById(R.id.cognomePuFamiglia2);

        emailPuFam = (TextView) view.findViewById(R.id.emailPuFamiglia);
        emailPuFam2 = (TextView) view.findViewById(R.id.emailPuFamiglia2);

        numeroPuFam = (TextView) view.findViewById(R.id.telefonoPuFamiglia);
        numeroPuFam2 = (TextView) view.findViewById(R.id.telefonoPuFamiglia2);

        nazionePuFam = (TextView) view.findViewById(R.id.nazionePuFamiglia);
        nazionePuFam2 = (TextView) view.findViewById(R.id.nazionePuFamiglia2);

        provinciaPuFam = (TextView) view.findViewById(R.id.provinciaPuFamily);
        provinciaPuFam2 = (TextView) view.findViewById(R.id.provinciaPuFamily2);

        cittaPuFam = (TextView) view.findViewById(R.id.cittaPuFamily);
        cittaPuFam2 = (TextView) view.findViewById(R.id.cittaPuFamily2);

        viaPuFam = (TextView) view.findViewById(R.id.viaPuFamily);
        viaPuFam2 = (TextView) view.findViewById(R.id.viaPuFamily2);

        civicoPuFam = (TextView) view.findViewById(R.id.civicoPuFamily);
        civicoPuFam2 = (TextView) view.findViewById(R.id.civicoPuFamily2);

        numFigliPuFam = (TextView) view.findViewById(R.id.figliPuFamiglia);
        numFigliPuFam2 = (TextView) view.findViewById(R.id.figliPuFamiglia2);

        animaliPuFam = (TextView) view.findViewById(R.id.animaliPuFamiglia);
        animaliPuFam2 = (TextView) view.findViewById(R.id.animaliPuFamiglia2);

        contattaSitter = (Button) view.findViewById(R.id.contattaSitter);
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
        void onFragmentInteraction(UtenteFamiglia family);
    }
}
