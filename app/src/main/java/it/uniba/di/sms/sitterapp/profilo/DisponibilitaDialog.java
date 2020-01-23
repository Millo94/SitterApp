package it.uniba.di.sms.sitterapp.profilo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;

/**
 * Dialog per la disponibilità della baby sitter
 */
public class DisponibilitaDialog extends AppCompatDialogFragment {

    CheckBox MON1, MON2, MON3,
            TUE1, TUE2, TUE3,
            WED1, WED2, WED3,
            THU1, THU2, THU3,
            FRI1, FRI2, FRI3,
            SAT1, SAT2, SAT3,
            SUN1, SUN2, SUN3;
    ArrayList<CheckBox> checkBoxArrayList;
    ArrayList<Integer> checkedBox;


    Button saveChanges;

    SessionManager sessionManager;
    RequestQueue requestQueue;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //session manager
        sessionManager = new SessionManager(getActivity().getApplicationContext());

        //coda per la volley
        requestQueue = Volley.newRequestQueue(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.disponibilita_sitter_dialog, null);
        inizializzaCheckbox(view);
        saveChanges = (Button) view.findViewById(R.id.saveDispBtn);

        //creazione del dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle(R.string.DisponibilitaSitter)
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        //se è una baby sitter non ci deve essere il bottone della disponibilità
        if (sessionManager.getSessionType() == Constants.TYPE_FAMILY) {
            disableClick();
            saveChanges.setVisibility(View.GONE);
        } else {
            saveChanges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveAvailability();
                    dismiss();
                }
            });
        }

        loadAvailability();

        load(getArguments().getString("username"));
        return builder.create();
    }

    //creazione della nuova istanza del dialog
    public static DisponibilitaDialog newInstance(String username) {

        Bundle args = new Bundle();
        args.putString("username", username);

        DisponibilitaDialog fragment = new DisponibilitaDialog();
        fragment.setArguments(args);
        return fragment;
    }

    //inizializzazione delle checkbox
    private void inizializzaCheckbox(View v) {

        checkBoxArrayList = new ArrayList<>();

        MON1 = (CheckBox) v.findViewById(R.id.MON1);
        checkBoxArrayList.add(MON1);
        MON2 = (CheckBox) v.findViewById(R.id.MON2);
        checkBoxArrayList.add(MON2);
        MON3 = (CheckBox) v.findViewById(R.id.MON3);
        checkBoxArrayList.add(MON3);
        TUE1 = (CheckBox) v.findViewById(R.id.TUE1);
        checkBoxArrayList.add(TUE1);
        TUE2 = (CheckBox) v.findViewById(R.id.TUE2);
        checkBoxArrayList.add(TUE2);
        TUE3 = (CheckBox) v.findViewById(R.id.TUE3);
        checkBoxArrayList.add(TUE3);
        WED1 = (CheckBox) v.findViewById(R.id.WED1);
        checkBoxArrayList.add(WED1);
        WED2 = (CheckBox) v.findViewById(R.id.WED2);
        checkBoxArrayList.add(WED2);
        WED3 = (CheckBox) v.findViewById(R.id.WED3);
        checkBoxArrayList.add(WED3);
        THU1 = (CheckBox) v.findViewById(R.id.THU1);
        checkBoxArrayList.add(THU1);
        THU2 = (CheckBox) v.findViewById(R.id.THU2);
        checkBoxArrayList.add(THU2);
        THU3 = (CheckBox) v.findViewById(R.id.THU3);
        checkBoxArrayList.add(THU3);
        FRI1 = (CheckBox) v.findViewById(R.id.FRI1);
        checkBoxArrayList.add(FRI1);
        FRI2 = (CheckBox) v.findViewById(R.id.FRI2);
        checkBoxArrayList.add(FRI2);
        FRI3 = (CheckBox) v.findViewById(R.id.FRI3);
        checkBoxArrayList.add(FRI3);
        SAT1 = (CheckBox) v.findViewById(R.id.SAT1);
        checkBoxArrayList.add(SAT1);
        SAT2 = (CheckBox) v.findViewById(R.id.SAT2);
        checkBoxArrayList.add(SAT2);
        SAT3 = (CheckBox) v.findViewById(R.id.SAT3);
        checkBoxArrayList.add(SAT3);
        SUN1 = (CheckBox) v.findViewById(R.id.SUN1);
        checkBoxArrayList.add(SUN1);
        SUN2 = (CheckBox) v.findViewById(R.id.SUN2);
        checkBoxArrayList.add(SUN2);
        SUN3 = (CheckBox) v.findViewById(R.id.SUN3);
        checkBoxArrayList.add(SUN3);
    }

    private void disableClick() {
        for (CheckBox box : checkBoxArrayList)
            box.setClickable(false);
    }


    private void saveAvailability(){

        DocumentReference docRef = db.collection("utente")
                .document(sessionManager.getSessionUid());

        checkedBox = new ArrayList<>();
        for(int checked = 0; checked<checkBoxArrayList.size(); checked++){
            if(checkBoxArrayList.get(checked).isChecked())
                checkedBox.add(checked + 1);
        }

        docRef.update("babysitter.Disponibilita", checkedBox)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        //View view = inflater.inflate(R.id.fragment_profilo_privato_sitter, null);
                        Snackbar.make(getActivity().findViewById(R.id.profiloPrivatoSitter), R.string.modifySuccess, Snackbar.LENGTH_SHORT)
                                .show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Snackbar.make(getView(), "fail", Snackbar.LENGTH_SHORT);
                    }
                });

    }

    private void loadAvailability(){


        db.collection("utente")
                .document(sessionManager.getSessionUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            ArrayList<Long> checkedBoxList = new ArrayList<>();
                            checkedBoxList.addAll((ArrayList<Long>) documentSnapshot.get("babysitter.Disponibilita"));
                            for(Long i : checkedBoxList){
                                checkBoxArrayList.get(i.intValue() - 1).setChecked(true);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    //volley per la richiesta della disponibilità
    private void save() {

        StringRequest save = new StringRequest(Request.Method.POST, Php.DISPONIBILITA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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

                // Popolo l'array delle checkbox selezionate
                checkedBox = new ArrayList<>();
                for (int checked = 0; checked < checkBoxArrayList.size(); checked++) {
                    if (checkBoxArrayList.get(checked).isChecked())
                        checkedBox.add(checked + 1);
                }

                // Creo l'oggetto json da inviare come parametro
                JSONArray JSONChecked = new JSONArray(checkedBox);

                params.put("operation", "save");
                params.put("username", sessionManager.getSessionUsername());
                params.put("checkedArray", JSONChecked.toString());
                return params;
            }
        };

        requestQueue.add(save);
    }

    //caricamento della disponibilità
    private void load(final String babysitter) {

        StringRequest load = new StringRequest(Request.Method.POST, Php.DISPONIBILITA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int fascia = jsonObject.getInt("fascia");
                        checkBoxArrayList.get(fascia - 1).setChecked(true);
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
                params.put("operation", "load");
                params.put("username", babysitter);
                return params;
            }
        };

        requestQueue.add(load);
    }

    //barra di caricamento
    private class AsyncCaller extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(getActivity());

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tSaving...");
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //save();
            //saveAvailability();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Toast.makeText(getContext(), R.string.modifySuccess, Toast.LENGTH_SHORT).show();
            pdLoading.dismiss();
        }

    }
}
