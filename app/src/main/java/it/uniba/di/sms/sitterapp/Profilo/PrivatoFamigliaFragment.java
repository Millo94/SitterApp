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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Oggetti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.SessionManager;

/**
 * PROFILO PRIVATO FAMIGLIA
 */
public class PrivatoFamigliaFragment extends Fragment {


    View view;
    TextView usernamePrFam, nomePrFam, cognomePrFam, emailPrFam, numeroPrFam, nazionePrFam, cittaPrFam, numFigliPrFam;
    EditText descrPrFam, nomePrFam2, cognomePrFam2, emailPrFam2, numeroPrFam2, nazionePrFam2, cittaPrFam2,  numFigliPrFam2;
    Switch animaliPrFam2;
    RatingBar ratingPrFam;
    Button modificaProfilo;
    boolean edit = false;

    RequestQueue requestQueue;
    SessionManager sessionManager;

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
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        requestQueue = Volley.newRequestQueue(getContext());
        inizializzazione();
        openProfile();
        return view;
    }

    private void openProfile(){

        StringRequest profileRequest = new StringRequest(Request.Method.POST, Php.PROFILO , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("open");

                    if (result.equals("true")){
                        usernamePrFam.setText(sessionManager.getSessionUsername());
                        if(!json.getString("rating").equals("null")) {
                            ratingPrFam.setRating((float) json.getDouble("rating"));
                        }
                        if(json.getString("descrizione").equals("null")){
                            descrPrFam.setHint(R.string.missingdescription);
                        } else {
                            descrPrFam.setText(json.getString("descrizione"));
                        }
                        nomePrFam2.setText(json.getString("nome"));
                        cognomePrFam2.setText(json.getString("cognome"));
                        emailPrFam2.setText(json.getString("email"));
                        numeroPrFam2.setText(json.getString("telefono"));
                        nazionePrFam2.setText(json.getString("nazione"));
                        cittaPrFam2.setText(json.getString("citta"));
                        // Conversione del flag animali
                        if(json.getString("animali").equals("0"))
                            animaliPrFam2.setChecked(true);
                        else
                            animaliPrFam2.setChecked(false);
                        // Setta numero figli
                        if(!json.getString("numFigli").equals("null"))
                            numFigliPrFam2.setText(json.getString("numFigli"));
                        else
                            numFigliPrFam2.setText("0");

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
                params.put("type", String.valueOf(Constants.TYPE_FAMILY));
                params.put("username", sessionManager.getSessionUsername());
                return params;
            }
        };

        requestQueue.add(profileRequest);
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
                cittaPrFam2.setEnabled(true);
                numFigliPrFam2.setEnabled(true);

                edit = true;

            } else {

                descrPrFam.setEnabled(false);
                nomePrFam2.setEnabled(false);
                cognomePrFam2.setEnabled(false);
                emailPrFam2.setEnabled(false);
                numeroPrFam2.setEnabled(false);
                animaliPrFam2.setEnabled(false);
                cittaPrFam2.setEnabled(false);
                numFigliPrFam2.setEnabled(false);
                modifyProfile();
                edit = false;
            }
        }
    };

    private void modifyProfile(){

        StringRequest modify = new StringRequest(Request.Method.POST, Php.PROFILO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.optString("modify");
                    if(json.getString("modify").equals("true")){
                        Toast.makeText(getActivity().getApplicationContext(), R.string.modifySuccess,Toast.LENGTH_SHORT).show();
                    } else if (result.equals("false")) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.genericError,Toast.LENGTH_SHORT).show();
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
                params.put("type", String.valueOf(Constants.TYPE_FAMILY));
                params.put("username", sessionManager.getSessionUsername());
                params.put("descrizione", descrPrFam.getText().toString());
                params.put("nome", nomePrFam2.getText().toString());
                params.put("cognome", cognomePrFam2.getText().toString());
                params.put("email", emailPrFam2.getText().toString());
                params.put("telefono", numeroPrFam2.getText().toString());
                params.put("nazione", nazionePrFam2.getText().toString());
                params.put("citta", cittaPrFam2.getText().toString());
                params.put("numFigli", numFigliPrFam2.getText().toString());
                // Conversione del flag animali
                if(animaliPrFam2.isChecked())
                    params.put("animali", "0");
                else
                    params.put("animali", "1");
                return params;
            }
        };

        requestQueue.add(modify);
    }

    private void inizializzazione() {
        usernamePrFam = (TextView) view.findViewById(R.id.usernamePrFamiglia);

        descrPrFam = (EditText) view.findViewById(R.id.descrizionePrFamiglia);
        descrPrFam.setEnabled(false);

        ratingPrFam = (RatingBar) view.findViewById(R.id.ratingPrFamiglia);
        ratingPrFam.setEnabled(false);

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

        cittaPrFam = (TextView) view.findViewById(R.id.cittaPrFamiglia);
        cittaPrFam2 = (EditText) view.findViewById(R.id.cittaPrFamiglia2);
        cittaPrFam2.setEnabled(false);

        numFigliPrFam = (TextView) view.findViewById(R.id.figliPrFamiglia);
        numFigliPrFam2 = (EditText) view.findViewById(R.id.figliPrFamiglia2);
        numFigliPrFam2.setEnabled(false);

        animaliPrFam2 = (Switch) view.findViewById(R.id.animaliPrFamiglia);
        animaliPrFam2.setEnabled(false);

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
