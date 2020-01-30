package it.uniba.di.sms.sitterapp.principale;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import it.uniba.di.sms.sitterapp.Constants;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import it.uniba.di.sms.sitterapp.appuntamenti.IngaggiActivity;
import it.uniba.di.sms.sitterapp.chat.ChatActivity;
import it.uniba.di.sms.sitterapp.profilo.ProfiloPrivatoActivity;
import it.uniba.di.sms.sitterapp.recensioni.RecensioniActivity;
import it.uniba.di.sms.sitterapp.scriviRecensione.IngaggiDaRecensireActivity;

/**
 * Questa classe contiene solo il drawer. E' una classe base. Tutte le classi legate al drawer, dovranno estendere
 * questa classe, per avere anche esse il drawer.
 */
public class DrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener, SensorEventListener {

    protected SessionManager sessionManager;
    private static final String SELECTED = "selected";
    BottomNavigationView bottomNavigationView;

    private ViewPager mViewPager;

    private static final int SHAKE_SLOP_TIME_MS = 500;
    float xAccel,yAccel,zAccel;
    float xPreviousAccel,yPreviousAccel,zPreviousAccel;
    private long mShakeTimestamp;
    boolean firstUpdate = true;
    boolean shakeInitiated = false;
    float shakeThreshold  = 15.5f;

    Sensor accelerometer;
    SensorManager sm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_home);


        Intent myIntent =new Intent(DrawerActivity.this,MySensorsService.class);
        startService(myIntent);

//        if (sessionManager.getSessionType() == Constants.TYPE_FAMILY && cercaSitter.getVisibility() == View.GONE) {
//            cercaSitter.show();
//            cercaSitter.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //ricerca
//                    DialogFiltro dialogFiltro = DialogFiltro.newInstance();
//                    dialogFiltro.show(getSupportFragmentManager(), "dialog");
//                }
//            });
//        } else if (sessionManager.getSessionType() == Constants.TYPE_SITTER && cercaSitter.getVisibility() == View.VISIBLE) {
//            cercaSitter.hide();


        sm= (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //BottomNavigationView
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().findItem(getIntent().getIntExtra(SELECTED,R.id.nav_home)).setChecked(true);

        // Valorizzo il session manager
        sessionManager = new SessionManager(getApplicationContext());

    }

/*
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
*/


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (!sessionManager.checkLogin()) {
            sessionManager.forceLogin(this);
        } else {
            if (!item.isChecked()) {

                // Gestisce la navigazione al click del menu
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    //Activity principale
                    Intent home = new Intent(DrawerActivity.this, HomeActivity.class);
                    startActivity(home);

                }else if (id == R.id.nav_recensioni) {
                    //collegamento alla sezione recensioni
                    //Activity in cui si vedono le recensioni fatte/ricevute
                    Intent recensioni = new Intent(DrawerActivity.this, RecensioniActivity.class);
                    startActivity(recensioni);

                }else if (id == R.id.nav_chat) {
                    //collegamento alla sezione recensioni
                    //chat con babysitter/famiglia
                    Intent chat = new Intent(DrawerActivity.this, ChatActivity.class);
                    startActivity(chat);

                } else if (id == R.id.nav_engagements) {

                    //collegamento alla sezione degli appuntamenti
                    //lista degli annunci fatti (nel caso della famiglia) assegnati (nel caso della babysitter)
                    Intent menuIngaggi = new Intent(DrawerActivity.this, IngaggiActivity.class);
                    menuIngaggi.putExtra(SELECTED, id);
                    startActivity(menuIngaggi);

                }else if (id == R.id.nav_account) {
                    //collegamento a profilo privato
                    //porta alle informazioni sul proprio profilo
                    Intent intent = new Intent(DrawerActivity.this, ProfiloPrivatoActivity.class);
                    intent.putExtra(Constants.TYPE, sessionManager.getSessionType());
                    startActivity(intent);

                }
            }
        }

        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //al click sul nome dell'utente si accede al profilo privato
    public void goProfile(View view) {
        Intent intent = new Intent(DrawerActivity.this, ProfiloPrivatoActivity.class);
        intent.putExtra(Constants.TYPE, sessionManager.getSessionType());
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        updateAccelParameters(event.values[0],event.values[1],event.values[2]);
        if(!shakeInitiated){
            shakeInitiated = true;
        }
        else if ((shakeInitiated) && isAccelerationChanged()){
            final long now = System.currentTimeMillis();
            // ignore shake events too close to each other (500ms)
            if (!(mShakeTimestamp + SHAKE_SLOP_TIME_MS > now))
                executeShakeAction();
            mShakeTimestamp = now;

        }
        else if((shakeInitiated) && !isAccelerationChanged()){
            shakeInitiated = false;
        }

    }

    private void updateAccelParameters(float xNewAccel, float yNewAccel, float zNewAccel) {

        if (firstUpdate){
            xPreviousAccel = xNewAccel;
            yPreviousAccel = yNewAccel;
            zPreviousAccel = zNewAccel;
            firstUpdate = false;
        }
        else{
            xPreviousAccel = xAccel;
            yPreviousAccel = yAccel;
            zPreviousAccel = zAccel;

        }

        xAccel = xNewAccel;
        yAccel = yNewAccel;
        zAccel = zNewAccel;

    }

    private boolean isAccelerationChanged() {

        //Detect if Acceleration values are changed. if change is atleast 2 axis, so we can detect the shake motion
        float deltaX = Math.abs(xPreviousAccel - xAccel);
        float deltaY = Math.abs(yPreviousAccel - yAccel);
        float deltaZ = Math.abs(zPreviousAccel - zAccel);

        return (deltaX > shakeThreshold && deltaY > shakeThreshold)
                || (deltaX > shakeThreshold && deltaZ > shakeThreshold)
                || (deltaY > shakeThreshold && deltaZ >shakeThreshold);
    }
    private void executeShakeAction() {

        if(sessionManager.getSessionType() == Constants.TYPE_FAMILY){
            Intent ii = new Intent(this,NewNoticeActivity.class);
            ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(ii);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
