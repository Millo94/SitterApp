package it.uniba.di.sms.sitterapp.profilo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import it.uniba.di.sms.sitterapp.Constants.Constants;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import it.uniba.di.sms.sitterapp.Constants.FirebaseDb;

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
                    Toast.makeText(v.getContext(), R.string.modifySuccess, Toast.LENGTH_SHORT);
                    dismiss();
                }
            });
        }

        loadAvailability(getArguments().getString("sitterUid"));

        //load(getArguments().getString("username"));
        return builder.create();
    }

    //creazione della nuova istanza del dialog
    public static DisponibilitaDialog newInstance(String sitterUid) {

        Bundle args = new Bundle();
        args.putString("sitterUid", sitterUid);

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

        DocumentReference docRef = db.collection(FirebaseDb.USERS)
                .document(sessionManager.getSessionUid());

        checkedBox = new ArrayList<>();
        for(int checked = 0; checked<checkBoxArrayList.size(); checked++){
            if(checkBoxArrayList.get(checked).isChecked())
                checkedBox.add(checked + 1);
        }

        docRef.update(FirebaseDb.BABYSITTER+"."+FirebaseDb.BABYSITTER_DISPONIBILITA, checkedBox)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void loadAvailability(final String babysitter){

        db.collection(FirebaseDb.USERS)
                .document(babysitter)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            ArrayList<Long> checkedBoxList = new ArrayList<>();
                            checkedBoxList.addAll((ArrayList<Long>) documentSnapshot.get(FirebaseDb.BABYSITTER+"."+FirebaseDb.BABYSITTER_DISPONIBILITA));
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
}
