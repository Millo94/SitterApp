package it.uniba.di.sms.sitterapp.appuntamenti;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
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
import it.uniba.di.sms.sitterapp.oggetti.Notice;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.principale.HomeActivity;
import it.uniba.di.sms.sitterapp.profilo.ProfiloPubblicoActivity;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import com.android.volley.RequestQueue;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Dialogs per i dettagli di un annuncio
 */

public class DialogsNoticeDetails extends AppCompatDialogFragment {


    TextView user,dataDet,start,end,desc;
    int visibility = View.VISIBLE;
    SessionManager sessionManager;
    private static final String elimina = "delete";
    private String idAnnuncio;
    RequestQueue requestQueue;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        sessionManager = new SessionManager(getActivity().getApplicationContext());
        requestQueue = Volley.newRequestQueue(getContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        requestQueue = Volley.newRequestQueue(getContext());

        //dialog per le baby sitter
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
            //viene visualizzato quando una babysitter si può candidare ad un annuncio
            candidate.setVisibility((getArguments().getBoolean("candidatura") || sessionManager.getSessionType() == Constants.TYPE_FAMILY)?View.GONE:View.VISIBLE);
            candidate.setOnClickListener(candidateListener);

            Button confirm = (Button) view.findViewById(R.id.confirmSit);
            //viene visualizzato quando la babysitter, candidata, viene scelta dalla famiglia e deve confermare la scelta
            confirm.setVisibility(getArguments().getString("sitter").equals(sessionManager.getSessionUsername()) && !getArguments().getBoolean("conferma")?View.VISIBLE:View.GONE);
            confirm.setOnClickListener(confirmEngageListener);

            Button deleteCandidatura = (Button) view.findViewById(R.id.deleteApplication);
            //viene visualizzato quando una babysitter, candidata, vpuò rimuovere la sua candidatura
            deleteCandidatura.setVisibility((getArguments().getBoolean("candidatura") && !getArguments().getBoolean("conferma"))?View.VISIBLE:View.GONE);
            deleteCandidatura.setOnClickListener(deleteCandidaturaListener);

            user.setText(getArguments().getString("username"));

        } else if(sessionManager.getSessionType() == Constants.TYPE_FAMILY){            //dialog per la famiglia
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
            Button candidate = (Button) view.findViewById(R.id.visualCandidate);
            candidate.setOnClickListener(viewCandidateListener);
        }

        dataDet.setText(getArguments().getString("data"));
        start.setText(getArguments().getString("oraInizio"));
        end.setText(getArguments().getString("oraFine"));
        desc.setText(getArguments().getString("descrizione"));
        idAnnuncio = getArguments().getString("idAnnuncio");

        return builder.create();
    }

    //per recuperare le informazioni dalla pagina precedente
    public static DialogsNoticeDetails newInstance(Notice notice, String username) {

        Bundle args = new Bundle();
        args.putString("username", notice.getFamily());
        args.putString("data", notice.getDate());
        args.putString("oraInizio", notice.getStart_time());
        args.putString("oraFine", notice.getEnd_time());
        args.putString("descrizione", notice.getDescription());
        args.putString("idAnnuncio", notice.getIdAnnuncio());
        args.putBoolean("candidatura", notice.containsCandidatura(username));
        args.putString("sitter", notice.getSitter());
        args.putBoolean("conferma", notice.getConferma());

        DialogsNoticeDetails fragment = new DialogsNoticeDetails();
        fragment.setArguments(args);


        return fragment;
    }

    //per aprire il profilo pubblico
    View.OnClickListener openProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent detailIntent = new Intent(getContext(), ProfiloPubblicoActivity.class);
            detailIntent.putExtra(Constants.TYPE, Constants.TYPE_FAMILY);
            detailIntent.putExtra("username", user.getText().toString());
            startActivity(detailIntent);
        }
    };

    //per la conferma della candidatura
    View.OnClickListener candidateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle(R.string.candidami)
                    .setMessage(R.string.confermaCandidatura)
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            candidaSitter();
                        }
                    })
                    .create()
                    .show();
        }
    };

    //per la conferma successiva alla scelta da parte della famiglia
    View.OnClickListener confirmEngageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle(R.string.confermaCandidaturaSit)
                    .setMessage(R.string.confermaConfermaCandidaturaSit)
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //va aggiunto TRUE al campo conferma nella collezione annuncio
                            confermaCandidatura();
                        }
                    })
                    .create()
                    .show();
        }
    };


    View.OnClickListener deleteCandidaturaListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle(R.string.eliminaCandidatura)
                    .setMessage(R.string.confermaEliminaCandidatura)
                    .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //va aggiunto TRUE al campo conferma nella collezione annuncio
                            eliminaCandidatura();
                        }
                    })
                    .create()
                    .show();
        }
    };

    //per eliminare un annuncio
    View.OnClickListener deleteNoticeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            eliminaAnnuncio();
            Intent goEngagments = new Intent(getContext(), IngaggiActivity.class);
            startActivity(goEngagments);
        }
    };

    //per visualizzare la lista delle candidate
    View.OnClickListener viewCandidateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent visualizzaIntent = new Intent(getContext(), SceltaSitter.class);
            visualizzaIntent.putExtra("idAnnuncio",idAnnuncio);
            startActivity(visualizzaIntent);
        }
    };

    //volley per eliminare un annuncio
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

    //Nascondere il pulsante "candidami" quando si apre il dialog da "i miei annunci"
    public void hideButton(){
        visibility = View.GONE;
    }


    /**
     * Metodo per far caricare la babysitter in candidatura
     *
     */
    public void candidaSitter(){


        String username = sessionManager.getSessionUsername();

        db.collection("annuncio")
                .document(idAnnuncio)
                .update("candidatura." + username , username)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.candidateSucces, Toast.LENGTH_SHORT).show();
                        Intent intentback = new Intent(getContext(),HomeActivity.class);
                        startActivity(intentback);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.candidatefail, Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void eliminaCandidatura(){
        Map<String, Object> deleteSitter = new HashMap<>();
        deleteSitter.put("candidatura."+sessionManager.getSessionUsername(), FieldValue.delete());

        db.collection("annuncio")
                .document(idAnnuncio)
                .update(deleteSitter)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.deleteSuccess, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.genericError, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void confermaCandidatura(){
        db.collection("annuncio")
                .document(idAnnuncio)
                .update("conferma",true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.candidateSucces, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.genericError, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
