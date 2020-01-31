package it.uniba.di.sms.sitterapp.recensioni;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import it.uniba.di.sms.sitterapp.adapter.RecensioniRicevuteAdapter;
import it.uniba.di.sms.sitterapp.oggetti.Recensione;
import tr.xip.errorview.ErrorView;


public class RecensioniRicevuteFragment extends Fragment {


    private RecyclerView recycler;
    private RecensioniRicevuteAdapter adapter;
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
        adapter = new RecensioniRicevuteAdapter(recensioneList);
        recycler.setAdapter(adapter);

        errorView = (ErrorView) view.findViewById(R.id.errorViewContent);


        ReviewRicevute();

        return view;
    }


    private void ReviewRicevute() {


        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        progressDialog.dismiss();


        db.collection("recensione")
                .whereEqualTo("receiver", sessionManager.getSessionUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots.isEmpty()) {
                            errorView.setTitle(R.string.niente_recensioni_ricevute);
                            errorView.setVisibility(View.VISIBLE);

                        } else {
                            errorView.setVisibility(View.GONE);
                            Iterator<QueryDocumentSnapshot> iterableReview = queryDocumentSnapshots.iterator();
                            while(iterableReview.hasNext()){
                                final DocumentSnapshot docSnap = iterableReview.next();
                                db.collection("utente").document(docSnap.getString("sender"))
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Recensione recensione = docSnap.toObject(Recensione.class);
                                                recensione.setSender(documentSnapshot.getString("NomeCompleto"));
                                                recensioneList.add(recensione);
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                            }
                        }

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

}



