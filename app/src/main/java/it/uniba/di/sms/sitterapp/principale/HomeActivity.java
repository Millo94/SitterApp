package it.uniba.di.sms.sitterapp.principale;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.adapter.NoticeAdapter;
import it.uniba.di.sms.sitterapp.adapter.SitterAdapter;
import it.uniba.di.sms.sitterapp.appuntamenti.DialogsNoticeDetails;
import it.uniba.di.sms.sitterapp.oggetti.Notice;
import it.uniba.di.sms.sitterapp.oggetti.UtenteSitter;
import it.uniba.di.sms.sitterapp.profilo.ProfiloPubblicoActivity;
import tr.xip.errorview.ErrorView;

/**
 * Activity per la Home
 */
public class HomeActivity extends DrawerActivity
        implements NoticeAdapter.NoticeAdapterListener, SitterAdapter.ContactsSitterAdapterListener, DialogFiltro.DialogListener {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG = "HomeActivity";
    // Vista
    private RecyclerView recyclerView;

    //Items babysitter
    private List<Notice> noticeList;
    private NoticeAdapter noticeAdapter;

    //Items family
    private List<UtenteSitter> sitterList;
    private List<UtenteSitter> filteredSitterList; //per la lista dopo aver applicato un filtro

    private SitterAdapter sitterAdapter;

    FloatingActionButton cercaSitter;

    //richieste
    private static final String annunci = "ANNUNCI";

    // Filtro disponibilità
    public Map<String, ArrayList<Long>> dispTotali;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispTotali = new HashMap<>();
        //FAB per la ricerca delle baby sitter
        cercaSitter = (FloatingActionButton) findViewById(R.id.cercaSitter);
        if (sessionManager.getSessionType() == Constants.TYPE_FAMILY && cercaSitter.getVisibility() == View.GONE) {
            cercaSitter.show();
            cercaSitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //ricerca
                    DialogFiltro dialogFiltro = DialogFiltro.newInstance();
                    dialogFiltro.show(getSupportFragmentManager(), "dialog");
                }
            });
        } else if (sessionManager.getSessionType() == Constants.TYPE_SITTER && cercaSitter.getVisibility() == View.VISIBLE) {
            cercaSitter.hide();
        }

        //CARICAMENTO DEGLI ANNUNCI
        recyclerView = (RecyclerView) findViewById(R.id.recyclerHome);

        if (sessionManager.checkLogin()) {
            loadHome();
        } else {
            loadDemo();
        }



    }

    //Carica la home in fase di Demo
    private void loadDemo() {
        int type = getIntent().getIntExtra(Constants.TYPE, -1);

        if (type == Constants.TYPE_SITTER) {

            noticeList = new ArrayList<>();
            noticeList.add(new Notice("1", "Ladisa","23-06-2018", "17.00", "20.00", "Ho bisogno di qualcuno che badi ai miei figli mentre faccio la spesa."));
            noticeList.add(new Notice("2", "Luprano", "24-06-2018", "07.30", "12.30", "Cerco babysitter che badi a mio figlio durante il mio turno di lavoro."));
            noticeList.add(new Notice("3", "Deperte", "25-06-2018", "13.00", "16.00", "Cercasi babysitter per i miei due figli."));
            noticeList.add(new Notice("4", "Angarano", "25-06-2018", "19.00", "22.00", "Ho bisogno di una babysitter che prepari la cena per mia figlia"));
            noticeList.add(new Notice("5", "Cuccovillo", "26-06-2018", "18.00", "21.00", "Cercasi tata per i miei tre figli."));
            noticeList.add(new Notice("6", "Loiacono", "27-06-2018", "09.00", "13.00", "La mia tata è impegnata, e ho bisogno di una sostituta per un giorno."));
            noticeAdapter = new NoticeAdapter(HomeActivity.this, noticeList, HomeActivity.this);

            recyclerView.setAdapter(noticeAdapter);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

        } else if (type == Constants.TYPE_FAMILY) {

            sitterList = new ArrayList<>();
            sitterList.add(new UtenteSitter("","Francesca", "gs://sitterapp-223aa.appspot.com/img/user_img/demo_user_img/francesca.jpg",true, new Float(2.5), 4));
            sitterList.add(new UtenteSitter("","Gabriella", "gs://sitterapp-223aa.appspot.com/img/user_img/demo_user_img/gabriella.jpg", true,4, 3));
            sitterList.add(new UtenteSitter("","Gianluca", "gs://sitterapp-223aa.appspot.com/img/user_img/demo_user_img/gianluca.jpg", true,new Float(3.5), 7));
            sitterList.add(new UtenteSitter("","Davide", "gs://sitterapp-223aa.appspot.com/img/user_img/demo_user_img/davide.jpg", true,3, 5));
            sitterList.add(new UtenteSitter("","Monica", "gs://sitterapp-223aa.appspot.com/img/user_img/demo_user_img/monica.jpg", true,2, 4));
            sitterList.add(new UtenteSitter("","Giorgia", "gs://sitterapp-223aa.appspot.com/img/user_img/demo_user_img/giorgia.jpg", true,5, 7));
            sitterAdapter = new SitterAdapter(HomeActivity.this, sitterList, HomeActivity.this);

            recyclerView.setAdapter(sitterAdapter);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

        }
    }

    //Carica la home se è stato effettuato l'accesso
    private void loadHome() {
        if ((sessionManager.getSessionType() == Constants.TYPE_SITTER)) {

            noticeList = new ArrayList<>();
            noticeAdapter = new NoticeAdapter(HomeActivity.this, noticeList, HomeActivity.this);

            recyclerView.setAdapter(noticeAdapter);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            caricaAnnunci();


        } else if (sessionManager.getSessionType() == Constants.TYPE_FAMILY) {

            sitterList = new ArrayList<>();
            sitterAdapter = new SitterAdapter(HomeActivity.this, sitterList, HomeActivity.this);

            recyclerView.setAdapter(sitterAdapter);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            caricaSitter();

        }
    }

    /**
     * Caricamento degli annunci sulla home per le babysitter
     */

    private void caricaAnnunci(){

        CollectionReference colRef = db.collection("annuncio");
        colRef
                .whereEqualTo("conferma", false)
                .orderBy("date")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            Log.i(TAG,e.getMessage());
                            Toast.makeText(HomeActivity.this, R.string.genericError, Toast.LENGTH_SHORT).show();
                        }

                        else {
                            noticeList.clear();
                        Iterator<QueryDocumentSnapshot> iterableNotice = queryDocumentSnapshots.iterator();
                         ErrorView errorView = (ErrorView) findViewById(R.id.errorView);

                            if (!iterableNotice.hasNext()) {
                                errorView.setTitle(R.string.niente_annunci);
                                errorView.setVisibility(View.VISIBLE);

                            } else {
                                errorView.setVisibility(View.INVISIBLE);
                                while (iterableNotice.hasNext()) {
                                    DocumentSnapshot documentSnapshot = iterableNotice.next();
                                    Notice notice = documentSnapshot.toObject(Notice.class);
                                    //mostra solo gli ingaggi ancora disponibili alla candidatura
                                    if (noticeAdapter.annuncioScaduto(notice) == false && !notice.containsCandidatura(sessionManager.getSessionUid())) {
                                        noticeList.add(notice);
                                    }
                                }
                                if(noticeList.size()==0)errorView.setVisibility(View.VISIBLE);
                                noticeAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

    }


    /**
     * Caricamento delle babysitter nella home
     */

    private void caricaSitter(){

        CollectionReference colRef = db.collection("utente");
        /**
        colRef
                .whereEqualTo("tipoUtente", Constants.TYPE_SITTER)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(e != null){
                            Log.i(TAG,e.getMessage());
                            Toast.makeText(HomeActivity.this, R.string.genericError, Toast.LENGTH_SHORT).show();

                        }else {
                            sitterList.clear();
                            Iterator<QueryDocumentSnapshot> iterableSitter = queryDocumentSnapshots.iterator();
                            ErrorView errorView = (ErrorView) findViewById(R.id.errorView);
                            if (!iterableSitter.hasNext()) {
                                errorView.setTitle(R.string.niente_sitter);
                                errorView.setVisibility(View.VISIBLE);
                            } else {
                            errorView.setVisibility(View.INVISIBLE);
                            while (iterableSitter.hasNext()) {
                                DocumentSnapshot documentSnapshot = iterableSitter.next();
                                UtenteSitter bs = new UtenteSitter(
                                        documentSnapshot.getId(),
                                        (String) documentSnapshot.get("NomeCompleto"),
                                        (String) documentSnapshot.get("Avatar"),
                                        documentSnapshot.getBoolean("online"),
                                        Float.valueOf(documentSnapshot.get("babysitter.Rating").toString()),
                                        Integer.valueOf(documentSnapshot.get("babysitter.numLavori").toString()));

                                sitterList.add(bs);
                            }
                            sitterAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
        */
        colRef
                .whereEqualTo("tipoUtente", Constants.TYPE_SITTER)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            sitterList.clear();
                            Iterator<QueryDocumentSnapshot> querySnapshotIterator = querySnapshot.iterator();
                            ErrorView errorView = (ErrorView) findViewById(R.id.errorView);
                            if (!querySnapshotIterator.hasNext()) {
                                errorView.setTitle(R.string.niente_sitter);
                                errorView.setVisibility(View.VISIBLE);
                            } else {
                                errorView.setVisibility(View.INVISIBLE);
                                while (querySnapshotIterator.hasNext()) {
                                    DocumentSnapshot documentSnapshot = querySnapshotIterator.next();
                                    UtenteSitter bs = new UtenteSitter(
                                            documentSnapshot.getId(),
                                            (String) documentSnapshot.get("NomeCompleto"),
                                            (String) documentSnapshot.get("Avatar"),
                                            documentSnapshot.getBoolean("online"),
                                            Float.valueOf(documentSnapshot.get("babysitter.Rating").toString()),
                                            Integer.valueOf(documentSnapshot.get("babysitter.numLavori").toString()));

                                    sitterList.add(bs);
                                    dispTotali.put(documentSnapshot.getId(), new ArrayList<Long>());
                                    loadAvailability(documentSnapshot.getId());
                                }
                                sitterAdapter.notifyDataSetChanged();
                            }
                        }

                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeActivity.this, R.string.genericError, Toast.LENGTH_SHORT).show();
                    }
                });

    }


    //al click su un annuncio visualizza i dettagli
    @Override
    public void onNoticeSelected(Notice notice) {

        if (sessionManager.checkLogin()) {
            DialogsNoticeDetails dialogs = DialogsNoticeDetails.newInstance(notice, sessionManager.getSessionUid());
            dialogs.show(getSupportFragmentManager(), "dialog");
        } else {
            sessionManager.forceLogin(this);
        }

    }

    //al click su un sitter visualizza il profilo della sitter
    @Override
    public void onSitterSelected(UtenteSitter sitter) {

        if (sessionManager.checkLogin()) {
            Intent detailIntent = new Intent(HomeActivity.this, ProfiloPubblicoActivity.class);
            detailIntent.putExtra(Constants.TYPE, Constants.TYPE_SITTER);
            detailIntent.putExtra("uid", sitter.getId());
            startActivity(detailIntent);
        } else {
            sessionManager.forceLogin(this);
        }

    }

    //filtro sulle baby sitter
    @Override
    public void settaFiltro(ArrayList<Integer> checkedBox, float rating, int minLavori) {

        filteredSitterList = new ArrayList<>();
        filteredSitterList.addAll(sitterList);
        ArrayList<UtenteSitter> removeList = new ArrayList<>();

        for (UtenteSitter sitter : filteredSitterList) {

            // CHECK SUL MINLAVORI
            if (sitter.getNumLavori() <= minLavori) {
                removeList.add(sitter);
                continue;
            }

            // CHECK SUL RATING
            if (sitter.getRating() < rating) {
                removeList.add(sitter);
                continue;
            }


            // CHECK SULLA DISPONIBILITA
            for (Integer checked : checkedBox) {
                if (!dispTotali.get(sitter.getId()).contains(checked.longValue())) {
                    removeList.add(sitter);
                    break;
                }
            }
        }

        filteredSitterList.removeAll(removeList);
        sitterAdapter.updateSitterList(filteredSitterList);
        sitterAdapter.notifyDataSetChanged();

        ErrorView errorView = (ErrorView) findViewById(R.id.errorView);
        if (filteredSitterList.size() == 0) {
            errorView.setTitle(R.string.niente_sitter);
            errorView.setVisibility(View.VISIBLE);

        } else {
            errorView.setVisibility(View.GONE);
        }
    }

    private void loadAvailability(final String babysitter){

        db.collection("utente")
                .document(babysitter)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.get("babysitter.Disponibilita") != null){
                                dispTotali.get(babysitter).addAll((ArrayList<Long>) documentSnapshot.get("babysitter.Disponibilita"));
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    //volley per la disponibilità delle sitter
    private void getDisponibilita(final String username) {

        StringRequest load = new StringRequest(Request.Method.POST, Php.DISPONIBILITA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int fascia = jsonObject.getInt("fascia");
                       // dispTotali.get(username).add(fascia);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("operation", "load");
                params.put("username", username);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(load);
    }
}