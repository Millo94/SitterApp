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

    public Notice(String family,String date,String start_time, String end_time, String description){
        this.family = family;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.description = description;
    }
    public Notice(String id , String family,String date,String start_time, String end_time, String description){
        this.idAnnuncio = id;
        this.family = family;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.description = description;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
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
}
