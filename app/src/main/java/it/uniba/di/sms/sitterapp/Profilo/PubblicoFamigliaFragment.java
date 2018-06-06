package it.uniba.di.sms.sitterapp.Profilo;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.content.Intent;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Oggetti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.Recensioni.RecensioniPubblicoActivity;

/**
 * FRAGMENT PROFILO PUBBLICO FAMIGLIA
 *
 */
public class PubblicoFamigliaFragment extends Fragment {

    View view;
    //STRINGHE STATICHE DA NON TOCCARE
    TextView usernamePuFam, descrPuFam, nomePuFam, cognomePuFam, emailPuFam, numeroPuFam, nazionePuFam, capPuFam, numFigliPuFam, animaliPuFam;
    //STRINGHE DA COLLEGARE AL DATABASE
    TextView nomePuFam2, cognomePuFam2, emailPuFam2, numeroPuFam2, nazionePuFam2, capPuFam2, numFigliPuFam2, animaliPuFam2;
    //DA COLLEGARE ALLA CHAT
    Button contattaFamiglia;
    Button feedbackFam;
    //DA CAPIRE
    RatingBar ratingPuFam;

    RequestQueue requestQueue;

    //numero di telefono di sanzone
    String telefono;
    //EMAIL ANNUNCIO
    String email;

    private OnFragmentInteractionListener mListener;

    public PubblicoFamigliaFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profilo_pubblico_family, container, false);

        requestQueue = Volley.newRequestQueue(getContext());
        inizializzazione();
        showProfile(getActivity().getIntent().getStringExtra("username"));
        return view;
    }

    private void showProfile(final String username){

        StringRequest request = new StringRequest(Request.Method.POST, Php.PROFILO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.optString("show");

                    if(result.equals("true")){
                        usernamePuFam.setText(username);
                        emailPuFam2.setText(json.getString("email"));
                        email = json.getString("email");
                        nomePuFam2.setText(json.getString("nome"));
                        cognomePuFam2.setText(json.getString("cognome"));
                        numeroPuFam2.setText(json.getString("telefono"));
                        telefono = json.getString("telefono");
                        nazionePuFam2.setText(json.getString("nazione"));
                        capPuFam2.setText(json.getString("cap"));
                        // Rating bar
                        if(!json.getString("rating").equals("null")) {
                            ratingPuFam.setRating((float) json.getDouble("rating"));
                        }
                        // Setta animali
                        if(json.getString("animali").equals("0"))
                            animaliPuFam2.setText("Si");
                        else
                            animaliPuFam2.setText("No");
                        // Setta numero figli
                        if(!json.getString("numFigli").equals("null"))
                            numFigliPuFam2.setText(json.getString("numFigli"));
                        else
                            numFigliPuFam2.setText("0");
                        // Check descrizione
                        if(!json.getString("descrizione").equals("null"))
                            descrPuFam.setText(json.getString("descrizione"));
                        else
                            descrPuFam.setText(R.string.descrizioneAssente);

                    } else if (result.equals("false")) {
                        Toast.makeText(getContext(), R.string.profileError, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), R.string.profileError, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("operation", "show");
                params.put("type", String.valueOf(Constants.TYPE_FAMILY));
                params.put("username", username);
                return params;
            }
        };

        requestQueue.add(request);
    }

    public void inizializzazione() {

        usernamePuFam = (TextView) view.findViewById(R.id.usernamePuFamiglia);

        descrPuFam = (TextView) view.findViewById(R.id.descrizionePuFamiglia);

        ratingPuFam = (RatingBar) view.findViewById(R.id.ratingPuFamiglia);
        ratingPuFam.setEnabled(false);

        nomePuFam = (TextView) view.findViewById(R.id.nomePuFamiglia);
        nomePuFam2 = (TextView) view.findViewById(R.id.nomePuFamiglia2);

        cognomePuFam = (TextView) view.findViewById(R.id.cognomePuFamiglia);
        cognomePuFam2 = (TextView) view.findViewById(R.id.cognomePuFamiglia2);

        emailPuFam = (TextView) view.findViewById(R.id.emailPuFamiglia);
        emailPuFam2 = (TextView) view.findViewById(R.id.emailPuFamiglia2);

        numeroPuFam = (TextView) view.findViewById(R.id.telefonoPuFamiglia);
        numeroPuFam2 = (TextView) view.findViewById(R.id.telefonoPuFamiglia2);

        nazionePuFam = (TextView) view.findViewById(R.id.nazionePuFamiglia);
        nazionePuFam2 = (TextView) view.findViewById(R.id.nazionePuFamiglia2);

        capPuFam = (TextView) view.findViewById(R.id.capPuFamily);
        capPuFam2 = (TextView) view.findViewById(R.id.capPuFamily2);


        numFigliPuFam = (TextView) view.findViewById(R.id.figliPuFamiglia);
        numFigliPuFam2 = (TextView) view.findViewById(R.id.figliPuFamiglia2);

        animaliPuFam = (TextView) view.findViewById(R.id.animaliPuFamiglia);
        animaliPuFam2 = (TextView) view.findViewById(R.id.animaliPuFamiglia2);

        contattaFamiglia = (Button) view.findViewById(R.id.contattaFamiglia);
        contattaFamiglia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence servizi[] = new CharSequence[] {getString(R.string.chiamata),getString(R.string.email),getString(R.string.sms)};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle(R.string.sceltaAzione);
                builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setItems(servizi, new DialogInterface.OnClickListener(){
                    /**
                     * Questo metodo serve per eseguire un azione tra chiamata sms e e-mail
                     * per contattare la famiglia dell'annuncio
                     * @param dialog
                     * @param which
                     */
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            switch (which){

                                //chiamata
                                case 0:
                                    Intent chiamaIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+telefono));
                                    startActivity(chiamaIntent);
                                    break;
                                //invio e-mail
                                case 1:
                                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                    emailIntent.putExtra(Intent.EXTRA_EMAIL,email );
                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Oggetto Email");
                                    emailIntent.putExtra(Intent.EXTRA_TEXT, "testo da mostrare");
                                    emailIntent.setType("text/plain");
                                    startActivity(emailIntent);
                                    break;
                                //invio sms
                                case 2:
                                    Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms",telefono,null));
                                    startActivity(smsIntent);
                                    break;
                                default:
                                    break;
                            }

                    }
                });
                builder.show();

            }
        });
        feedbackFam = (Button) view.findViewById(R.id.FeedbackFamiglia);
        feedbackFam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showfeedIntent = new Intent(getContext(), RecensioniPubblicoActivity.class);
                showfeedIntent.putExtra("username",getActivity().getIntent().getStringExtra("username"));
                startActivity(showfeedIntent);
            }
        });

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
        void onFragmentInteraction(UtenteFamiglia family);

    }


}
