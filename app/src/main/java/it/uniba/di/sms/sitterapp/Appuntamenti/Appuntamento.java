package it.uniba.di.sms.sitterapp.Appuntamenti;

/**
 * Created by Feder on 21/05/2018.
 */

public class Appuntamento {
    private String family;
    private String date;
    private String start_time;
    private String end_time;
    private String description;

    public Appuntamento(String family, String date, String start_time, String end_time, String description) {
        this.family = family;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
        this.description = description;
    }
    public Appuntamento(String family, String date, String start_time, String end_time) {
        this.family = family;
        this.date = date;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public String getFamily() {
        return family;
    }

    public String getDate() {
        return date;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getDescription() {
        return description;
    }
}
