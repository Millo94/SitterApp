package it.uniba.di.sms.sitterapp.Registrazione;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.regex.Pattern;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Utenti.UtenteFamiglia;


public class FamilyRegistrationFragment extends Fragment {


    ArrayList<EditText> listaET = new ArrayList<>();
    View view;
    Spinner nazioni;
    String arraypaesi[];
    EditText usernameET,passwordET, confermaPasswordET, nomeET, cognomeET, emailET, numeroET, provinciaET, cittaET, viaET, civicoET, numFigliET;
    TextView nazioneET;
    Switch animaliSW;
    Button confRegistation;


    private OnFragmentInteractionListener mListener;

    public FamilyRegistrationFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_family_registration, container, false);
        // Inizializzazione del layout
        initialization();

        // Generatore dello spinner di scelta della nazione
        nazioni = (Spinner) view.findViewById(R.id.spinnerNationFamiglia);
        arraypaesi = getResources().getStringArray(R.array.paesi);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.paesi, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nazioni.setAdapter(adapter);

        confRegistation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEmpty()) {
                    Toast.makeText(getContext(),"Compilare i campi mancanti.", Toast.LENGTH_LONG).show();
                } else if (!confermaPassword(passwordET.getText().toString(),confermaPasswordET.getText().toString())){
                    Toast.makeText(getContext(),"Le password non corrispondono.", Toast.LENGTH_LONG).show();
                } else if (!checkEmail(emailET.getText().toString())){
                    Toast.makeText(getContext(),"Email non valida.", Toast.LENGTH_LONG).show();
                } else {
                    UtenteFamiglia famiglia = new UtenteFamiglia(usernameET.getText().toString(),
                            passwordET.getText().toString(),
                            confermaPasswordET.getText().toString(),
                            nomeET.getText().toString(),
                            cognomeET.getText().toString(),
                            emailET.getText().toString(),
                            numeroET.getText().toString(),
                            nazioneET.getText().toString(),
                            provinciaET.getText().toString(),
                            cittaET.getText().toString(),
                            viaET.getText().toString(),
                            civicoET.getText().toString(),
                            Integer.parseInt(numFigliET.getText().toString()),
                            animaliSW.isChecked());
                    mListener.onFragmentInteraction(famiglia);
                }
            }
        });






        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try{
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + "must override method");
        }
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

    public void initialization(){

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
        emailET= (EditText) view.findViewById(R.id.emailFamiglia);
        listaET.add(emailET);
        numeroET = (EditText) view.findViewById(R.id.telefonoFamiglia);
        listaET.add(numeroET);
        provinciaET = (EditText) view.findViewById(R.id.provinciaFamiglia);
        listaET.add(provinciaET);
        cittaET = (EditText) view.findViewById(R.id.cittaFamiglia);
        listaET.add(cittaET);
        viaET = (EditText) view.findViewById(R.id.addressFamiglia);
        listaET.add(viaET);
        civicoET = (EditText) view.findViewById(R.id.civicoFamiglia);
        listaET.add(civicoET);
        numFigliET = (EditText) view.findViewById(R.id.numeroFigliFamiglia);
        listaET.add(numFigliET);
        nazioneET = (TextView) view.findViewById(R.id.nazioneFamiglia);
        animaliSW = (Switch) view.findViewById(R.id.petSwitchFamiglia);
        confRegistation = (Button) view.findViewById(R.id.buttonReg);
    }

    private boolean confermaPassword(String password, String conferma){
        return password.equals(conferma);
    }

    private boolean checkEmail(String email){
        Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        return emailPattern.matcher(email).matches();
    }

    private boolean isEmpty(){

        boolean empty = false;

        for(EditText element : listaET){
            if(element.getText().toString().equals(""))
                empty = true;
        }

        return empty;
    }
}
