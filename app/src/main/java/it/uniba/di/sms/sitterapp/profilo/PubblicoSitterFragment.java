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
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stfalcon.chatkit.commons.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Php;
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

    View view;
    RatingBar ratingPuSitter;
    ImageView profilePic;
    TextView usernamePuSit, descrPuSit, nomePuSit, cognomePuSit, emailPuSit, numeroPuSit, carPuSit, sessoPuSit, dataPuSit, tariffaPuSit, ingaggiPuSit, nazionePuSit, cittaPuSit;
    TextView nomePuSit2, cognomePuSit2, emailPuSit2, numeroPuSit2, carPuSit2, sessoPuSit2, dataPuSit2, tariffaPuSit2, ingaggiPuSit2, nazionePuSit2, cittaPuSit2;
    Button contattaSitter, feedbackSit, disponibilitaSitter;
    SessionManager sessionManager;
    //per salvare mail e telefono del contatto
    private String telefono;
    private String email;

    //uid per creare una chat
    private String receiverId;


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
        requestQueue = Volley.newRequestQueue(getContext());
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


    private void mostraProfilo(final String uid){

        db.collection("utente")
                .document(uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        receiverId = uid;
                        imageLoader.loadImage((ImageView)getActivity().findViewById(R.id.imagePuSitter),documentSnapshot.getString("Avatar"),null);
                        usernamePuSit.setText(documentSnapshot.getString("NomeCompleto"));
                        emailPuSit2.setText((documentSnapshot.getString("Email")));
                        email = documentSnapshot.getString("Email");
                        nomePuSit2.setText(documentSnapshot.getString("Nome"));
                        cognomePuSit2.setText(documentSnapshot.getString("NomeCompleto"));
                        numeroPuSit2.setText(documentSnapshot.getString("Telefono"));
                        telefono = documentSnapshot.getString("Telefono");
                        nazionePuSit2.setText(documentSnapshot.getString("Nazione"));
                        cittaPuSit2.setText(documentSnapshot.getString("Citta"));
                        //TODO rating bar
                        //TODO tariffaoraria
                        sessoPuSit2.setText(documentSnapshot.getString("babysitter.Genere"));
                        carPuSit2.setText(documentSnapshot.getBoolean("babysitter.Auto").toString());
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
    //volley per mostrare il profilo
    private void showProfile(final String uid) {

        StringRequest request = new StringRequest(Request.Method.POST, Php.PROFILO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    String result = json.optString("show");

                    if (result.equals("true")) {
                        Glide.with(getContext()).load(json.getString("pathFoto")).into(profilePic);
                        usernamePuSit.setText(uid);
                        emailPuSit2.setText(json.getString("email"));
                        email = json.getString("email");
                        nomePuSit2.setText(json.getString("nome"));
                        cognomePuSit2.setText(json.getString("cognome"));
                        numeroPuSit2.setText(json.getString("telefono"));
                        telefono = json.getString("telefono");
                        nazionePuSit2.setText(json.getString("nazione"));
                        cittaPuSit2.setText(json.getString("citta"));
                        // Rating bar
                        if (!json.getString("rating").equals("null")) {
                            ratingPuSitter.setRating((float) json.getDouble("rating"));
                        }
                        // Setta numero ingaggi
                        if (!json.getString("ingaggi").equals("null"))
                            ingaggiPuSit2.setText(json.getString("ingaggi"));
                        else
                            ingaggiPuSit2.setText("0");
                        // Setta il genere
                        if (json.getString("genere").equals(("M")))
                            sessoPuSit2.setText("Uomo");
                        else
                            sessoPuSit2.setText("Donna");
                        // Setta l'auto
                        if (json.getString("auto").equals("0"))
                            carPuSit2.setText("Si");
                        else
                            //carPuSit2.setChecked(false);
                            carPuSit2.setText("No");
                        dataPuSit2.setText(Constants.SQLtoDate(json.getString("dataNascita")));
                        // Check tariffa
                        if (!json.getString("tariffaOraria").equals("null"))
                            tariffaPuSit2.setText(json.getString("tariffaOraria"));
                        else
                            tariffaPuSit2.setText(R.string.tariffaAssente);
                        // Check descrizione
                        if (!json.getString("descrizione").equals("null"))
                            descrPuSit.setText(json.getString("descrizione"));
                        else
                            descrPuSit.setText(R.string.descrizioneAssente);
                    } else if (result.equals("false")) {
                        Toast.makeText(getActivity().getApplicationContext(), R.string.profileError, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.profileError, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("operation", "show");
                params.put("type", String.valueOf(Constants.TYPE_SITTER));
                params.put("uid", uid);
                return params;
            }
        };

        requestQueue.add(request);
    }

    //inizializzazione dei campi del profilo
    public void inizializzazione() {

        usernamePuSit = (TextView) view.findViewById(R.id.usernamePuSitter);

        profilePic = (ImageView) view.findViewById(R.id.imagePuSitter);

        descrPuSit = (TextView) view.findViewById(R.id.descrizionePuSitter);

        ratingPuSitter = (RatingBar) view.findViewById(R.id.ratingPuSitter);
        ratingPuSitter.setEnabled(false);

        nomePuSit = (TextView) view.findViewById(R.id.nomePuSitter);
        nomePuSit2 = (TextView) view.findViewById(R.id.nomePuSitter2);

        cognomePuSit = (TextView) view.findViewById(R.id.cognomePuSitter);
        cognomePuSit2 = (TextView) view.findViewById(R.id.cognomePuSitter2);

        emailPuSit = (TextView) view.findViewById(R.id.emailPuSitter);
        emailPuSit2 = (TextView) view.findViewById(R.id.emailPuSitter2);

        numeroPuSit = (TextView) view.findViewById(R.id.telefonoPuSitter);
        numeroPuSit2 = (TextView) view.findViewById(R.id.telefonoPuSitter2);

        cognomePuSit = (TextView) view.findViewById(R.id.cognomePuSitter);
        cognomePuSit2 = (TextView) view.findViewById(R.id.cognomePuSitter2);

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
                DisponibilitaDialog dialog = DisponibilitaDialog.newInstance(usernamePuSit.getText().toString());
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
                                Intent chatConversationIntent = new Intent(getActivity(), ChatConversationActivity.class);
                                chatConversationIntent.putExtra("conversationName",usernamePuSit.getText().toString());
                                chatConversationIntent.putExtra("senderId",sessionManager.getSessionUid());
                                chatConversationIntent.putExtra("receiverId",receiverId);
                                chatConversationIntent.putExtra("conversationUID","NEWUID");
                                startActivity(chatConversationIntent);
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
