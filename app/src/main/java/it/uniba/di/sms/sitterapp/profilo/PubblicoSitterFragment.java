package it.uniba.di.sms.sitterapp.profilo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stfalcon.chatkit.commons.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import it.uniba.di.sms.sitterapp.chat.ChatConversationActivity;
import it.uniba.di.sms.sitterapp.oggetti.UtenteSitter;
import it.uniba.di.sms.sitterapp.recensioni.RecensioniPubblicoActivity;

/**
 * FRAGMENT PROFILO PUBBLICO SITTER
 */
public class PubblicoSitterFragment extends Fragment {

    private final String TAG = "PubblicoSitterFragment";

    CheckBox MON1, MON2, MON3,
            TUE1, TUE2, TUE3,
            WED1, WED2, WED3,
            THU1, THU2, THU3,
            FRI1, FRI2, FRI3,
            SAT1, SAT2, SAT3,
            SUN1, SUN2, SUN3;

    View view;
    RatingBar ratingPuSitter;
    ImageView profilePic;
    TextView nomeCompletoPuSit, descrPuSit, emailPuSit, numeroPuSit, carPuSit, sessoPuSit, dataPuSit, tariffaPuSit, ingaggiPuSit, nazionePuSit, cittaPuSit;
    TextView emailPuSit2, numeroPuSit2, carPuSit2, sessoPuSit2, dataPuSit2, tariffaPuSit2, ingaggiPuSit2, nazionePuSit2, cittaPuSit2;
    Button contattaSitter, feedbackSit, disponibilitaSitter;
    SessionManager sessionManager;
    //per salvare mail e telefono del contatto
    private String telefono;
    private String email;

    //uid per creare una chat
    private String sitterUid;


    RequestQueue requestQueue;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private OnFragmentInteractionListener mListener;

    //PERMESSI
    private int CALL_PERMISSION = 1;
    private int SEND_SMS_PEMISSION = 2;

    ImageLoader imageLoader;


