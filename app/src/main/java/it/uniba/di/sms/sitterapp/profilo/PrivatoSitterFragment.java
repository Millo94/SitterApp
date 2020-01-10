package it.uniba.di.sms.sitterapp.profilo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import it.uniba.di.sms.sitterapp.oggetti.UtenteSitter;

import static android.app.Activity.RESULT_OK;

/**
 * FRAGMENT PROFILO PRIVATO SITTER
 */

public class PrivatoSitterFragment extends Fragment implements DatePickerDialog.OnDateSetListener {


    static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int GALLERY_REQUEST = 1;
    private RequestQueue requestQueue;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SessionManager sessionManager;

    View view;
    //NomeCompleto, E-mail, dataNascita, Avatar, Telefono, Rating, Retribuzione Oraria,
    //Auto,
    RatingBar ratingPrSitter;
    TextView emailPrSit, numeroPrSit, sessoPrSit, dataPrSit, tariffaPrSit, ingaggiPrSit;
    EditText nomeCompletoPrSit, descrPrSit, nomeCompletoPrSit2, cognomePrSit2, emailPrSit2, numeroPrSit2, sessoPrSit2, dataPrSit2, tariffaPrSit2, ingaggiPrSit2;
    Switch carPrSit2;
    ImageView profilePic;
    ToggleButton modificaProfilo;
    Button editDisp,exit_button;
    boolean edit = false;

    private OnFragmentInteractionListener mListener;
    private Bitmap bitmap;

