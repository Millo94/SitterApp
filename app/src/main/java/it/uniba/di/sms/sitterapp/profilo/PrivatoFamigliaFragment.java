package it.uniba.di.sms.sitterapp.profilo;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.oggetti.UtenteFamiglia;
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
    Button modificaProfilo,exit_button;
    boolean edit = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


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
        //session manager
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        //coda per la richiesta della volley
        requestQueue = Volley.newRequestQueue(getContext());
        inizializzazione();
        //openProfile();
        apriProfilo();
        exit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sessionManager.logout();

            }
        });
        return view;
    }

    private void getRatingFamily(final String username){


        db.collection("recensione");



    }

    private void apriProfilo(){

        db.collection("utente")
                .document(sessionManager.getSessionUsername())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        descrPrFam.setText(documentSnapshot.getString("famiglia.descrizione"));
                        nomePrFam2.setText(documentSnapshot.getString("famiglia.nome"));
                        cognomePrFam2.setText(documentSnapshot.getString("famiglia.cognome"));
                        emailPrFam2.setText(documentSnapshot.getString("famiglia.email"));
                        numeroPrFam2.setText(documentSnapshot.getString("famiglia.numero"));
                        animaliPrFam2.setChecked(documentSnapshot.getBoolean("famiglia.animali"));
                        numFigliPrFam2.setText(documentSnapshot.getString("famiglia.numFigli"));
                        nazionePrFam2.setText(documentSnapshot.getString("famiglia.nazione"));
                        cittaPrFam2.setText(documentSnapshot.getString("famiglia.citta"));
                        //TODO AGGIUNGI RATING
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), R.string.profileError ,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //volley per aprire il profilo privato
    /*private void openProfile(){

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
    }*/

    //per modificare i dati
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
                modifica();
                edit = false;
            }
        }
    };

    private void modifica(){

        DocumentReference docRef = db.collection("utente")
                .document(sessionManager.getSessionUsername());

        docRef.update("famiglia.numFigli", numeroPrFam2.getText().toString(),
                "famiglia.nazione", nazionePrFam2.getText().toString(),
                "famiglia.descrizione", descrPrFam.getText().toString(),
                "famiglia.citta", cittaPrFam2.getText().toString(),
                "famiglia.email", emailPrFam2.getText().toString(),
                "famiglia.nome", nomePrFam2.getText().toString(),
                "famiglia.cognome", cognomePrFam2.getText().toString(),
                "famiglia.numero", numeroPrFam2.getText().toString(),
                "famiglia.animali", animaliPrFam2.isChecked()
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.modifySuccess,Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.genericError,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /*private void modifyProfile(){

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
    }*/

    private void inizializzazione() {
        usernamePrFam = (TextView) view.findViewById(R.id.usernamePrFamiglia);
        usernamePrFam.setText(sessionManager.getSessionUsername());

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

        exit_button = view.findViewById(R.id.exit_button);

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