    public PubblicoSitterFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profilo_pubblico_sitter, container, false);
        //requestQueue = Volley.newRequestQueue(getContext());
        inizializzazione();
        mostraProfilo(getActivity().getIntent().getStringExtra("uid"));
        sessionManager = new SessionManager(getContext());

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(final ImageView imageView, String url, Object payload) {
                //modifico il link per la foto
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                storageReference.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(getActivity())
                                        .load(uri == null ? R.drawable.ic_account_circle_black_56dp : uri)
                                        .into(imageView);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, e.getMessage());
                            }
                        });
            }
        };

        return view;
    }

    private void getRatingSitter(final String uid){

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

                    ratingPuSitter.setRating(sumRating/i);

                }else{
                    Toast.makeText(getContext(), R.string.genericError ,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void mostraProfilo(final String uid){

        db.collection("utente")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        sitterUid = uid;
                        imageLoader.loadImage((ImageView)getActivity().findViewById(R.id.imagePuSitter),documentSnapshot.getString("Avatar"),null);
                        nomeCompletoPuSit.setText(documentSnapshot.getString("NomeCompleto"));
                        emailPuSit2.setText((documentSnapshot.getString("Email")));
                        email = documentSnapshot.getString("Email");
                        numeroPuSit2.setText(documentSnapshot.getString("Telefono"));
                        telefono = documentSnapshot.getString("Telefono");
                        nazionePuSit2.setText(documentSnapshot.getString("Nazione"));
                        cittaPuSit2.setText(documentSnapshot.getString("Citta"));
                        getRatingSitter(uid);
                        tariffaPuSit2.setText(documentSnapshot.getString("babysitter.Retribuzione"));
                        sessoPuSit2.setText(documentSnapshot.getString("babysitter.Genere"));
                        carPuSit2.setText(documentSnapshot.getBoolean("babysitter.Auto")?"Sì":"No");
                        ingaggiPuSit2.setText(documentSnapshot.get("babysitter.numLavori").toString());
                        descrPuSit.setText(documentSnapshot.getString("Descrizione"));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), R.string.profileError, Toast.LENGTH_SHORT).show();
                    }
                });

    }


    //inizializzazione dei campi del profilo
    public void inizializzazione() {

        nomeCompletoPuSit = (TextView) view.findViewById(R.id.nomeCompletoPuSitter);

        profilePic = (ImageView) view.findViewById(R.id.imagePuSitter);

        descrPuSit = (TextView) view.findViewById(R.id.descrizionePuSitter);

        ratingPuSitter = (RatingBar) view.findViewById(R.id.ratingPuSitter);
        ratingPuSitter.setEnabled(false);

        emailPuSit = (TextView) view.findViewById(R.id.emailPuSitter);
        emailPuSit2 = (TextView) view.findViewById(R.id.emailPuSitter2);

        numeroPuSit = (TextView) view.findViewById(R.id.telefonoPuSitter);
        numeroPuSit2 = (TextView) view.findViewById(R.id.telefonoPuSitter2);

        carPuSit = (TextView) view.findViewById(R.id.autoPuSitter);
        carPuSit2 = (TextView) view.findViewById(R.id.autoPuSitter2);

        sessoPuSit = (TextView) view.findViewById(R.id.sessoPuSitter);
        sessoPuSit2 = (TextView) view.findViewById(R.id.sessoPuSitter2);

        dataPuSit = (TextView) view.findViewById(R.id.nascitaPuSitter);
        dataPuSit2 = (TextView) view.findViewById(R.id.nascitaPuSitter2);

        tariffaPuSit = (TextView) view.findViewById(R.id.tariffaPuSitter);
        tariffaPuSit2 = (TextView) view.findViewById(R.id.tariffaPuSitter2);

        ingaggiPuSit = (TextView) view.findViewById(R.id.ingaggiPuSitter);
        ingaggiPuSit2 = (TextView) view.findViewById(R.id.ingaggiPuSitter2);

        nazionePuSit = (TextView) view.findViewById(R.id.nazionePuSitter);
        nazionePuSit2 = (TextView) view.findViewById(R.id.nazionePuSitter2);

        cittaPuSit = (TextView) view.findViewById(R.id.cittaPuSitter);
        cittaPuSit2 = (TextView) view.findViewById(R.id.cittaPuSitter2);

        disponibilitaSitter = (Button) view.findViewById(R.id.vediDispSitter);
        disponibilitaSitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DisponibilitaDialog dialog = DisponibilitaDialog.newInstance(sitterUid);
                dialog.show(getChildFragmentManager(), "dialog");

            }
        });

        //dialog per la scelta per contattare l'altro utente
        contattaSitter = (Button) view.findViewById(R.id.contattaSitter);
        contattaSitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence servizi[] = new CharSequence[]{getString(R.string.chiamata), getString(R.string.email), getString(R.string.sms), getString(R.string.chat)};

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
                            case 3:

                                db.collection("chat")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                List<DocumentSnapshot> documentsList = querySnapshot.getDocuments();
                                                String code = "NEWUID";
                                                Intent chatConversationIntent = new Intent(getActivity(), ChatConversationActivity.class);
                                                chatConversationIntent.putExtra("conversationName", nomeCompletoPuSit.getText().toString());
                                                chatConversationIntent.putExtra("senderId",sessionManager.getSessionUid());
                                                chatConversationIntent.putExtra("receiverId", sitterUid);
                                                for(DocumentSnapshot docSnap : documentsList){
                                                    List<String> users = (List<String>)docSnap.get("UsersList");
                                                    if(users.contains(sessionManager.getSessionUid()) && users.contains(sitterUid));
                                                    code = docSnap.getId();
                                                }
                                                chatConversationIntent.putExtra("conversationUID",code);
                                                startActivity(chatConversationIntent);

                                            }
                                        });
                                break;
                            default:
                                break;
                        }

                    }
                });

                builder.show();


            }
        });
        feedbackSit = (Button) view.findViewById(R.id.feedbackSitter);
        feedbackSit.setOnClickListener(new View.OnClickListener() {
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


    //per gestire l'intent del permesso
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
        void onFragmentInteraction(UtenteSitter sitter);
    }
}
