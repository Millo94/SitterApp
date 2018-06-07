package it.uniba.di.sms.sitterapp.Principale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import it.uniba.di.sms.sitterapp.Oggetti.UtenteSitter;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;

/**
 * Created by Feder on 07/06/2018.
 */

public class DialogFiltro  extends AppCompatDialogFragment {
    private DialogListener listener;
    RadioGroup numerolavori;
    RatingBar ratingBar;
    CheckBox MON11, MON22, MON33,
            TUE11, TUE22, TUE33,
            WED11, WED22, WED33,
            THU11, THU22, THU33,
            FRI11, FRI22, FRI33,
            SAT11, SAT22, SAT33,
            SUN11, SUN22, SUN33;
    float rating = 0;
    int lavori = 0;

    ArrayList<CheckBox> checkBoxArrayList;
    ArrayList<Integer> checkedBox;

    SessionManager sessionManager;
    RequestQueue requestQueue;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        requestQueue = Volley.newRequestQueue(getActivity());
        checkBoxArrayList = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.ricerca_sitter, null);
        inizializzaCheckbox(view);
        numerolavori = (RadioGroup) view.findViewById(R.id.radioGroup);
        ratingBar = (RatingBar) view.findViewById(R.id.cerca_stars);

        builder.setView(view)
                .setTitle(R.string.filtro)
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setPositiveButton(R.string.applicFiltro, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedBox = new ArrayList<>();
                for (int checked = 0; checked < checkBoxArrayList.size(); checked++) {
                    if (checkBoxArrayList.get(checked).isChecked())
                        checkedBox.add(checked + 1);
                }
                numerolavori.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.radioButton)
                            lavori = 1;
                        else if (checkedId == R.id.radioButton2)
                            lavori = 2;
                        else if (checkedId == R.id.radioButton3)
                            lavori = 3;
                    }
                });
                        listener.passaggioParamentr(checkedBox,rating,lavori);
            }
        });


        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener= (DialogListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement DialogFiltro");
        }
    }

    public static DialogFiltro newInstance() {
        Bundle args = new Bundle();
        DialogFiltro fragment = new DialogFiltro();
        fragment.setArguments(args);
        return fragment;
    }

    private void inizializzaCheckbox(View v) {

        checkBoxArrayList = new ArrayList<>();

        MON11 = (CheckBox) v.findViewById(R.id.MON11);
        checkBoxArrayList.add(MON11);
        MON22 = (CheckBox) v.findViewById(R.id.MON22);
        checkBoxArrayList.add(MON22);
        MON33 = (CheckBox) v.findViewById(R.id.MON33);
        checkBoxArrayList.add(MON33);
        TUE11 = (CheckBox) v.findViewById(R.id.TUE11);
        checkBoxArrayList.add(TUE11);
        TUE22 = (CheckBox) v.findViewById(R.id.TUE22);
        checkBoxArrayList.add(TUE22);
        TUE33 = (CheckBox) v.findViewById(R.id.TUE33);
        checkBoxArrayList.add(TUE33);
        WED11 = (CheckBox) v.findViewById(R.id.WED11);
        checkBoxArrayList.add(WED11);
        WED22 = (CheckBox) v.findViewById(R.id.WED22);
        checkBoxArrayList.add(WED22);
        WED33 = (CheckBox) v.findViewById(R.id.WED33);
        checkBoxArrayList.add(WED33);
        THU11 = (CheckBox) v.findViewById(R.id.THU11);
        checkBoxArrayList.add(THU11);
        THU22 = (CheckBox) v.findViewById(R.id.THU22);
        checkBoxArrayList.add(THU22);
        THU33 = (CheckBox) v.findViewById(R.id.THU33);
        checkBoxArrayList.add(THU33);
        FRI11 = (CheckBox) v.findViewById(R.id.FRI11);
        checkBoxArrayList.add(FRI11);
        FRI22 = (CheckBox) v.findViewById(R.id.FRI22);
        checkBoxArrayList.add(FRI22);
        FRI33 = (CheckBox) v.findViewById(R.id.FRI33);
        checkBoxArrayList.add(FRI33);
        SAT11 = (CheckBox) v.findViewById(R.id.SAT11);
        checkBoxArrayList.add(SAT11);
        SAT22 = (CheckBox) v.findViewById(R.id.SAT22);
        checkBoxArrayList.add(SAT22);
        SAT33 = (CheckBox) v.findViewById(R.id.SAT33);
        checkBoxArrayList.add(SAT33);
        SUN11 = (CheckBox) v.findViewById(R.id.SUN11);
        checkBoxArrayList.add(SUN11);
        SUN22 = (CheckBox) v.findViewById(R.id.SUN22);
        checkBoxArrayList.add(SUN22);
        SUN33 = (CheckBox) v.findViewById(R.id.SUN33);
        checkBoxArrayList.add(SUN33);
    }

    public interface DialogListener{
        void passaggioParamentr(ArrayList<Integer> listacheck , float rating , int lavori);
    }



}
