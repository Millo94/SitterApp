package it.uniba.di.sms.sitterapp.Appuntamenti;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Oggetti.Notice;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.Principale.HomeActivity;
import it.uniba.di.sms.sitterapp.Profilo.ProfiloPubblicoActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import com.android.volley.RequestQueue;

/**
 * Created by Francesca on 05/06/18.
 */

public class DialogsNoticeDetails extends AppCompatDialogFragment {

    TextView user,dataDet,start,end,desc;
    SessionManager sessionManager;
    private static final String elimina = "delete";
    private String idAnnuncio;
    RequestQueue requestQueue;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        sessionManager = new SessionManager(getActivity().getApplicationContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        requestQueue = Volley.newRequestQueue(getContext());


        if (sessionManager.getSessionType() == Constants.TYPE_SITTER){
            View view = inflater.inflate(R.layout.details_notice_sitter, null);

            builder.setView(view)
                    .setTitle(R.string.dettaglioAnnuncio)
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            user = (TextView) view.findViewById(R.id.usernameDettagliSit2);
            dataDet= (TextView) view.findViewById(R.id.dataDettagliSit2);
            start =(TextView)view.findViewById(R.id.oraInizioDettagliSit2);
            end = (TextView) view.findViewById(R.id.oraFineDettagliSit2);
            desc = (TextView) view.findViewById(R.id.descrizioneDettagliSit2);

            Button openProfile = (Button) view.findViewById(R.id.openFamilyProfile);
            openProfile.setOnClickListener(openProfileListener);
            Button candidate = (Button) view.findViewById(R.id.candidamiSit);
            candidate.setOnClickListener(candidateListener);

            user.setText(getArguments().getString("username"));

        } else if(sessionManager.getSessionType() == Constants.TYPE_FAMILY){
            View view = inflater.inflate(R.layout.details_notice_family, null);

            builder.setView(view)
                    .setTitle(R.string.dettaglioAnnuncio)
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });


            dataDet= (TextView) view.findViewById(R.id.dataDettagliFamiglia2);
            start =(TextView)view.findViewById(R.id.oraInizioDettagliFamiglia2);
            end = (TextView) view.findViewById(R.id.oraFineDettagliFamiglia2);
            desc = (TextView) view.findViewById(R.id.descrizioneDettagliFamiglia2);

            Button delete_notice = (Button) view.findViewById(R.id.eliminaAnnuncio);
            delete_notice.setOnClickListener(deleteNoticeListener);
            Button view_candidate = (Button) view.findViewById(R.id.visualCandidate);
            view_candidate.setOnClickListener(viewCandidateListener);
        }


        dataDet.setText(getArguments().getString("data"));
        start.setText(getArguments().getString("oraInizio"));
        end.setText(getArguments().getString("oraFine"));
        desc.setText(getArguments().getString("descrizione"));
        idAnnuncio = getArguments().getString("idAnnuncio");





        return builder.create();
    }


    public static DialogsNoticeDetails newInstance(Notice notice) {

        Bundle args = new Bundle();
        args.putString("username", notice.getFamily());
        args.putString("data", notice.getDate());
        args.putString("oraInizio", notice.getStart_time());
        args.putString("oraFine", notice.getEnd_time());
        args.putString("descrizione", notice.getDescription());
        args.putString("idAnnuncio", notice.getIdAnnuncio());

        DialogsNoticeDetails fragment = new DialogsNoticeDetails();
        fragment.setArguments(args);


        return fragment;
    }

    View.OnClickListener openProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent detailIntent = new Intent(getContext(), ProfiloPubblicoActivity.class);
            detailIntent.putExtra(Constants.TYPE, Constants.TYPE_FAMILY);
            detailIntent.putExtra("username", user.getText().toString());
            startActivity(detailIntent);
        }
    };

    View.OnClickListener candidateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            candidami();
        }
    };



    View.OnClickListener deleteNoticeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            eliminaAnnuncio();
        }
    };

    View.OnClickListener viewCandidateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public void eliminaAnnuncio(){
        StringRequest deleteRequest = new StringRequest(Request.Method.POST, Php.INGAGGI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString(elimina);

                    if (result.equals("true")) {
                        Toast.makeText(getContext(), R.string.deleteSuccess, Toast.LENGTH_LONG).show();
                        Intent intentback = new Intent(getContext(),IngaggiActivity.class);
                        startActivity(intentback);
                    } else if(result.equals("false")){
                        Toast.makeText(getContext(), R.string.deletefail ,Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), R.string.deletefail, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("operation", elimina);
                params.put("idAnnuncioElimina",idAnnuncio);
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(deleteRequest);
    }

    public void candidami(){
        StringRequest request = new StringRequest(Request.Method.POST, Php.CANDIDAMI, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.optString("response");

                    if (result.equals("true")) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.candidateSucces, Toast.LENGTH_SHORT).show();
                        Intent intentback = new Intent(getContext(),HomeActivity.class);
                        startActivity(intentback);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.candidatefail, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.genericError, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("richiesta","CANDIDAMI");
                params.put("username", sessionManager.getSessionUsername());
                params.put("idAnnuncio", idAnnuncio);
                return params;
            }
        };

        requestQueue.add(request);
    }

}
