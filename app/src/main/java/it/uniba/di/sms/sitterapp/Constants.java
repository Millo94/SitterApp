package it.uniba.di.sms.sitterapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Enrico Ladisa
 *
 * Classe contenente tutte le costanti utilizzate nel progetto
 */

public class Constants {

    /**
     * Codice per il tipo Famiglia
     */
    public static final int TYPE_FAMILY = 0;

    /**
     * Codice per il tipo Babysitter
     */
    public static final int TYPE_SITTER = 1;

    /**
     * Stringa per l'extra di tipo type negli intent
     */
    public static final String TYPE = "type";

    /**
     * Url di base per accesso agli script
     */
    public static final String BASE_URL = "http://sitterapp.altervista.org/";

    /**
     * Funzione per convertire il formato data da SQL a normale
     * @param sql data sql
     * @return data normale
     */
    public static String SQLtoDate (String sql){

        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date data = format.parse(sql);
            format.applyPattern("dd-MM-yyyy");
            result = format.format(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static String dateToSQL (String date){

        String result = "";
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date data = format.parse(date);
            format.applyPattern("yyyy-MM-dd");
            result = format.format(data);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }
}
