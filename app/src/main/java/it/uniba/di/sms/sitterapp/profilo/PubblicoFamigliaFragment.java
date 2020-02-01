package it.uniba.di.sms.sitterapp.profilo;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stfalcon.chatkit.commons.ImageLoader;

import android.content.Intent;

import java.util.List;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import it.uniba.di.sms.sitterapp.chat.ChatConversationActivity;
import it.uniba.di.sms.sitterapp.oggetti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.recensioni.RecensioniPubblicoActivity;
import it.uniba.di.sms.sitterapp.Constants.FirebaseDb;

/**
 * FRAGMENT PROFILO PUBBLICO FAMIGLIA
 */
public class PubblicoFamigliaFragment extends Fragment {

    private final String TAG = "PubSitterFrag";
    View view;
    TextView nomeCompletoPuFam, descrPuFam, emailPuFam, numeroPuFam, nazionePuFam, cittaPuFam, numFigliPuFam, animaliPuFam;
    //STRINGHE DA COLLEGARE AL DATABASE
    TextView emailPuFam2, numeroPuFam2, nazionePuFam2, cittaPuFam2, numFigliPuFam2, animaliPuFam2;
    Button contattaFamiglia;
    Button feedbackFam;
    RatingBar ratingPuFam;
    ImageLoader imageLoader;

    SessionManager sessionManager;

    RequestQueue requestQueue;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //numero di telefono
    String telefono;
    //EMAIL ANNUNCIO
    String email;

    //uid per creare una chat
    private String receiverId;

    private OnFragmentInteractionListener mListener;

    //PERMESSI
    private int CALL_PERMISSION = 1;
    private int SEND_SMS_PEMISSION = 2;

    public PubblicoFamigliaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profilo_pubblico_family, container, false);
        sessionManager = new SessionManager(getContext());
        requestQueue = Volley.newRequestQueue(getContext());
        inizializzazione();

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

        mostraProfilo(getActivity().getIntent().getStringExtra("uid"));
        return view;
    }


    private void mostraProfilo(final String uid){

        db.collection(FirebaseDb.USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        receiverId = documentSnapshot.getId();
                        imageLoader.loadImage((ImageView)getActivity().findViewById(R.id.imagePuFamiglia),documentSnapshot.getString(FirebaseDb.USER_AVATAR),null);
                        nomeCompletoPuFam.setText(documentSnapshot.getString(FirebaseDb.USER_NOME_COMPLETO));
                        emailPuFam2.setText((documentSnapshot.getString(FirebaseDb.USER_EMAIL)));
                        email = documentSnapshot.getString(FirebaseDb.USER_EMAIL);
                        numeroPuFam2.setText(documentSnapshot.getString(FirebaseDb.USER_TELEFONO));
                        telefono = documentSnapshot.getString(FirebaseDb.USER_TELEFONO);
                        nazionePuFam2.setText(documentSnapshot.getString(FirebaseDb.USER_NAZIONE));
                        cittaPuFam2.setText(documentSnapshot.getString(FirebaseDb.USER_CITTA));
                        ratingPuFam.setRating(documentSnapshot.getDouble(FirebaseDb.FAMIGLIA+"."+FirebaseDb.FAMIGLIA_RATING).floatValue());
                        animaliPuFam2.setText(documentSnapshot.getBoolean(FirebaseDb.FAMIGLIA+"."+FirebaseDb.FAMIGLIA_ANIMALI)?R.string.yes:R.string.no);
                        numFigliPuFam2.setText(documentSnapshot.getString(FirebaseDb.FAMIGLIA+"."+FirebaseDb.FAMIGLIA_NUMFIGLI));
                        descrPuFam.setText(documentSnapshot.getString(FirebaseDb.USER_DESCRIZIONE));

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

        nomeCompletoPuFam = (TextView) view.findViewById(R.id.nomeCompletoPuFamiglia);

        descrPuFam = (TextView) view.findViewById(R.id.descrizionePuFamiglia);

        ratingPuFam = (RatingBar) view.findViewById(R.id.ratingPuFamiglia);
        ratingPuFam.setEnabled(false);

        emailPuFam = (TextView) view.findViewById(R.id.emailPuFamiglia);
        emailPuFam2 = (TextView) view.findViewById(R.id.emailPuFamiglia2);

        numeroPuFam = (TextView) view.findViewById(R.id.telefonoPuFamiglia);
        numeroPuFam2 = (TextView) view.findViewById(R.id.telefonoPuFamiglia2);

        nazionePuFam = (TextView) view.findViewById(R.id.nazionePuFamiglia);
        nazionePuFam2 = (TextView) view.findViewById(R.id.nazionePuFamiglia2);

        cittaPuFam = (TextView) view.findViewById(R.id.cittaPuFamily);
        cittaPuFam2 = (TextView) view.findViewById(R.id.cittaPuFamily2);


        numFigliPuFam = (TextView) view.findViewById(R.id.figliPuFamiglia);
        numFigliPuFam2 = (TextView) view.findViewById(R.id.figliPuFamiglia2);

        animaliPuFam = (TextView) view.findViewById(R.id.animaliPuFamiglia);
        animaliPuFam2 = (TextView) view.findViewById(R.id.animaliPuFamiglia2);

        //dialog per la scelta per contattare la famiglia
        contattaFamiglia = (Button) view.findViewById(R.id.contattaFamiglia);
        contattaFamiglia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CharSequence servizi[] = new CharSequence[]{getString(R.string.chiamata), getString(R.string.email), getString(R.string.sms), getString(R.string.chat)};

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle(R.string.sceltaAzione);
                builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
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
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection(FirebaseDb.CHAT)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                List<DocumentSnapshot> documentsList = querySnapshot.getDocuments();
                                                String code = "NEWUID";
                                                Intent chatConversationIntent = new Intent(getActivity(), ChatConversationActivity.class);
                                                chatConversationIntent.putExtra("conversationName", nomeCompletoPuFam.getText().toString());
                                                chatConversationIntent.putExtra("senderId",sessionManager.getSessionUid());
                                                chatConversationIntent.putExtra("receiverId", receiverId);
                                                for(DocumentSnapshot docSnap : documentsList){
                                                    List<String> users = (List<String>)docSnap.get(FirebaseDb.CHAT_USERSLIST);
                                                    if(users.contains(sessionManager.getSessionUid()) & users.contains(receiverId)){
                                                        code = docSnap.getId();
                                                    }
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


        feedbackFam = (Button) view.findViewById(R.id.FeedbackFamiglia);
        feedbackFam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showfeedIntent = new Intent(getContext(), RecensioniPubblicoActivity.class);
                showfeedIntent.putExtra("uid", getActivity().getIntent().getStringExtra("uid"));
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
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, CALL_PERMISSION);
                            }
                        })
                        .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
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
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("smsto:" + telefono));
            intent.putExtra("sms_body", getString(R.string.sms_testo));
            startActivity(intent);

        } else {


            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS)) {
                new AlertDialog.Builder(getContext())
                        .setTitle(R.string.permessoRichiesto)
                        .setMessage(R.string.stringPermissionRequest)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PEMISSION);
                            }
                        })
                        .setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
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


    //gestisce il risultato dell'intent dei permessi
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
        void onFragmentInteraction(UtenteFamiglia family);

    }


}
