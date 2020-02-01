package it.uniba.di.sms.sitterapp.registrazione;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Pattern;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.oggetti.UtenteFamiglia;
import it.uniba.di.sms.sitterapp.utils.FirebaseDb;

import static android.app.Activity.RESULT_OK;


public class FamilyRegistrationFragment extends Fragment {


    ArrayList<EditText> listaET = new ArrayList<>();
    View view;
    Spinner nazioni;
    String arraypaesi[];
    String pathFoto = FirebaseDb.STOCK_PATH_PHOTO;
    EditText nomeCompletoET, passwordET, confermaPasswordET, emailET, numeroET, cittaET, numFigliET;
    ImageView imgProfile;
    TextView nazioneET;
    Switch animaliSW;
    Boolean animali = true;
    Button confRegistration;

    Bitmap sImage;
    Uri selectedImage;

    private int imageChanged = 0;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    //Creazione delle referenze per lo storage di firebase
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();


    private OnFragmentInteractionListener mListener;

    public FamilyRegistrationFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_registration_family, container, false);
        // Inizializzazione del layout
        initialization();

        // Generatore dello spinner di scelta della nazione
        nazioni = (Spinner) view.findViewById(R.id.spinnerNationFamiglia);
        arraypaesi = getResources().getStringArray(R.array.paesi);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.paesi, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nazioni.setAdapter(adapter);

        // Gestione del flag animali
        animali = animaliSW.isChecked();

        // Gestione foto/avatar
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final CharSequence optionsPicture[] = new CharSequence[]{getString(R.string.takePic), getString(R.string.uploadImg)};

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle(R.string.choosePic);
                builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setItems(optionsPicture, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        switch(i){
                            case 0:
                                photoPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
                                imageChanged++;
                                break;
                            case 1:
                                galleryPermission(Manifest.permission.READ_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
                                imageChanged++;
                                break;
                        }

                        dialogInterface.dismiss();

                    }

                });

                builder.show();

            }
        });

        //controllo dei campi inseriti
        confRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imageChanged != 0){
                    uploadImage();
                }


                if (isEmpty()) {
                    Toast.makeText(getContext(), R.string.missingFields, Toast.LENGTH_LONG).show();
                } else if (!confermaPassword(passwordET.getText().toString(), confermaPasswordET.getText().toString())) {
                    Toast.makeText(getContext(), R.string.checkPassword, Toast.LENGTH_LONG).show();
                } else if (!checkEmail(emailET.getText().toString())) {
                    Toast.makeText(getContext(), R.string.invalidEmail, Toast.LENGTH_LONG).show();
                } else {
                    UtenteFamiglia famiglia = new UtenteFamiglia("",
                            nomeCompletoET.getText().toString(),
                            pathFoto,
                            emailET.getText().toString(),
                            passwordET.getText().toString(),
                            nazioni.getSelectedItem().toString(),
                            cittaET.getText().toString(),
                            numeroET.getText().toString(),
                            true,
                            "",
                            numFigliET.getText().toString(),
                            animali,
                            0);
                    mListener.onFragmentInteraction(famiglia);
                }
            }
        });

        return view;
    }

    public void photoPermission(String permission, int requestCode){

        if (ContextCompat.checkSelfPermission(getContext(), permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(getActivity(), new String[] { permission }, requestCode);

        }
        else {
            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePicture, 0);
        }

    }

    public void galleryPermission(String permission, int requestCode){

        if(ContextCompat.checkSelfPermission(getContext(), permission)== PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(getActivity(), new String[] {permission}, requestCode);

        }else {
            Intent pickPicture = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPicture, 1);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (requestCode == 0 && resultCode == RESULT_OK) {

                    Bundle bundle = imageReturnedIntent.getExtras();
                    sImage = (Bitmap) bundle.get("data");
                    Glide.with(FamilyRegistrationFragment.this.getContext())
                            .load(sImage)
                            .into(imgProfile);

                }
                break;
            case 1:
                if (requestCode == 1 && resultCode == RESULT_OK) {

                    selectedImage = imageReturnedIntent.getData();
                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Glide.with(FamilyRegistrationFragment.this.getContext())
                            .load(bitmap)
                            .into(imgProfile);

                }
                break;
        }

    }

    //Metodo per convertire la foto da Bitmap a Uri
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //Metodo per caricare l'immagine profilo sullo storage
    private void uploadImage(){

        String randUid = UUID.randomUUID().toString();

        if (selectedImage != null) {

            // Defining the child of storageReference

            storageRef.child(FirebaseDb.LOCAL_USERIMAGE_PATH + randUid)
                    .putFile(selectedImage)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){

                                    // Image uploaded successfully
                                    // Dismiss dialog

                                }
                            })

                    .addOnFailureListener(new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception e){

                            // Error, Image not uploaded

                        }
                    });

        } else {

            storageRef.child(FirebaseDb.LOCAL_USERIMAGE_PATH + randUid)
                    .putFile(getImageUri(getContext(), sImage))
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot){

                                    // Image uploaded successfully
                                    // Dismiss dialog

                                }
                            })

                    .addOnFailureListener(new OnFailureListener(){
                        @Override
                        public void onFailure(@NonNull Exception e){

                            // Error, Image not uploaded

                        }
                    });

        }

        pathFoto = storageRef.child(FirebaseDb.LOCAL_USERIMAGE_PATH + randUid).toString();

    }


    //controllo che l'activity che chiama il fragment implementi OnFragmentInteractionListener
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
        void onFragmentInteraction(UtenteFamiglia famiglia);
    }

    //inizializzazione dei campi
    public void initialization() {

        imgProfile = (ImageView) view.findViewById(R.id.profilePictureFamiglia);
        nomeCompletoET = (EditText) view.findViewById(R.id.nomeCompletoFamiglia);
        listaET.add(nomeCompletoET);
        passwordET = (EditText) view.findViewById(R.id.passwordFamiglia);
        listaET.add(passwordET);
        confermaPasswordET = (EditText) view.findViewById(R.id.confPasswordFamiglia);
        listaET.add(confermaPasswordET);
        emailET = (EditText) view.findViewById(R.id.emailFamiglia);
        listaET.add(emailET);
        numeroET = (EditText) view.findViewById(R.id.telefonoFamiglia);
        listaET.add(numeroET);
        cittaET = (EditText) view.findViewById(R.id.CittaFamiglia);
        listaET.add(cittaET);
        numFigliET = (EditText) view.findViewById(R.id.numeroFigliFamiglia);
        listaET.add(numFigliET);
        nazioneET = (TextView) view.findViewById(R.id.nazioneFamiglia);
        animaliSW = (Switch) view.findViewById(R.id.petSwitchFamiglia);
        confRegistration = (Button) view.findViewById(R.id.confermaButton);

    }

    //controllo campi password e conferma password
    private boolean confermaPassword(String password, String conferma) {
        return password.equals(conferma);
    }

    //controllo la mail
    private boolean checkEmail(String email) {
        Pattern emailPattern = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        return emailPattern.matcher(email).matches();
    }

    //controllo se i campi sono vuoti
    private boolean isEmpty() {

        boolean empty = false;

        for (EditText element : listaET) {
            if (element.getText().toString().equals(""))
                empty = true;
        }

        return empty;
    }

    
}
