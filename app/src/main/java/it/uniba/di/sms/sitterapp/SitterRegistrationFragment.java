package it.uniba.di.sms.sitterapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class SitterRegistrationFragment extends Fragment {
    ArrayList<EditText> lista  = new ArrayList<>();
    EditText dataNascita;
    Date data_nascita;
    Calendar c;
    View view;
    EditText usernameET,passwordET,confPasswordET,nameET,surnameET,emailET,phoneET;
    RadioGroup radioBtnGender;
    RadioButton male,female;
    Switch carSwitch;
    Button confRegistation;

    private OnFragmentInteractionListener mListener;

    public SitterRegistrationFragment() {
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
        view = inflater.inflate(R.layout.fragment_sitter_registration, container, false);

        initialization();

        dataNascita.setVisibility(View.VISIBLE);
        dataNascita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog.OnDateSetListener mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yy");
                        data_nascita = new Date(i, i1, i2);
                        data_nascita = new Date(i - 1900, i1, i2);
                        dataNascita.setText(formatter.format(data_nascita));
                    }
                };
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), mOnDateSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH)
                        , c.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setTitle(R.string.datanascita);
                datePickerDialog.show();

            }
        });
        confRegistation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  EditText usernameET,passwordET,confPasswordET,nameET,surnameET,emailET,phoneET;
                String usertxt  = usernameET.getText().toString();
                String  passtxt = passwordET.getText().toString();
                String  conftxt = confPasswordET.getText().toString();
                String nametxt = nameET.getText().toString();
                String  surnametxt = surnameET.getText().toString();
                String emailTxt = emailET.getText().toString();
                String  phonetxt= phoneET.getText().toString();
                mListener.onFragmentInteraction(usertxt,passtxt,conftxt,nametxt,surnametxt,emailTxt,phonetxt);
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(String usename,String password , String confPass, String name,String surname , String email, String phone);
    }

    public void initialization(){
        usernameET = (EditText) view.findViewById(R.id.usernameSitterReg);
        lista.add(usernameET);
        passwordET = (EditText) view.findViewById(R.id.passwordSitterReg);
        lista.add(passwordET);
        confPasswordET= (EditText) view.findViewById(R.id.confPasswordSitterReg);
        lista.add(confPasswordET);
        nameET = (EditText) view.findViewById(R.id.nomeSitterReg);
        lista.add(nameET);
        surnameET= (EditText) view.findViewById(R.id.cognomeSitterReg);
        lista.add(surnameET);
        emailET= (EditText) view.findViewById(R.id.emailSitterReg);
        lista.add(emailET);
        phoneET = (EditText) view.findViewById(R.id.phoneSitterReg);
        lista.add(phoneET);
        radioBtnGender = (RadioGroup) view.findViewById(R.id.groupGenderSitterReg);
        male = (RadioButton) view.findViewById(R.id.maleSitterReg);
        female = (RadioButton) view.findViewById(R.id.femaleSitterReg);
        carSwitch = (Switch) view.findViewById(R.id.switch1);
        confRegistation = (Button) view.findViewById(R.id.buttonReg);
        dataNascita = (EditText) view.findViewById(R.id.nascitaSitterReg);
    }

    private boolean checkPass(String s1, String s2){
        return s1.equals(s2);
    }
    private boolean isEmpty(){
            boolean empty = true;
        for ( EditText e : lista) {
           if( e.getText().toString().equals("")|| e.getText() == null)
                empty = false;
        }
    return empty;
    }


}
