package it.uniba.di.sms.sitterapp.profilo;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.oggetti.Message;
import it.uniba.di.sms.sitterapp.oggetti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.SessionManager;

/**
 * PROFILO PRIVATO FAMIGLIA
 */
public class PrivatoFamigliaFragment extends Fragment {


    View view;
    ImageView profilePic;
    TextView emailPrFam, nazionePrFam, cittaPrFam, numFigliPrFam, telPrFam;
    EditText nomeCompletoPrFam,descrPrFam, emailPrFam2, nazionePrFam2, cittaPrFam2, telPrFam2, numFigliPrFam2;
    Switch animaliPrFam2;
    RatingBar ratingPrFam;
    Button modificaProfilo,exit_button;
    boolean edit = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    RequestQueue requestQueue;
    SessionManager sessionManager;

    private OnFragmentInteractionListener mListener;

    public PrivatoFamigliaFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profilo_privato_family, container, false);
        //session manager
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        //coda per la richiesta della volley
        requestQueue = Volley.newRequestQueue(getContext());
        inizializzazione();
        //openProfile();
        apriProfilo();

        //setRatingDb();

        exit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sessionManager.logout();

            }
        });
        return view;
    }

    private void getRatingFamily(final String uid){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("recensione").whereEqualTo("receiver", uid)
            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    float sumRating = 0;
                    int i = 0;
                    for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot){

                        sumRating += queryDocumentSnapshot.getDouble("rating").floatValue();
                        i++;

                    }

                    ratingPrFam.setRating(sumRating/i);
                    setRatingDb();

                }else{
                    Toast.makeText(getContext(), R.string.genericError ,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setRatingDb(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("utente")
                .document(sessionManager.getSessionUid())
                .update("famiglia.rating", ratingPrFam.getRating())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void apriProfilo(){

        db.collection("utente")
                .document(sessionManager.getSessionUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        showImage(documentSnapshot.getString("Avatar"));
                        nomeCompletoPrFam.setText(documentSnapshot.getString("NomeCompleto"));
                        descrPrFam.setText(documentSnapshot.getString("Descrizione"));
                        emailPrFam2.setText(documentSnapshot.getString("Email"));
                        telPrFam2.setText(documentSnapshot.getString("Telefono"));
                        animaliPrFam2.setChecked(documentSnapshot.getBoolean("famiglia.Animali"));
                        numFigliPrFam2.setText(documentSnapshot.getString("famiglia.numFigli"));
                        nazionePrFam2.setText(documentSnapshot.getString("Nazione"));
                        cittaPrFam2.setText(documentSnapshot.getString("Citta"));
                        getRatingFamily(sessionManager.getSessionUid());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), R.string.profileError ,Toast.LENGTH_SHORT).show();
                    }
                });

    }


    //per modificare i dati
    public View.OnClickListener goEditable = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!edit) {

                nomeCompletoPrFam.setEnabled(true);
                descrPrFam.setEnabled(true);
                animaliPrFam2.setEnabled(true);
                cittaPrFam2.setEnabled(true);
                numFigliPrFam2.setEnabled(true);
                telPrFam2.setEnabled(true);

                edit = true;

            } else {

                nomeCompletoPrFam.setEnabled(false);
                descrPrFam.setEnabled(false);
                animaliPrFam2.setEnabled(false);
                cittaPrFam2.setEnabled(false);
                numFigliPrFam2.setEnabled(false);
                telPrFam2.setEnabled(false);
                modifica();

                edit = false;
            }
        }
    };

    private void modifica(){

        DocumentReference docRef = db.collection("utente")
                .document(sessionManager.getSessionUid());

        docRef.update("famiglia.numFigli", numFigliPrFam2.getText().toString(),
                "Nazione", nazionePrFam2.getText().toString(),
                "Descrizione", descrPrFam.getText().toString(),
                "Citta", cittaPrFam2.getText().toString(),
                "Email", emailPrFam2.getText().toString(),
                "Telefono", telPrFam2.getText().toString(),
                "famiglia.Animali", animaliPrFam2.isChecked(),
                "NomeCompleto", nomeCompletoPrFam.getText().toString()
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.modifySuccess,Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.genericError,Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void showImage(final String pathfoto){
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(pathfoto);
        storageRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getContext())
                                .load(uri)
                                .into(profilePic);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void inizializzazione() {

        nomeCompletoPrFam = (EditText) view.findViewById(R.id.nomeCompletoPrFamiglia);
        nomeCompletoPrFam.setEnabled(false);

        descrPrFam = (EditText) view.findViewById(R.id.descrizionePrFamiglia);
        descrPrFam.setEnabled(false);

        ratingPrFam = (RatingBar) view.findViewById(R.id.ratingPrFamiglia);
        ratingPrFam.setEnabled(false);

        emailPrFam = (TextView) view.findViewById(R.id.emailPrFamiglia);
        emailPrFam2 = (EditText) view.findViewById(R.id.emailPrFamiglia2);
        emailPrFam2.setEnabled(false);

        nazionePrFam = (TextView) view.findViewById(R.id.nazionePrFamiglia);
        nazionePrFam2 = (EditText) view.findViewById(R.id.nazionePrFamiglia2);
        nazionePrFam2.setEnabled(false);

        cittaPrFam = (TextView) view.findViewById(R.id.cittaPrFamiglia);
        cittaPrFam2 = (EditText) view.findViewById(R.id.cittaPrFamiglia2);
        cittaPrFam2.setEnabled(false);

        numFigliPrFam = (TextView) view.findViewById(R.id.figliPrFamiglia);
        numFigliPrFam2 = (EditText) view.findViewById(R.id.figliPrFamiglia2);
        numFigliPrFam2.setEnabled(false);

        telPrFam = (TextView) view.findViewById(R.id.telefonoPrFamiglia);
        telPrFam2 = (EditText) view.findViewById(R.id.telefonoPrFamiglia2);
        telPrFam2.setEnabled(false);

        animaliPrFam2 = (Switch) view.findViewById(R.id.animaliPrFamiglia);
        animaliPrFam2.setEnabled(false);

        modificaProfilo = (Button) view.findViewById(R.id.togglePrFamiglia);
        modificaProfilo.setOnClickListener(goEditable);

        exit_button = view.findViewById(R.id.exit_button);

        profilePic = (ImageView) view.findViewById(R.id.imgPrFamily);

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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(UtenteFamiglia famiglia);
    }
}
