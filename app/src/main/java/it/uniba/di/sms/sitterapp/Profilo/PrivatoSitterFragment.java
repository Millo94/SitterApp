package it.uniba.di.sms.sitterapp.Profilo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import it.uniba.di.sms.sitterapp.Oggetti.UtenteSitter;

/**
 * FRAGMENT PROFILO PRIVATO SITTER
 */
public class PrivatoSitterFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private RequestQueue requestQueue;
    private SessionManager sessionManager;

    View view;
    RatingBar ratingPrSitter;
    TextView usernamePrSit, nomePrSit, cognomePrSit, emailPrSit, numeroPrSit, sessoPrSit, dataPrSit, tariffaPrSit, ingaggiPrSit;
    EditText descrPrSit, nomePrSit2, cognomePrSit2, emailPrSit2, numeroPrSit2, sessoPrSit2, dataPrSit2, tariffaPrSit2, ingaggiPrSit2;
    Switch carPrSit2;
    ImageView profilePic;
    ToggleButton modificaProfilo;
    boolean edit = false;

    private OnFragmentInteractionListener mListener;

    public PrivatoSitterFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profilo_privato_sitter, container, false);

        requestQueue = Volley.newRequestQueue(getContext());

        // Valorizzo il session manager
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        //Creazione dell'istanza calendario per l'utilizzo del datePiker
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        inizializzazione(datePickerDialog);
        openProfile();

        // Modifica
        modificaProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goEditable();
            }
        });

        return view;
    }

    /**
     * Funzione che inizializza i dati del profilo all'apertura.
     */
    private void openProfile(){

        StringRequest profileRequest = new StringRequest(Request.Method.POST, Php.PROFILO , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("open");

                    if (result.equals("true")){
                        usernamePrSit.setText(json.getString("username"));
                        if(!json.getString("rating").equals("null")) {
                            ratingPrSitter.setRating((float) json.getDouble("rating"));
                        }
                        if(json.getString("descrizione").equals("null")){
                            descrPrSit.setHint(R.string.missingdescription);
                        } else {
                            descrPrSit.setText(json.getString("descrizione"));
                        }
                        nomePrSit2.setText(json.getString("nome"));
                        cognomePrSit2.setText(json.getString("cognome"));
                        emailPrSit2.setText(json.getString("email"));
                        numeroPrSit2.setText(json.getString("telefono"));

                        // Conversione del flag auto
                        if(json.getString("auto").equals("0"))
                            carPrSit2.setChecked(true);
                        else
                            carPrSit2.setChecked(false);

                        // Conversione del flag genere
                        if(json.getString("genere").equals(("M")))
                            sessoPrSit2.setText("Uomo");
                        else
                            sessoPrSit2.setText("Donna");

                        dataPrSit2.setText(Constants.SQLtoDate(json.getString("dataNascita")));
                        tariffaPrSit2.setText(json.getString("tariffaOraria"));
                        ingaggiPrSit2.setText(json.getString("numeroLavori"));

                        Glide.with(getContext()).load(json.getString("pathFoto")).into(profilePic);

                    } else if(result.equals("false")) {
                        Toast.makeText(getContext(), R.string.profileError ,Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), R.string.profileError ,Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("operation", "open");
                params.put("type", String.valueOf(Constants.TYPE_SITTER));
                params.put("username", sessionManager.getSessionUsername());
                return params;
            }
        };

        requestQueue.add(profileRequest);
    }

    private void modifyProfile(){

        StringRequest modify = new StringRequest(Request.Method.POST, Php.PROFILO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("modify");

                    if(json.getString("response").equals("error"))
                        Toast.makeText(getContext(), "Fatal Error", Toast.LENGTH_SHORT).show();

                    if(result.equals("true")){
                        Toast.makeText(getContext(), R.string.modifySuccess,Toast.LENGTH_SHORT).show();
                    } else if (result.equals("false")) {
                        Toast.makeText(getContext(), R.string.genericError,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), R.string.genericError,Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("operation", "modify");
                params.put("type", String.valueOf(Constants.TYPE_SITTER));
                params.put("username", sessionManager.getSessionUsername());
                params.put("descrizione", descrPrSit.getText().toString());
                params.put("nome", nomePrSit2.getText().toString());
                params.put("cognome", cognomePrSit2.getText().toString());
                params.put("email", emailPrSit2.getText().toString());
                params.put("telefono", numeroPrSit2.getText().toString());
                params.put("dataNascita", Constants.dateToSQL(dataPrSit2.getText().toString()));
                // Conversione del flag auto
                if(carPrSit2.isChecked())
                    params.put("auto", "0");
                else
                    params.put("auto", "1");
                params.put("tariffaOraria", tariffaPrSit2.getText().toString());
                return params;
            }
        };

        requestQueue.add(modify);
    }

    public void inizializzazione(final DatePickerDialog datePickerDialog) {

        usernamePrSit = (TextView) view.findViewById(R.id.usernamePrSitter);

        descrPrSit = (EditText) view.findViewById(R.id.descrizionePrSitter);
        descrPrSit.setEnabled(false);

        ratingPrSitter = (RatingBar) view.findViewById(R.id.ratingPrSitter);
        ratingPrSitter.setEnabled(false);

        nomePrSit = (TextView) view.findViewById(R.id.nomePrSitter);
        nomePrSit2 = (EditText) view.findViewById(R.id.nomePrSitter2);
        nomePrSit2.setEnabled(false);

        cognomePrSit = (TextView) view.findViewById(R.id.cognomePrSitter);
        cognomePrSit2 = (EditText) view.findViewById(R.id.cognomePrSitter2);
        cognomePrSit2.setEnabled(false);

        emailPrSit = (TextView) view.findViewById(R.id.emailPrSitter);
        emailPrSit2 = (EditText) view.findViewById(R.id.emailPrSitter2);
        emailPrSit2.setEnabled(false);

        numeroPrSit = (TextView) view.findViewById(R.id.telefonoPrSitter);
        numeroPrSit2 = (EditText) view.findViewById(R.id.telefonoPrSitter2);
        numeroPrSit2.setEnabled(false);

        carPrSit2 = (Switch) view.findViewById(R.id.carPrSit);
        carPrSit2.setEnabled(false);

        sessoPrSit = (TextView) view.findViewById(R.id.sessoPrSitter);
        sessoPrSit2 = (EditText) view.findViewById(R.id.sessoPrSitter2);
        sessoPrSit2.setEnabled(false);

        dataPrSit = (TextView) view.findViewById(R.id.nascitaPrSitter);
        dataPrSit2 = (EditText) view.findViewById(R.id.nascitaPrSitter2);
        dataPrSit2.setEnabled(false);
        dataPrSit2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                datePickerDialog.show();
            }
        });


        tariffaPrSit = (TextView) view.findViewById(R.id.tariffaPrSitter);
        tariffaPrSit2 = (EditText) view.findViewById(R.id.tariffaPrSitter2);
        tariffaPrSit2.setEnabled(false);

        ingaggiPrSit = (TextView) view.findViewById(R.id.ingaggiPrSitter);
        ingaggiPrSit2 = (EditText) view.findViewById(R.id.ingaggiPrSitter2);
        ingaggiPrSit2.setEnabled(false);

        profilePic = (ImageView) view.findViewById(R.id.imgPrSitter);

        modificaProfilo = (ToggleButton) view.findViewById(R.id.modificaProfilo);
    }

    private void goEditable() {
        if(!edit){
            descrPrSit.setEnabled(true);
            nomePrSit2.setEnabled(true);
            cognomePrSit2.setEnabled(true);
            emailPrSit2.setEnabled(true);
            numeroPrSit2.setEnabled(true);
            carPrSit2.setEnabled(true);
            tariffaPrSit2.setEnabled(true);
            dataPrSit2.setEnabled(true);
            edit = true;
        } else {
            descrPrSit.setEnabled(false);
            nomePrSit2.setEnabled(false);
            cognomePrSit2.setEnabled(false);
            emailPrSit2.setEnabled(false);
            numeroPrSit2.setEnabled(false);
            carPrSit2.setEnabled(false);
            dataPrSit2.setEnabled(false);
            tariffaPrSit2.setEnabled(false);
            edit = false;
            modifyProfile();
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
     * formattazione della stringa restituita dal date picker
     *
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dataPrSit2.setText(
                new StringBuilder()
                        .append(dayOfMonth).append("-")
                        .append(month).append("-")
                        .append(year));
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
