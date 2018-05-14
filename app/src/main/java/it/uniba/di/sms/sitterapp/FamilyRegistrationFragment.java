package it.uniba.di.sms.sitterapp;

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

import java.util.ArrayList;


public class FamilyRegistrationFragment extends Fragment {


    ArrayList<EditText> lista  = new ArrayList<>();
    View view;
    Spinner nazioni;
    String arraypaesi[];
    EditText usernameET,passwordET,confPasswordET,nameET,surnameET,emailET,phoneET, provinceET, cityET, streetET, civicoET, numChildET;
    TextView nationET;
    Switch petSwitch;
    Button confRegistation;


    private OnFragmentInteractionListener mListener;

    public FamilyRegistrationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_family_registration, container, false);

        initialization();

        nazioni = (Spinner) view.findViewById(R.id.spinnerNationFamiglia);
        arraypaesi = getResources().getStringArray(R.array.paesi);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.paesi, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nazioni.setAdapter(adapter);






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
        // TODO: Update argument type and name
        public void onFragmentInteraction(String usename,String password , String confPass, String name,String surname , String email, String phone, String nazione);
    }

    public void initialization(){

        usernameET = (EditText) view.findViewById(R.id.usernameFamiglia);
        lista.add(usernameET);
        passwordET = (EditText) view.findViewById(R.id.passwordFamiglia);
        lista.add(passwordET);
        confPasswordET= (EditText) view.findViewById(R.id.confPasswordFamiglia);
        lista.add(confPasswordET);
        nameET = (EditText) view.findViewById(R.id.nomeFamiglia);
        lista.add(nameET);
        surnameET= (EditText) view.findViewById(R.id.cognomeFamiglia);
        lista.add(surnameET);
        emailET= (EditText) view.findViewById(R.id.emailFamiglia);
        lista.add(emailET);
        phoneET = (EditText) view.findViewById(R.id.telefonoFamiglia);
        lista.add(phoneET);
        provinceET = (EditText) view.findViewById(R.id.provinciaFamiglia);
        lista.add(provinceET);
        cityET = (EditText) view.findViewById(R.id.cittaFamiglia);
        lista.add(cityET);
        streetET = (EditText) view.findViewById(R.id.addressFamiglia);
        lista.add(streetET);
        civicoET = (EditText) view.findViewById(R.id.civicoFamiglia);
        lista.add(civicoET);
        numChildET = (EditText) view.findViewById(R.id.numeroFigliFamiglia);
        lista.add(numChildET);
        nationET = (TextView) view.findViewById(R.id.nazioneFamiglia);
        petSwitch = (Switch) view.findViewById(R.id.petSwitchFamiglia);
        confRegistation = (Button) view.findViewById(R.id.buttonReg);
    }
}