    public PrivatoSitterFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profilo_privato_sitter, container, false);

        requestQueue = Volley.newRequestQueue(getActivity());

        // Valorizzo il session manager
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        //Creazione dell'istanza calendario per l'utilizzo del datePiker
        Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        inizializzazione(datePickerDialog);

        //apertura del profilo
        apriProfilo();

        // Modifica
        modificaProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goEditable();
            }
        });
        editDisp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisponibilitaDialog dialog = DisponibilitaDialog.newInstance(sessionManager.getSessionUsername());
                dialog.show(getChildFragmentManager(), "dialog");
            }
        });

        exit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sessionManager.logout();

            }
        });

        return view;
    }

    private void apriProfilo(){

        db.collection("utente")
                .document(sessionManager.getSessionUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        descrPrSit.setText(documentSnapshot.getString("Descrizione"));
                        nomeCompletoPrSit.setText(documentSnapshot.getString("NomeCompleto"));
                        emailPrSit2.setText(documentSnapshot.getString("Email"));
                        numeroPrSit2.setText(documentSnapshot.getString("Telefono"));
                        carPrSit2.setChecked(documentSnapshot.getBoolean("babysitter.Auto"));
                        sessoPrSit2.setText(documentSnapshot.getString("babysitter.Genere"));
                        dataPrSit2.setText(documentSnapshot.getString("babysitter.dataNascita"));
                        ingaggiPrSit2.setText(documentSnapshot.get("babysitter.numLavori").toString());
                        ratingPrSitter.setRating(Float.valueOf(documentSnapshot.get("babysitter.Rating").toString()));
                        tariffaPrSit2.setText(documentSnapshot.getString("babysitter.Retribuzione"));
                        //TODO AGGIUNGI FOTO
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), R.string.profileError ,Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void modifica(){

        DocumentReference docRef = db.collection("utente")
                .document(sessionManager.getSessionUid());

        docRef.update("Descrizione", descrPrSit.getText().toString(),
                "NomeCompleto", nomeCompletoPrSit.getText().toString(),
                "Email", emailPrSit2.getText().toString(),
                "Telefono", numeroPrSit2.getText().toString(),
                "babysitter.Genere", sessoPrSit2.getText().toString(),
                "babysitter.dataNascita", dataPrSit2.getText().toString(),
                "babysitter.Auto", carPrSit2.isChecked(),
                "babysitter.Retribuzione", tariffaPrSit2.getText().toString()
        )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.modifySuccess, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.genericError,Toast.LENGTH_SHORT).show();
                    }
                });
    }



    //inizializzazione dei campi
    public void inizializzazione(final DatePickerDialog datePickerDialog) {

        nomeCompletoPrSit = (EditText) view.findViewById(R.id.nomeCompletoPrSitter);
        nomeCompletoPrSit.setEnabled(false);

        descrPrSit = (EditText) view.findViewById(R.id.descrizionePrSitter);
        descrPrSit.setEnabled(false);

        ratingPrSitter = (RatingBar) view.findViewById(R.id.ratingPrSitter);
        ratingPrSitter.setEnabled(false);

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

        modificaProfilo = (ToggleButton) view.findViewById(R.id.modificaProfilo);
        editDisp = (Button) view.findViewById(R.id.editDisp);
        exit_button = view.findViewById(R.id.exit_button2);

        // SCELTA DELLA FOTO
        profilePic = (ImageView) view.findViewById(R.id.imgPrSitter);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence options[] = new CharSequence[]{getString(R.string.usaGalleria), getString(R.string.usaCamera)};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        switch (which) {

                            case 0:
                                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, GALLERY_REQUEST);
                                break;

                            case 1:
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (takePicture.resolveActivity(getContext().getPackageManager()) != null) {
                                    startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);
                                }
                                break;
                        }
                    }
                }).show();
            }
        });
    }

    //riceve e gestisce il risultato dell'intent per cambiare la foto
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri pickedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), pickedImage);
                new AsyncCaller().execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            bitmap = (Bitmap) data.getExtras().get("data");
            modificaFoto();
        }
    }

    //volley per la modifica della foto
    public void modificaFoto() {

        StringRequest request = new StringRequest(Request.Method.POST, Php.MODIFICA_FOTO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("response").equals("true")) {
                        sessionManager.setProfilePic(jsonObject.optString("nomeFile"));
                        Glide
                                .with(PrivatoSitterFragment.this.getContext())
                                .load(sessionManager.getProfilePic())
                                .into(profilePic);
                        Toast.makeText(getContext(), R.string.risutltatoCaricamento, Toast.LENGTH_SHORT).show();
                    } else if (jsonObject.getString("response").equals("false")) {
                        Toast.makeText(getContext(), R.string.genericError, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", sessionManager.getSessionUsername());
                params.put("nomeFile", sessionManager.getSessionUsername() + "Pic" + String.valueOf(new Random().nextInt(100)));
                params.put("immagine", imageToString(bitmap));
                return params;
            }
        };

        Volley.newRequestQueue(getActivity()).add(request);
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    //per modificare le info del profilo
    private void goEditable() {
        if (!edit) {
            descrPrSit.setEnabled(true);
            nomeCompletoPrSit.setEnabled(true);
            numeroPrSit2.setEnabled(true);
            carPrSit2.setEnabled(true);
            tariffaPrSit2.setEnabled(true);
            dataPrSit2.setEnabled(true);
            edit = true;
        } else {
            descrPrSit.setEnabled(false);
            nomeCompletoPrSit.setEnabled(false);
            numeroPrSit2.setEnabled(false);
            carPrSit2.setEnabled(false);
            dataPrSit2.setEnabled(false);
            tariffaPrSit2.setEnabled(false);
            edit = false;
            modifica();
        }
    }

    //per la comunicazione con il fragment
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

    //per disassociare fragment e activity
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //formattazione della stringa restituita dal date picker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dataPrSit2.setText(
                new StringBuilder()
                        .append(dayOfMonth).append("-")
                        .append(month).append("-")
                        .append(year));
    }

    //Interfaccia di comunicazione tra fragment e activity

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(UtenteSitter sitter);
    }


    //Task asincrono per il caricamento della foto
    private class AsyncCaller extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            pdLoading.setMessage(getResources().getString(R.string.caricamentoFoto));
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            modificaFoto();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            pdLoading.dismiss();
        }
    }
}
