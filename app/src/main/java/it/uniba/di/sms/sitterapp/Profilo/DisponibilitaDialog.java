package it.uniba.di.sms.sitterapp.Profilo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Appuntamenti.candidati;
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



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        sessionManager = new SessionManager(getActivity().getApplicationContext());
        requestQueue = Volley.newRequestQueue(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.disponibilita_sitter_dialog, null);
        inizializzaCheckbox(view);
        saveChanges = (Button) view.findViewById(R.id.saveDispBtn);

        builder.setView(view)
                .setTitle(R.string.DisponibilitaSitter)
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        if (sessionManager.getSessionType() == Constants.TYPE_FAMILY) {
            disableClick();
            saveChanges.setVisibility(View.GONE);
        } else {
            saveChanges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AsyncCaller().execute();
                    dismiss();
                }
            });
        }

        load(getArguments().getString("username"));
        return builder.create();
    }

    public static DisponibilitaDialog newInstance(String username) {

        Bundle args = new Bundle();
        args.putString("username", username);

        DisponibilitaDialog fragment = new DisponibilitaDialog();
        fragment.setArguments(args);
        return fragment;
    }

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
            save();
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
