package it.uniba.di.sms.sitterapp;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;


public class SitterRegistrationFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    ArrayList<EditText> listaET = new ArrayList<>();
    View view;
    EditText usernameET,passwordET, confermaPassowordET, nomeET, cognomeET, emailET, numeroET, dataNascitaET;
    RadioGroup genereRG;
    Switch autoSW;
    Button confRegistation;
    String genere = new String("");

    private OnFragmentInteractionListener mListener;

    public SitterRegistrationFragment() {}

    // Creazione della stringa dalla data scelta con il DatePicker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dataNascitaET.setText(
                new StringBuilder()
                        .append(dayOfMonth).append("-")
                        .append(month).append("-")
                        .append(year).append(" "));
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

        // Inizializzazione del layout
        initialization();

        // Creazione del Date Picker
        Calendar c = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        // Visualizzazione del date picker al click del campo
        dataNascitaET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        // Gestione della scelta del genere
        genereRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.maleSitterReg)
                    genere = "M";
                else if(checkedId == R.id.femaleSitterReg)
                    genere = "F";
            }
        });

        confRegistation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isEmpty()) {
                    Toast.makeText(getContext(),"Compilare i campi mancanti.", Toast.LENGTH_LONG).show();
                } else if (!confermaPassword(passwordET.getText().toString(),confermaPassowordET.getText().toString())){
                    Toast.makeText(getContext(),"Le password non corrispondono.", Toast.LENGTH_LONG).show();
                } else if (!checkEmail(emailET.getText().toString())){
                    Toast.makeText(getContext(),"Email non valida.", Toast.LENGTH_LONG).show();
                } else {
                    UtenteSitter sitter = new UtenteSitter(usernameET.getText().toString(),
                            passwordET.getText().toString(),
                            confermaPassowordET.getText().toString(),
                            nomeET.getText().toString(),
                            cognomeET.getText().toString(),
                            Date.valueOf(dataNascitaET.getText().toString()),
                            emailET.getText().toString(),
                            numeroET.getText().toString(),
                            genere,
                            autoSW.isChecked());
                    mListener.onFragmentInteraction(sitter);
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
        void onFragmentInteraction(UtenteSitter sitter);
    }

    public void initialization(){
        usernameET = (EditText) view.findViewById(R.id.usernameSitterReg);
        listaET.add(usernameET);
        passwordET = (EditText) view.findViewById(R.id.passwordSitterReg);
        listaET.add(passwordET);
        confermaPassowordET = (EditText) view.findViewById(R.id.confPasswordSitterReg);
        listaET.add(confermaPassowordET);
        nomeET = (EditText) view.findViewById(R.id.nomeSitterReg);
        listaET.add(nomeET);
        cognomeET = (EditText) view.findViewById(R.id.cognomeSitterReg);
        listaET.add(cognomeET);
        emailET= (EditText) view.findViewById(R.id.emailSitterReg);
        listaET.add(emailET);
        numeroET = (EditText) view.findViewById(R.id.phoneSitterReg);
        listaET.add(numeroET);
        dataNascitaET = (EditText) view.findViewById(R.id.nascitaSitterReg);
        listaET.add(dataNascitaET);
        genereRG = (RadioGroup) view.findViewById(R.id.groupGenderSitterReg);
        autoSW = (Switch) view.findViewById(R.id.switch1);
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

        if(genere.equals(""))
            empty = true;

        return empty;
    }


}
