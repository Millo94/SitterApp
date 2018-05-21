package it.uniba.di.sms.sitterapp.Profilo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Utenti.UtenteFamiglia;

/**
 * PROFILO PRIVATO FAMIGLIA
 */
public class PrivatoFamigliaFragment extends Fragment {


    View view;
    TextView usernamePrFam, nomePrFam, cognomePrFam, emailPrFam, numeroPrFam, nazionePrFam, provinciaPrFam, cittaPrFam, viaPrFam, civicoPrFam, numFigliPrFam, animaliPrFam;
    EditText descrPrFam, nomePrFam2, cognomePrFam2, emailPrFam2, numeroPrFam2, nazionePrFam2, provinciaPrFam2, cittaPrFam2, viaPrFam2, civicoPrFam2, numFigliPrFam2, animaliPrFam2;
    RatingBar ratingPrFam;

    Button modificaProfilo;
    boolean edit = false;

    private OnFragmentInteractionListener mListener;

    public PrivatoFamigliaFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profilo_privato_family, container, false);

        inizializzazione();

        return view;
    }

    public View.OnClickListener goEditable = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!edit) {

                descrPrFam.setEnabled(true);
                nomePrFam2.setEnabled(true);
                cognomePrFam2.setEnabled(true);
                emailPrFam2.setEnabled(true);
                numeroPrFam2.setEnabled(true);
                animaliPrFam2.setEnabled(true);
                nazionePrFam2.setEnabled(true);
                provinciaPrFam2.setEnabled(true);
                cittaPrFam2.setEnabled(true);
                viaPrFam2.setEnabled(true);
                civicoPrFam2.setEnabled(true);
                numFigliPrFam2.setEnabled(true);

                edit = true;

            } else {

                descrPrFam.setEnabled(false);
                nomePrFam2.setEnabled(false);
                cognomePrFam2.setEnabled(false);
                emailPrFam2.setEnabled(false);
                numeroPrFam2.setEnabled(false);
                animaliPrFam2.setEnabled(false);
                nazionePrFam2.setEnabled(false);
                provinciaPrFam2.setEnabled(false);
                cittaPrFam2.setEnabled(false);
                viaPrFam2.setEnabled(false);
                civicoPrFam2.setEnabled(false);
                numFigliPrFam2.setEnabled(false);

                edit = false;
            }
        }
    };

    private void inizializzazione() {
        usernamePrFam = (TextView) view.findViewById(R.id.usernamePrFamiglia);

        descrPrFam = (EditText) view.findViewById(R.id.descrizionePrFamiglia);
        descrPrFam.setEnabled(false);

        ratingPrFam = (RatingBar) view.findViewById(R.id.ratingPrFamiglia);

        nomePrFam = (TextView) view.findViewById(R.id.nomePrFamiglia);
        nomePrFam2 = (EditText) view.findViewById(R.id.nomePrFamiglia2);
        nomePrFam2.setEnabled(false);

        cognomePrFam = (TextView) view.findViewById(R.id.cognomePrFamiglia);
        cognomePrFam2 = (EditText) view.findViewById(R.id.cognomePrFamiglia2);
        cognomePrFam2.setEnabled(false);

        emailPrFam = (TextView) view.findViewById(R.id.emailPrFamiglia);
        emailPrFam2 = (EditText) view.findViewById(R.id.emailPrFamiglia2);
        emailPrFam2.setEnabled(false);

        numeroPrFam = (TextView) view.findViewById(R.id.telefonoPrFamiglia);
        numeroPrFam2 = (EditText) view.findViewById(R.id.telefonoPrFamiglia2);
        numeroPrFam2.setEnabled(false);

        nazionePrFam = (TextView) view.findViewById(R.id.nazionePrFamiglia);
        nazionePrFam2 = (EditText) view.findViewById(R.id.nazionePrFamiglia2);
        nazionePrFam2.setEnabled(false);

        provinciaPrFam = (TextView) view.findViewById(R.id.provinciaPrFamiglia);
        provinciaPrFam2 = (EditText) view.findViewById(R.id.provinciaPrFamiglia2);
        provinciaPrFam2.setEnabled(false);

        cittaPrFam = (TextView) view.findViewById(R.id.cittaPrFamiglia);
        cittaPrFam2 = (EditText) view.findViewById(R.id.cittaPrFamiglia2);
        cittaPrFam2.setEnabled(false);

        viaPrFam = (TextView) view.findViewById(R.id.viaPrFamiglia);
        viaPrFam2 = (EditText) view.findViewById(R.id.viaPrFamiglia2);
        viaPrFam2.setEnabled(false);

        civicoPrFam = (TextView) view.findViewById(R.id.civicoPrFamiglia);
        civicoPrFam2 = (EditText) view.findViewById(R.id.civicoPrFamiglia2);
        civicoPrFam2.setEnabled(false);

        numFigliPrFam = (TextView) view.findViewById(R.id.figliPrFamiglia);
        numFigliPrFam2 = (EditText) view.findViewById(R.id.figliPrFamiglia2);
        numFigliPrFam2.setEnabled(false);

        animaliPrFam = (TextView) view.findViewById(R.id.animaliPrFamiglia);
        animaliPrFam.setEnabled(false);

        modificaProfilo = (Button) view.findViewById(R.id.togglePrFamiglia);
        modificaProfilo.setOnClickListener(goEditable);
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
        void onFragmentInteraction(UtenteFamiglia famiglia);
    }
}
