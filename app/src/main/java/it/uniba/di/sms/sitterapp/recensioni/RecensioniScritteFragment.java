package it.uniba.di.sms.sitterapp.recensioni;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import it.uniba.di.sms.sitterapp.adapter.RecensioniAdapter;
import it.uniba.di.sms.sitterapp.oggetti.Recensione;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import tr.xip.errorview.ErrorView;


public class RecensioniScritteFragment extends Fragment {


    private RecyclerView recycler;
    private RecensioniAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Recensione> recensioneList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private SessionManager sessionManager;
    ErrorView errorView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_home, container, false);

        sessionManager = new SessionManager(getActivity().getApplicationContext());

        recycler = (RecyclerView) view.findViewById(R.id.recyclerHome);
        recycler.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());

        recycler.setLayoutManager(layoutManager);

        recensioneList = new ArrayList<>();
        adapter = new RecensioniAdapter(recensioneList);
        recycler.setAdapter(adapter);

        errorView = (ErrorView) view.findViewById(R.id.errorView);
        //caricaReview();
        ReviewScritte();

        return view;
    }


    private void ReviewScritte() {


        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.dismiss();


        db.collection("recensione")
                .whereEqualTo("sender", sessionManager.getSessionUsername())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Iterator<QueryDocumentSnapshot> iterableReview = queryDocumentSnapshots.iterator();
                        while(iterableReview.hasNext()){
                            DocumentSnapshot documentSnapshot = iterableReview.next();
                            Recensione recensione = documentSnapshot.toObject(Recensione.class);
                            recensioneList.add(recensione);
                        }

                        adapter.notifyDataSetChanged();

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO Sostituire "Errore" con la stringa di errore di riferimento.
                        //Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }



}

