package it.uniba.di.sms.sitterapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class SitterRegistrationFragment extends Fragment {

    EditText dataNascita;
    Date data_nascita;
    Calendar c;
    View view;



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




        dataNascita = (EditText) view.findViewById(R.id.nascitaSitterReg);
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


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }


}
