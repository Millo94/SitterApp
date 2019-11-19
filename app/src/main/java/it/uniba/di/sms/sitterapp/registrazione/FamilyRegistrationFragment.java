package it.uniba.di.sms.sitterapp.registrazione;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.oggetti.UtenteFamiglia;


public class FamilyRegistrationFragment extends Fragment {


    ArrayList<EditText> listaET = new ArrayList<>();
    View view;
    Spinner nazioni;
    String arraypaesi[];
    EditText usernameET, passwordET, confermaPasswordET, nomeET, cognomeET, emailET, numeroET, cittaET, numFigliET;
    TextView nazioneET;
    Switch animaliSW;
    Boolean animali = true;
    Button confRegistration;


    private OnFragmentInteractionListener mListener;

    public FamilyRegistrationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_registration_family, container, false);
        // Inizializzazione del layout
        initialization();

        // Generatore dello spinner di scelta della nazione
        nazioni = (Spinner) view.findViewById(R.id.spinnerNationFamiglia);
        arraypaesi = getResources().getStringArray(R.array.paesi);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.paesi, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nazioni.setAdapter(adapter);

        // Gestione del flag animali

        animali = animaliSW.isChecked();

        //controllo dei campi inseriti
        confRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmpty()) {
                    Toast.makeText(getContext(), R.string.missingFields, Toast.LENGTH_LONG).show();
                } else if (!confermaPassword(passwordET.getText().toString(), confermaPasswordET.getText().toString())) {
                    Toast.makeText(getContext(), R.string.checkPassword, Toast.LENGTH_LONG).show();
                } else if (!checkEmail(emailET.getText().toString())) {
                    Toast.makeText(getContext(), R.string.invalidEmail, Toast.LENGTH_LONG).show();
                } else {
                    UtenteFamiglia famiglia = new UtenteFamiglia(usernameET.getText().toString(),
                            passwordET.getText().toString(),
                            nomeET.getText().toString(),
                            cognomeET.getText().toString(),
                            emailET.getText().toString(),
                            numeroET.getText().toString(),
                            nazioni.getSelectedItem().toString(),
                            cittaET.getText().toString(),
                            numFigliET.getText().toString(),
                            animali);
                    mListener.onFragmentInteraction(famiglia);
                }
            }
        });

        return view;
    }

    //controllo che l'activity che chiama il fragment implementi OnFragmentInteractionListener
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(UtenteFamiglia famiglia);
    }

    //inizializzazione dei campi
    public void initialization() {

        usernameET = (EditText) view.findViewById(R.id.usernameFamiglia);
        listaET.add(usernameET);
        passwordET = (EditText) view.findViewById(R.id.passwordFamiglia);
        listaET.add(passwordET);
        confermaPasswordET = (EditText) view.findViewById(R.id.confPasswordFamiglia);
        listaET.add(confermaPasswordET);
        nomeET = (EditText) view.findViewById(R.id.nomeFamiglia);
        listaET.add(nomeET);
        cognomeET = (EditText) view.findViewById(R.id.cognomeFamiglia);
        listaET.add(cognomeET);
        emailET = (EditText) view.findViewById(R.id.emailFamiglia);
        listaET.add(emailET);
        numeroET = (EditText) view.findViewById(R.id.telefonoFamiglia);
        listaET.add(numeroET);
        cittaET = (EditText) view.findViewById(R.id.CittaFamiglia);
        listaET.add(cittaET);
        numFigliET = (EditText) view.findViewById(R.id.numeroFigliFamiglia);
        listaET.add(numFigliET);
        nazioneET = (TextView) view.findViewById(R.id.nazioneFamiglia);
        animaliSW = (Switch) view.findViewById(R.id.petSwitchFamiglia);
        confRegistration = (Button) view.findViewById(R.id.confermaButton);
    }

    //controllo campi password e conferma password
    private boolean confermaPassword(String password, String conferma) {
        return password.equals(conferma);
    }

    //controllo la mail
    private boolean checkEmail(String email) {
        Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        return emailPattern.matcher(email).matches();
    }

    //controllo se i campi sono vuoti
    private boolean isEmpty() {

        boolean empty = false;

        for (EditText element : listaET) {
            if (element.getText().toString().equals(""))
                empty = true;
        }

        return empty;
    }
}
