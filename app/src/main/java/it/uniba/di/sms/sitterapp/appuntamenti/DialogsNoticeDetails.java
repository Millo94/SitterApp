package it.uniba.di.sms.sitterapp.appuntamenti;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import it.uniba.di.sms.sitterapp.oggetti.Notice;
import it.uniba.di.sms.sitterapp.principale.HomeActivity;
import it.uniba.di.sms.sitterapp.profilo.ProfiloPubblicoActivity;

/**
 * Dialogs per i dettagli di un annuncio
 */

public class DialogsNoticeDetails extends AppCompatDialogFragment {


    TextView familyName,dataDet,start,end,desc;
    String userID;
    int visibility = View.VISIBLE;
    SessionManager sessionManager;
    private static final String elimina = "delete";
    private String idAnnuncio;
    private String stato = new String();
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
                    .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            familyName = (TextView) view.findViewById(R.id.nomeCompletoSit2);
            dataDet= (TextView) view.findViewById(R.id.dataDettagliSit2);
            start =(TextView)view.findViewById(R.id.oraInizioDettagliSit2);
            end = (TextView) view.findViewById(R.id.oraFineDettagliSit2);
            desc = (TextView) view.findViewById(R.id.descrizioneDettagliSit2);
            //stato = (textView) view.findViewById(R.id.);

            Button openProfile = (Button) view.findViewById(R.id.openFamilyProfile);
            openProfile.setOnClickListener(openProfileListener);
            Button candidate = (Button) view.findViewById(R.id.candidamiSit);
            //viene visualizzato quando una babysitter si può candidare ad un annuncio
            candidate.setVisibility((getArguments().getBoolean("isCandidato") || sessionManager.getSessionType() == Constants.TYPE_FAMILY)?View.GONE:View.VISIBLE);
            candidate.setOnClickListener(candidateListener);

            Button confirm = (Button) view.findViewById(R.id.confirmSit);
            //viene visualizzato quando la babysitter, candidata, viene scelta dalla famiglia e deve confermare la scelta
            confirm.setVisibility(getArguments().getString("sitter").equals(sessionManager.getSessionUid()) && !getArguments().getBoolean("conferma")?View.VISIBLE:View.GONE);
            confirm.setOnClickListener(confirmEngageListener);

            Button deleteCandidatura = (Button) view.findViewById(R.id.deleteApplication);
            //viene visualizzato quando una babysitter, candidata, vpuò rimuovere la sua candidatura
            deleteCandidatura.setVisibility((getArguments().getBoolean("isCandidato") && !getArguments().getBoolean("conferma"))?View.VISIBLE:View.GONE);
            deleteCandidatura.setOnClickListener(deleteCandidaturaListener);

            userID = getArguments().getString("family");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("utente").document(getArguments().getString("family"))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            familyName.setText(documentSnapshot.getString("NomeCompleto"));
                        }
                    });

        } else if(sessionManager.getSessionType() == Constants.TYPE_FAMILY){            //dialog per la famiglia
            View view = inflater.inflate(R.layout.details_notice_family, null);

            builder.setView(view)
                    .setTitle(R.string.dettaglioAnnuncio)
                    .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
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


        String ds1 = getArguments().getString("data");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd-mm-yyyy");
        String ds2 = null;

        try {
            ds2 = sdf2.format(sdf1.parse(ds1));
        } catch (ParseException p) {
            p.printStackTrace();
        }

        dataDet.setText(ds2);
        start.setText(getArguments().getString("oraInizio"));
        end.setText(getArguments().getString("oraFine"));
        desc.setText(getArguments().getString("descrizione"));
        idAnnuncio = getArguments().getString("idAnnuncio");

        return builder.create();
    }

    //per recuperare le informazioni dalla pagina precedente
    public static DialogsNoticeDetails newInstance(Notice notice, String userID) {

        Bundle args = new Bundle();
        args.putString("family", notice.getFamily());
        args.putString("data", notice.getDate());
        args.putString("oraInizio", notice.getStart_time());
        args.putString("oraFine", notice.getEnd_time());
        args.putString("descrizione", notice.getDescription());
        args.putString("idAnnuncio", notice.getIdAnnuncio());
        args.putBoolean("isCandidato", notice.containsCandidatura(userID));
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
            detailIntent.putExtra("uid", userID);
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
                    .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
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
                    .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
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
                    .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
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


    public void hideButton(){
       visibility = View.GONE;
   }


    public void eliminaAnnuncio() {
        db.collection("annuncio").document(idAnnuncio)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.annuncioCancellato, Toast.LENGTH_SHORT).show();
                        Intent intentback = new Intent(getContext(), IngaggiActivity.class);
                        startActivity(intentback);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getActivity().getApplicationContext(), R.string.genericError, Toast.LENGTH_SHORT).show();
                    }
                });

    }


     //Metodo per far caricare la babysitter in candidatura

    public void candidaSitter(){

        String userID = sessionManager.getSessionUid();

        db.collection("annuncio")
                .document(idAnnuncio)
                .update("candidatura." + userID , userID)
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


        //elimina la sitter nel caso in cui fosse stata scelta dalla famiglia
        db.collection("annuncio").document(idAnnuncio)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                          @Override
                                          public void onSuccess(DocumentSnapshot documentSnapshot) {
                                              if(documentSnapshot.getString("sitter").equals(sessionManager.getSessionUid())){
                                                  db.collection("annuncio").document(idAnnuncio).update("sitter","");
                                              }
                                          }
                                      });

        //elimina la babysitter dalla lista delle candidate
        Map<String, Object> deleteSitter = new HashMap<>();
        deleteSitter.put("candidatura."+ sessionManager.getSessionUid(), FieldValue.delete());
        db.collection("annuncio")
                                .document(idAnnuncio)
                                .update(deleteSitter)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getActivity().getApplicationContext(), R.string.deleteSuccess, Toast.LENGTH_SHORT).show();
                                        Intent intentback = new Intent(getContext(), IngaggiActivity.class);
                                        startActivity(intentback);
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
                        Intent intentback = new Intent(getContext(),IngaggiActivity.class);
                        startActivity(intentback);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.genericError, Toast.LENGTH_SHORT).show();
                    }
                });
        db.collection("utente").document(sessionManager.getSessionUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final Long ingaggi = documentSnapshot.getLong("numLavori");
                        db.collection("utente")
                                .document(sessionManager.getSessionUid())
                                .update("numLavori",ingaggi+1);
                    }
                });
    }

}
