package it.uniba.di.sms.sitterapp.Profilo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RemoteViews;
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
import it.uniba.di.sms.sitterapp.Oggetti.UtenteSitter;

import static android.app.Activity.RESULT_OK;

/**
 * FRAGMENT PROFILO PRIVATO SITTER
 */

public class PrivatoSitterFragment extends Fragment implements DatePickerDialog.OnDateSetListener {


    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GALLERY_REQUEST = 1;
    private RequestQueue requestQueue;
    private SessionManager sessionManager;

    View view;
    RatingBar ratingPrSitter;
    TextView usernamePrSit, nomePrSit, cognomePrSit, emailPrSit, numeroPrSit, sessoPrSit, dataPrSit, tariffaPrSit, ingaggiPrSit;
    EditText descrPrSit, nomePrSit2, cognomePrSit2, emailPrSit2, numeroPrSit2, sessoPrSit2, dataPrSit2, tariffaPrSit2, ingaggiPrSit2;
    Switch carPrSit2;
    ImageView profilePic;
    ToggleButton modificaProfilo;
    Button editDisp;
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
        openProfile();

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

        return view;
    }

    /**
     * Funzione che inizializza i dati del profilo all'apertura.
     */
    private void openProfile() {

        StringRequest profileRequest = new StringRequest(Request.Method.POST, Php.PROFILO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.getString("open");

                    if (result.equals("true")) {
                        usernamePrSit.setText(sessionManager.getSessionUsername());
                        if (!json.getString("rating").equals("null")) {
                            ratingPrSitter.setRating((float) json.getDouble("rating"));
                        }
                        if (json.getString("descrizione").equals("null")) {
                            descrPrSit.setHint(R.string.missingdescription);
                        } else {
                            descrPrSit.setText(json.getString("descrizione"));
                        }
                        nomePrSit2.setText(json.getString("nome"));
                        cognomePrSit2.setText(json.getString("cognome"));
                        emailPrSit2.setText(json.getString("email"));
                        numeroPrSit2.setText(json.getString("telefono"));

                        // Conversione del flag auto
                        if (json.getString("auto").equals("0"))
                            carPrSit2.setChecked(true);
                        else
                            carPrSit2.setChecked(false);

                        // Conversione del flag genere
                        if (json.getString("genere").equals(("M")))
                            sessoPrSit2.setText("Uomo");
                        else
                            sessoPrSit2.setText("Donna");

                        dataPrSit2.setText(Constants.SQLtoDate(json.getString("dataNascita")));
                        tariffaPrSit2.setText(json.getString("tariffaOraria"));

                        Glide.with(PrivatoSitterFragment.this).load(sessionManager.getProfilePic()).into(profilePic);

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
                params.put("operation", "open");
                params.put("type", String.valueOf(Constants.TYPE_SITTER));
                params.put("username", sessionManager.getSessionUsername());
                return params;
            }
        };

        requestQueue.add(profileRequest);
    }

    private void modifyProfile() {

        StringRequest modify = new StringRequest(Request.Method.POST, Php.PROFILO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.optString("modify");
                    if (json.getString("modify").equals("true")) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.modifySuccess, Toast.LENGTH_SHORT).show();
                    } else if (result.equals("false")) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.genericError, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), R.string.genericError, Toast.LENGTH_SHORT).show();
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
                if (carPrSit2.isChecked())
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

        modificaProfilo = (ToggleButton) view.findViewById(R.id.modificaProfilo);
        editDisp = (Button) view.findViewById(R.id.editDisp);

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

                                //QUESTO è QUELLO DELLO SCATTO DELLA FOTO, VEDI COME RISOLEVERE LA QUESTIONE DELL'if. CIA    
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            //    if (takePicture.resolveActivity(getPackageManager()) != null) {
                                    startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);

                            //    }

                                break;
                        }
                    }
                }).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri pickedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), pickedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            new AsyncCaller().execute();

        }
    }

    public void modificaFoto() {

        StringRequest request = new StringRequest(Request.Method.POST, Php.MODIFICA_FOTO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("response").equals("true")) {
                        sessionManager.setProfilePic(jsonObject.optString("nomeFile"));
                        Glide.with(PrivatoSitterFragment.this).load(sessionManager.getProfilePic()).into(profilePic);
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

    private void goEditable() {
        if (!edit) {
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
     * Interfaccia di comunicazione tra fragment e activity
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(UtenteSitter sitter);
    }


    /**
     * Task asincrono per il caricamento della foto
     */
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
