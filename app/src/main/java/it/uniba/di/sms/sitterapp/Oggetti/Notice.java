package it.uniba.di.sms.sitterapp.Oggetti;

import java.sql.Date;
import java.sql.Time;

public class Notice {
    private String family;
    private String date;
    private String start_time;
    private String end_time;
    private String description;
    private String idAnnuncio;
    private String sitter;
    private int assegnato;


    public Notice(String id , String family,String date,String start_time, String end_time, String description,String sitter){
        this.idAnnuncio = id;
        this.family = family;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.description = description;
        this.sitter=sitter;
    }

    public Notice(String idAnnuncio, String famiglia, String data, String oraInizio, String oraFine, String descrizione) {
        this.idAnnuncio = idAnnuncio;
        this.family = famiglia;
        this.date = data;
        this.start_time = oraInizio;
        this.end_time = oraFine;
        this.description = descrizione;
    }
    
    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getFamily() {
        return family;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getIdAnnuncio(){return this.idAnnuncio;}

    public String getSitter() {return sitter;}

    public int getAssegnato(){ return assegnato;}
}
