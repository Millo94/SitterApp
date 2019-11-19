package it.uniba.di.sms.sitterapp.profilo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.oggetti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.recensioni.RecensioniPubblicoActivity;

/**
 * FRAGMENT PROFILO PUBBLICO FAMIGLIA
 */
public class PubblicoFamigliaFragment extends Fragment {

    View view;
    TextView usernamePuFam, descrPuFam, nomePuFam, cognomePuFam, emailPuFam, numeroPuFam, nazionePuFam, cittaPuFam, numFigliPuFam, animaliPuFam;
    //STRINGHE DA COLLEGARE AL DATABASE
    TextView nomePuFam2, cognomePuFam2, emailPuFam2, numeroPuFam2, nazionePuFam2, cittaPuFam2, numFigliPuFam2, animaliPuFam2;
    Button contattaFamiglia;
    Button feedbackFam;
    RatingBar ratingPuFam;

    RequestQueue requestQueue;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //numero di telefono
    String telefono;
    //EMAIL ANNUNCIO
    String email;

    private OnFragmentInteractionListener mListener;

    //PERMESSI
    private int CALL_PERMISSION = 1;
    private int SEND_SMS_PEMISSION = 2;

    public PubblicoFamigliaFragment() {
    }

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
        mostraProfilo(getActivity().getIntent().getStringExtra("username"));
       // showProfile(getActivity().getIntent().getStringExtra("username"));
        return view;
    }


    private void mostraProfilo(final String username){

        db.collection("utente")
                .document(username)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        usernamePuFam.setText(username);
                        emailPuFam2.setText((documentSnapshot.getString("famiglia.email")));
                        email = documentSnapshot.getString("famiglia.email");
                        nomePuFam2.setText(documentSnapshot.getString("famiglia.nome"));
                        cognomePuFam2.setText(documentSnapshot.getString("famiglia.cognome"));
                        numeroPuFam2.setText(documentSnapshot.getString("famiglia.numero"));
                        telefono = documentSnapshot.getString("famiglia.numero");
                        nazionePuFam2.setText(documentSnapshot.getString("famiglia.nazione"));
                        cittaPuFam2.setText(documentSnapshot.getString("famiglia.citta"));
                        //TODO rating bar
                        animaliPuFam2.setText(documentSnapshot.getBoolean("famiglia.animali").toString());
                        numFigliPuFam2.setText(documentSnapshot.getString("famiglia.numFigli"));
                        descrPuFam.setText(documentSnapshot.getString("famiglia.descrizione"));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), R.string.profileError, Toast.LENGTH_SHORT).show();
                    }
                });

    }


    //volley per la visualizzazione dei campi del profilo
    private void showProfile(final String username) {

        StringRequest request = new StringRequest(Request.Method.POST, Php.PROFILO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.optString("show");

                    if (result.equals("true")) {
                        usernamePuFam.setText(username);
                        emailPuFam2.setText(json.getString("email"));
                        email = json.getString("email");
                        nomePuFam2.setText(json.getString("nome"));
                        cognomePuFam2.setText(json.getString("cognome"));
                        numeroPuFam2.setText(json.getString("telefono"));
                        telefono = json.getString("telefono");
                        nazionePuFam2.setText(json.getString("nazione"));
                        cittaPuFam2.setText(json.getString("citta"));
                        // Rating bar
                        if (!json.getString("rating").equals("null")) {
                            ratingPuFam.setRating((float) json.getDouble("rating"));
                        }
                        // Setta animali
                        if (json.getString("animali").equals("0"))
                            animaliPuFam2.setText("Si");
                        else
                            animaliPuFam2.setText("No");
                        // Setta numero figli
                        if (!json.getString("numFigli").equals("null"))
                            numFigliPuFam2.setText(json.getString("numFigli"));
                        else
                            numFigliPuFam2.setText("0");
                        // Check descrizione
                        if (!json.getString("descrizione").equals("null"))
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

    //inizializzazione dei campi del profilo
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

        cittaPuFam = (TextView) view.findViewById(R.id.cittaPuFamily);
        cittaPuFam2 = (TextView) view.findViewById(R.id.cittaPuFamily2);


        numFigliPuFam = (TextView) view.findViewById(R.id.figliPuFamiglia);
        numFigliPuFam2 = (TextView) view.findViewById(R.id.figliPuFamiglia2);

        animaliPuFam = (TextView) view.findViewById(R.id.animaliPuFamiglia);
        animaliPuFam2 = (TextView) view.findViewById(R.id.animaliPuFamiglia2);

        //dialog per la scelta per contattare la famiglia
        contattaFamiglia = (Button) view.findViewById(R.id.contattaFamiglia);
        contattaFamiglia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence servizi[] = new CharSequence[]{getString(R.string.chiamata), getString(R.string.email), getString(R.string.sms)};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle(R.string.sceltaAzione);
                builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setItems(servizi, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {

                            //chiamata
                            case 0:
                                requestPhonePermission();
                                break;

                            //invio e-mail
                            case 1:
                                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                                emailIntent.setData(Uri.parse("mailto:"));
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_oggetto));
                                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_testo));
                                startActivity(emailIntent);
                                break;
                            //invio sms
                            case 2:
                                requestSMSPermission();
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
                showfeedIntent.putExtra("username", getActivity().getIntent().getStringExtra("username"));
                startActivity(showfeedIntent);
            }
        });

    }

    //richiesta del permesso per la chiamata
    public void requestPhonePermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent chiamaIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telefono));
            startActivity(chiamaIntent);

        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.permessoRichiesto)
                        .setMessage(R.string.stringPermissionRequest)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION);
                            }
                        })
                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION);
            }
        }
    }

    //richiesta del permesso per la chiamata
    public void requestSMSPermission() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setData(Uri.parse("smsto:" + telefono));
            intent.putExtra("sms_body", getString(R.string.sms_testo));
            startActivity(intent);

        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS)) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.permessoRichiesto)
                        .setMessage(R.string.stringPermissionRequest)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PEMISSION);
                            }
                        })
                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PEMISSION);
            }
        }
    }


    //gestisce il risultato dell'intent dei permessi
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestPhonePermission();
            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == SEND_SMS_PEMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(UtenteFamiglia family);

    }


}
