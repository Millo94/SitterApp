package it.uniba.di.sms.sitterapp.Profilo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Oggetti.UtenteSitter;
import it.uniba.di.sms.sitterapp.Recensioni.RecensioniPubblicoActivity;

/**
 * FRAGMENT PROFILO PUBBLICO SITTER
 *
 * TODO -> COLLEGARE I BOTTONI
 */
public class PubblicoSitterFragment extends Fragment {

    View view;
    RatingBar ratingPuSitter;
    ImageView profilePic;
    //QUESTE STRINGHE SONO STATICHE
    TextView usernamePuSit, descrPuSit, nomePuSit, cognomePuSit, emailPuSit, numeroPuSit, carPuSit, sessoPuSit, dataPuSit, tariffaPuSit, ingaggiPuSit, nazionePuSit, capPuSit;
    //QUESTE STRINGHE SONO DA COLLEGARE AL DATABASE
    TextView nomePuSit2, cognomePuSit2, emailPuSit2, numeroPuSit2, carPuSit2, sessoPuSit2, dataPuSit2, tariffaPuSit2, ingaggiPuSit2, nazionePuSit2, capPuSit2;
    //DA COLLEGARE QUANDO AVREMO AL CHAT
    Button contattaSitter, feedbackSit;

    //
    private String telefono;
    private String email;

    RequestQueue requestQueue;

    private OnFragmentInteractionListener mListener;

    public PubblicoSitterFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profilo_pubblico_sitter, container, false);
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
                        Glide.with(getContext()).load(json.getString("pathFoto")).into(profilePic);
                        usernamePuSit.setText(username);
                        emailPuSit2.setText(json.getString("email"));
                        email = json.getString("email");
                        nomePuSit2.setText(json.getString("nome"));
                        cognomePuSit2.setText(json.getString("cognome"));
                        numeroPuSit2.setText(json.getString("telefono"));
                        telefono = json.getString("telefono");
                        nazionePuSit2.setText(json.getString("nazione"));
                        capPuSit2.setText(json.getString("cap"));
                        // Rating bar
                        if(!json.getString("rating").equals("null")) {
                            ratingPuSitter.setRating((float) json.getDouble("rating"));
                        }
                        // Setta numero ingaggi
                        if(!json.getString("ingaggi").equals("null"))
                            ingaggiPuSit2.setText(json.getString("ingaggi"));
                        else
                            ingaggiPuSit2.setText("0");
                        // Setta il genere
                        if(json.getString("genere").equals(("M")))
                            sessoPuSit2.setText("Uomo");
                        else
                            sessoPuSit2.setText("Donna");
                        // Setta l'auto
                        if(json.getString("auto").equals("0"))
                            carPuSit2.setText("Si");
                        else
                            //carPuSit2.setChecked(false);
                            carPuSit2.setText("No");
                        dataPuSit2.setText(Constants.SQLtoDate(json.getString("dataNascita")));
                        // Check tariffa
                        if(!json.getString("tariffaOraria").equals("null"))
                            tariffaPuSit2.setText(json.getString("tariffaOraria"));
                        else
                            tariffaPuSit2.setText(R.string.tariffaAssente);
                        // Check descrizione
                        if(!json.getString("descrizione").equals("null"))
                            descrPuSit.setText(json.getString("descrizione"));
                        else
                            descrPuSit.setText(R.string.descrizioneAssente);
                    } else if (result.equals("false")){
                        Toast.makeText(getActivity().getApplicationContext(), R.string.profileError, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.profileError, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("operation", "show");
                params.put("type", String.valueOf(Constants.TYPE_SITTER));
                params.put("username", username);
                return params;
            }
        };

        requestQueue.add(request);
    }

    public void inizializzazione(){

        usernamePuSit = (TextView) view.findViewById(R.id.usernamePuSitter);

        profilePic = (ImageView) view.findViewById(R.id.imagePuSitter);

        descrPuSit = (TextView) view.findViewById(R.id.descrizionePuSitter);

        ratingPuSitter = (RatingBar) view.findViewById(R.id.ratingPuSitter);
        ratingPuSitter.setEnabled(false);

        nomePuSit = (TextView) view.findViewById(R.id.nomePuSitter);
        nomePuSit2 = (TextView) view.findViewById(R.id.nomePuSitter2);

        cognomePuSit = (TextView) view.findViewById(R.id.cognomePuSitter);
        cognomePuSit2 = (TextView) view.findViewById(R.id.cognomePuSitter2);

        emailPuSit = (TextView) view.findViewById(R.id.emailPuSitter);
        emailPuSit2 = (TextView) view.findViewById(R.id.emailPuSitter2);

        numeroPuSit = (TextView) view.findViewById(R.id.telefonoPuSitter);
        numeroPuSit2 = (TextView) view.findViewById(R.id.telefonoPuSitter2);

        cognomePuSit = (TextView) view.findViewById(R.id.cognomePuSitter);
        cognomePuSit2 = (TextView) view.findViewById(R.id.cognomePuSitter2);

        carPuSit = (TextView) view.findViewById(R.id.autoPuSitter);
        carPuSit2 = (TextView) view.findViewById(R.id.autoPuSitter2);

        sessoPuSit = (TextView) view.findViewById(R.id.sessoPuSitter);
        sessoPuSit2 = (TextView) view.findViewById(R.id.sessoPuSitter2);

        dataPuSit= (TextView) view.findViewById(R.id.nascitaPuSitter);
        dataPuSit2 = (TextView) view.findViewById(R.id.nascitaPuSitter2);

        tariffaPuSit = (TextView) view.findViewById(R.id.tariffaPuSitter);
        tariffaPuSit2 = (TextView) view.findViewById(R.id.tariffaPuSitter2);

        ingaggiPuSit = (TextView) view.findViewById(R.id.ingaggiPuSitter);
        ingaggiPuSit2 = (TextView) view.findViewById(R.id.ingaggiPuSitter2);

        nazionePuSit = (TextView) view.findViewById(R.id.nazionePuSitter);
        nazionePuSit2 = (TextView) view.findViewById(R.id.nazionePuSitter2);

        capPuSit = (TextView) view.findViewById(R.id.capPuSitter);
        capPuSit2 = (TextView) view.findViewById(R.id.capPuSitter2);

        contattaSitter = (Button) view.findViewById(R.id.contattaSitter);
        contattaSitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence servizi[] = new CharSequence[] {getString(R.string.chiamata),getString(R.string.email),getString(R.string.sms)};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle(R.string.sceltaAzione);
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
        feedbackSit = (Button) view.findViewById(R.id.feedbackSitter);
        feedbackSit.setOnClickListener(new View.OnClickListener() {
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
        void onFragmentInteraction(UtenteSitter sitter);
    }
}
