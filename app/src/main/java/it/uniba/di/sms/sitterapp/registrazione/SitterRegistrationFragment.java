package it.uniba.di.sms.sitterapp.registrazione;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
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
import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Pattern;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.oggetti.UtenteSitter;
import it.uniba.di.sms.sitterapp.Constants.FirebaseDb;

import static android.app.Activity.RESULT_OK;


public class SitterRegistrationFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    ArrayList<EditText> listaET = new ArrayList<>();
    View view;

    final String STOCK_PATH_PHOTO = "gs://sitterapp-223aa.appspot.com/img/stock_img/placeholder-profile-sq.jpg";

    //NomeCompleto, Email, Telefono, Nazione, Citta, dataNascita,
    EditText nomeCompletoET, passwordET, confermaPasswordET, emailET, numeroET, dataNascitaET, cittaET, retribuzioneET;
    //Avatar
    ImageView imgProfile;
    String pathFoto = STOCK_PATH_PHOTO;
    RadioGroup genereRG;
    Switch autoSW;
    Button confRegistration;
    //Rating al momento della registrazione, valore null/vuoto
    float rating ;
    String genere = "";
    //Automobile
    Boolean auto = false;
    Spinner nazioni;
    String arraypaesi[];
    private static final int CAMERA_PIC_REQUEST = 22;
    Uri selectedImage;
    Bitmap sImage;
    private int imageChanged = 0;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;

    //Creazione delle referenze per lo storage di firebase
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();



    private OnFragmentInteractionListener mListener;

    public SitterRegistrationFragment() {
    }

    // Creazione della stringa dalla data scelta con il DatePicker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dataNascitaET.setText(String.format("%02d-%02d-&04d",dayOfMonth,month+1,year));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_registration_sitter, container, false);

        // Generatore dello spinner di scelta della nazione
        nazioni = (Spinner) view.findViewById(R.id.spinnerSitterReg);
        arraypaesi = getResources().getStringArray(R.array.paesi);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.paesi, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nazioni.setAdapter(adapter);

        // Inizializzazione del layout
        initialization();

        // Creazione del Date Picker
        Calendar c = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));

        // Visualizzazione del date picker al click del campo
        dataNascitaET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        // Gestione della scelta del genere
        genereRG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.maleSitterReg)
                    genere = "M";
                else if (checkedId == R.id.femaleSitterReg)
                    genere = "F";
            }
        });

        // Gestione dello switch auto
        autoSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    auto = false;
                else
                    auto = true;
            }
        });



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



        // Click listener di conferma registrazione
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

                    UtenteSitter sitter = new UtenteSitter("",
                            nomeCompletoET.getText().toString(),
                            pathFoto,
                            emailET.getText().toString(),
                            passwordET.getText().toString(),
                            nazioni.getSelectedItem().toString(),
                            cittaET.getText().toString(),
                            numeroET.getText().toString(),
                            true,
                            "",
                            dataNascitaET.getText().toString(),
                            genere,
                            0,
                            0,
                            auto,
                            retribuzioneET.getText().toString());
                    mListener.onFragmentInteraction(sitter);
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
                    Glide.with(SitterRegistrationFragment.this.getContext())
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
                    Glide.with(SitterRegistrationFragment.this.getContext())
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



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must override method");
        }
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

    //inizializzazione delle view
    public void initialization() {

        imgProfile = (ImageView) view.findViewById(R.id.profilePictureSitter);
        nomeCompletoET = (EditText) view.findViewById(R.id.nomeCompletoSitterReg);
        listaET.add(nomeCompletoET);
        passwordET = (EditText) view.findViewById(R.id.passwordSitterReg);
        listaET.add(passwordET);
        confermaPasswordET = (EditText) view.findViewById(R.id.confPasswordSitterReg);
        listaET.add(confermaPasswordET);
        emailET = (EditText) view.findViewById(R.id.emailSitterReg);
        listaET.add(emailET);
        numeroET = (EditText) view.findViewById(R.id.phoneSitterReg);
        listaET.add(numeroET);
        dataNascitaET = (EditText) view.findViewById(R.id.nascitaSitterReg);
        listaET.add(dataNascitaET);
        genereRG = (RadioGroup) view.findViewById(R.id.groupGenderSitterReg);
        cittaET = (EditText) view.findViewById(R.id.CittaSitterReg);
        listaET.add(cittaET);
        retribuzioneET = (EditText) view.findViewById(R.id.retribuzioneSitterReg);
        listaET.add(retribuzioneET);
        autoSW = (Switch) view.findViewById(R.id.Auto);
        confRegistration = (Button) view.findViewById(R.id.buttonReg);

    }

    //controllo che le password siano uguali
    private boolean confermaPassword(String password, String conferma) {
        return password.equals(conferma);
    }

    //controllo il formato della mail
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

        if (genere.equals(""))
            empty = true;

        return empty;
    }




}
