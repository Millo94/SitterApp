package it.uniba.di.sms.sitterapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
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

    //Date Picker variables
    EditText dataNascita;
    static final int DATE_DIALOG_ID = 0;
    private int mYear;
    private int mMonth;
    private int mDay;

    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                updateDisplay();
            }
        };

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
        // Inizializza i componenti della vista
        initialization();

        // On focus della data
        dataNascita.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                onCreateDialog(DATE_DIALOG_ID);
            }
        });

        //Codice professore
        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // display the current date (this method is below)
        updateDisplay();


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

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this.getContext(),
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }




    //Update la data
    private void updateDisplay() {
        dataNascita.setText(
                new StringBuilder()
                        .append(mDay).append("-")
                        .append(mMonth + 1).append("-")
                        .append(mYear).append(" "));
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
