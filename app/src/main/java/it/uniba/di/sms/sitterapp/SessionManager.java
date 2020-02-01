package it.uniba.di.sms.sitterapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import it.uniba.di.sms.sitterapp.principale.LoginActivity;

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
    public static final String EMAIL = "email";
    public static final String UID = "uid";
    public static final String NOMECOMPLETO = "nomeCompleto";
    public static final String TYPE = "type";
    public static final String PATHFOTO = "pathfoto";

    /**
     * Valore per acquisire le preferenze in modalità privata
     */
    final int PRIVATE_MODE = 0;

    /**
     * Costruttore
     */
    public SessionManager(Context context) {
        this.myContext = context;

        // Associo alle preferenze legate al contesto corrente
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        // Associo l'editor delle preferenze alla variabile di classe
        editor = preferences.edit();
    }

    /**
     * Funzione che salva i dati nel file di sessione
     */
    public void createLoginSession(String email, String uid, int type, String nomeCompleto) {
        editor.putBoolean(IS_LOGGED, true);
        editor.putString(EMAIL, email);
        editor.putString(UID, uid);
        editor.putString(NOMECOMPLETO, nomeCompleto);
        editor.putInt(TYPE, type);
        editor.commit();
    }

    public void setProfilePic(String pathfoto) {
        editor.putString(PATHFOTO, pathfoto);
        editor.commit();
    }

    /**
     * Funzione che restituisce l'username di sessione
     *
     * @return username
     */
    public String getSessionEmail() {
        return preferences.getString(EMAIL, null);
    }


    /**
     * Aggiunto da Giacomo 09/01/2020
     *
     * Funzione che restituisce l'UID dell'utente
     *
     * @return uid
     */
    public String getSessionUid(){
        return preferences.getString(UID, null);
    }

    /**
     * Aggiunto da Giacomo 09/01/2020
     *
     * Funzione che restituisce il nome completo dell'utente
     *
     * @return uid
     */
    public String getNomeCompleto(){
        return preferences.getString(NOMECOMPLETO, null);
    }

    /**
     * Funzione che resituisce il tipo di sessione
     *
     * @return tipo utente
     */
    public int getSessionType() {
        return preferences.getInt(TYPE, -1);
    }

    /**
     * Funzione che restituisce l'url della foto profilo
     *
     * @return url foto profilo
     */
    public String getProfilePic() {
        return preferences.getString(PATHFOTO, null);
    }

    /**
     * Controlla se l'utente è loggato o no.
     */
    public boolean checkLogin() {

        return preferences.getBoolean(IS_LOGGED, false);
    }

    public void forceLogin(final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.attention);
        builder.setMessage(R.string.loginMessage);
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton(R.string.signIn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent login = new Intent(context, LoginActivity.class);
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// Inizio di una nuova "storia" d'uso
                context.startActivity(login);
            }
        });
        builder.show();
    }

    /**
     * Funzione di logout. Pulisce tutti i dati di sessione e reindirizza l'utente al login.
     */
    public void logout() {

        editor.clear();
        editor.commit();

        Intent login = new Intent(myContext, LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);// Inizio di una nuova "storia" d'uso
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myContext.startActivity(login);
    }

}
