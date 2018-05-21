package it.uniba.di.sms.sitterapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by Enrico on 20/05/2018.
 */

public class SessionManager {

    /**
     * Oggetto che conterrà tutte le preferenze salvate
     */
    SharedPreferences preferences;

    /**
     * Editor delle preferenze
     */
    SharedPreferences.Editor editor;

    /**
     * Oggetto che sarà valorizzato in ogni occasione col contesto corrente
     */
    Context myContext;

    /**
     * File che conterrà le preferenze
     */
    private static final String PREF_NAME = "SitterAppPref";

    /**
     * Valori chiave
     */
    public static final String IS_LOGGED = "IsLoggedIn";
    public static final String USERNAME = "username";

    /**
     * Valore per acquisire le preferenze in modalità privata
     */
    final int PRIVATE_MODE = 0;

    /**
     * Costruttore
     */
    public SessionManager(Context context){
        this.myContext = context;

        // Associo alle preferenze legate al contesto corrente
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        // Associo l'editor delle preferenze alla variabile di classe
        editor = preferences.edit();
    }

    /**
     * Funzione che salva i dati nel file di sessione
     */
    public void createLoginSession(String username){
        editor.putBoolean(IS_LOGGED, true);
        editor.putString(USERNAME, username);
        editor.commit();
    }

    /**
     * Funzione che restituisce l'username di sessione
     * @return username
     */
    public String getSessionUsername(){
        return preferences.getString(USERNAME, null);
    }

    /**
     * Controlla se l'utente è loggato o no. Se non lo è, riporta l'utente alla schermata di Login.
     * Altrimenti non fa niente.
     */
    public boolean checkLogin(){

        if(!preferences.getBoolean(IS_LOGGED, false)){
            Intent login = new Intent(myContext, LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// Inizio di una nuova "storia" d'uso
            myContext.startActivity(login);
            return true;
        }

        return false;
    }

    /**
     * Funzione di logout. Pulisce tutti i dati di sessione e reindirizza l'utente al login.
     */
    public void logout(){

        editor.clear();
        editor.commit();

        Intent login = new Intent(myContext, LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);// Inizio di una nuova "storia" d'uso
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myContext.startActivity(login);
    }

}
